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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@org.springframework.stereotype.Service
class GamesService
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GamesService.class);

  private final GamesRepository repository;

  GamesService(final GamesRepository repository) { this.repository = repository; }

  @Transactional(readOnly = true) List<Game> findAll(final String gameOwner)
  {
    return repository.findAllOf(gameOwner);
  }

  @Transactional Game createGameOfLevel(final String ownerEmail, final GameLevel level)
  {
    return createCustomGame(ownerEmail, level.rows, level.columns, level.mines);
  }

  @Transactional Game createCustomGame(final String ownerEmail, final int rows, final int columns, final int mines)
  {
    final var board = new BoardBuilder(rows, columns)
                          .randomlyPlaceMines(mines)
                          .calculateSurroundingMines()
                          .build();
    final int assignedId = repository.createGameWith(ownerEmail, board);
    final var game = get(assignedId);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Game#" + assignedId + " created, with board:\n\n" + game.toAsciiTable());

      LOGGER.debug("\nGame#" + assignedId + " created, with board:\n\n" + game.toAsciiTable(true));
    }
    return game;
  }

  @Transactional Game reveal(final int gameId, final String gameOwner, final int row, final int column)
  {
    final Game game = getGameWithId(gameId, gameOwner);
    final var cellReveal = game.reveal(row, column);

    if (cellReveal.hasNoChanges()) {
      return game;
    }

    repository.updateGameWith(cellReveal);
    return get(gameId);
  }

  @Transactional Game flag(final int gameId, final String gameOwner, final int row, final int column)
  {
    final Game game = getGameWithId(gameId, gameOwner);
    final var cellFlag = game.flag(row, column);

    if (cellFlag.hasNoChanges()) {
      return game;
    }

    repository.updateGameWith(cellFlag);
    return get(gameId);
  }

  @Transactional Game unflag(final int gameId, final String gameOwner, final int row, final int column)
  {
    final Game game = getGameWithId(gameId, gameOwner);
    final var cellUnflag = game.unflag(row, column);

    if (cellUnflag.hasNoChanges()) {
      return game;
    }

    repository.updateGameWith(cellUnflag);
    return get(gameId);
  }

  Game pause(final int gameId, final String gameOwner)
  {
    final var game = getGameWithId(gameId, gameOwner);
    if (game.canBePaused()) {
      repository.pauseGame(gameId);
      return get(gameId);
    }
    return game;
  }

  private Game getGameWithId(final int gameId, final String gameOwner)
  {
    final var game = get(gameId);
    if (!game.owner.equals(gameOwner)) {
      throw new AccessDeniedException("You don't own this game.");
    }
    if (game.isFinished()) {
      throw new AlreadyFinished(game);
    }
    return game;
  }

  private Game get(final int gameId) { return repository.findById(gameId); }
}
