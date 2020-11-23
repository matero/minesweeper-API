/*
 The MIT License (MIT)

 Copyright (c) 2020 Juan José GIL - matero@gmail.com

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
package minesweeper.games;

import minesweeper.NotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import java.sql.Array;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@org.springframework.stereotype.Repository
class Repository
{
  private static final List<SqlParameter> BOARD_PARAMETER = List.of(new SqlParameter(Types.ARRAY, "board"));

  private final JdbcTemplate db;
  private final PreparedStatementCreatorFactory insertIntoGames;

  @Autowired Repository(final JdbcTemplate db) { this(db, makeInsertIntoGames()); }

  Repository(final JdbcTemplate db, final PreparedStatementCreatorFactory insertIntoGames)
  {
    this.db = db;
    this.insertIntoGames = insertIntoGames;
  }

  Game findById(final int gameId)
  {
    final Object[] id = {gameId};
    final var game = db.query("""
                              SELECT
                                game.status,
                                game.creation,
                                game.finishedAt,
                                game.board,
                                coalesce((
                                  SELECT extract(milliseconds FROM sum(coalesce(play.finishedat, current_timestamp) - play.startedat))
                                  FROM minesweeper.PlayTimes play
                                  WHERE game.id = play.game), 0) as playtimeInMillis
                              FROM minesweeper.Games game
                              WHERE id = ?
                              """, id, (rs, rowNum) -> {
      final var status = readStatus(rs.getString(1));
      final var creation = rs.getObject(2, LocalDateTime.class);
      final var finishedAt = rs.getObject(3, LocalDateTime.class);
      final var board = readBoard(rs.getArray(4));
      final var playTime = Duration.ofMillis(rs.getLong(5));

      return new Game(gameId, status, creation, finishedAt, playTime, board.clone());
    });
    if (game.isEmpty()) {
      throw new NotFound("No Game is defined with id=" + gameId);
    }
    return game.get(0);
  }

  List<Game> findAll()
  {
    return db.query("SELECT id, status, creation, finishedAt, board FROM minesweeper.Games", (rs, rowNum) -> {
      final var id = rs.getInt(1);
      final var status = readStatus(rs.getString(2));
      final var creation = rs.getObject(3, LocalDateTime.class);
      final var finishedAt = rs.getObject(4, LocalDateTime.class);
      final var board = readBoard(rs.getArray(5));
      final var playTime = Duration.ofMillis(100);

      return new Game(id, status, creation, finishedAt, playTime, board.clone());
    });
  }

  int createGameWith(final int[][] board)
  {
    final var gameId = new GeneratedKeyHolder();
    final var psc = insertIntoGames.newPreparedStatementCreator(Collections.singletonList(board));
    db.update(psc, gameId);
    return gameId.getKey().intValue();
  }

  void update(final Game game)
  {
    db.update("""
              UPDATE minesweeper.Games
              SET status=?, finishedAt=?, board=?
              WHERE id=?
              """,
              game.status.name(), game.finishedAt, game.board, game.id);
  }

  private static PreparedStatementCreatorFactory makeInsertIntoGames()
  {
    final PreparedStatementCreatorFactory insertIntoGames;
    insertIntoGames = new PreparedStatementCreatorFactory("INSERT INTO minesweeper.Games(board) VALUES (?)", BOARD_PARAMETER);
    insertIntoGames.setReturnGeneratedKeys(true);
    insertIntoGames.setGeneratedKeysColumnNames("id");
    return insertIntoGames;
  }

  private GameStatus readStatus(final String status)
  {
    if (status == null) {
      throw new IllegalStateException("fetched null status from DB.");
    }
    try {
      return GameStatus.valueOf(status);
    } catch (final IllegalArgumentException unknown) {
      throw new IllegalStateException("Game status at DB is unknown. Value fetched: '" + status + "'.");
    }
  }

  private int[][] readBoard(final Array array) throws SQLException
  {
    if (array == null) {
      throw new IllegalStateException("fetched null board from DB.");
    }
    try {
      return asIntMatrix(array.getArray());
    } finally {
      array.free();
    }
  }

  private int[][] asIntMatrix(final Object value)
  {
    if (value == null) {
      throw new IllegalStateException("fetched null board from DB.");
    }
    if (value instanceof int[][] cells) {
      return cells;
    }
    if (value instanceof Integer[][] cells) {
      final var rows = cells.length;
      final var columns = cells[0].length;
      final var board = new int[cells.length][];

      for (int row = 0; row < rows; row++) {
        board[row] = new int[columns];
        for (int column = 0; column < columns; column++) {
          final var cell = Objects.requireNonNull(cells[row][column], "cells[" + row + "][" + column + "] fetched from DB is null, it shouldn't.");
          board[row][column] = cell;
        }
      }
      return board;
    }
    throw new IllegalStateException("uninterpretable board cells of type '" + value.getClass().getCanonicalName() + "'.");
  }
}
