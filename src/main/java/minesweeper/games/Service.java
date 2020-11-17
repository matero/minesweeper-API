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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
class Service
{
  private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);

  private final Repository repository;

  Service(final Repository repository) { this.repository = repository; }

  @Transactional
  Game createGameOfLevel(final GameLevel level)
  {
    return createCustomGame(level.rows, level.columns, level.mines);
  }

  @Transactional
  Game createCustomGame(final int rows, final int columns, final int mines)
  {
    final var board = new BoardBuilder(rows, columns)
                          .randomlyPlaceMines(mines)
                          .calculateSurroundingMines()
                          .build();
    final int assignedId = repository.createGameWith(board);
    final var game = new Game(assignedId, GameStatus.CREATED, Game.NOT_STARTED, Game.NOT_FINISHED, board);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Game#" + assignedId + " created, with board:\n\n" + game.toAsciiTable());

      LOGGER.debug("\nGame#" + assignedId + " created, with board:\n\n" + game.toAsciiTable(true));
    }
    return game;
  }

  Game reveal(final int gameId, final int row, final int column)
  {
    final var game = repository.findById(gameId);
    if (game == null) {
      throw new NotFound("No Game is defined with id=" + gameId);
    }
    if (game.isFinished()) {
      throw new AlreadyFinished(game);
    }
    final var result = game.reveal(row, column);

    if (noChangeWasPerformedIn(result)) {
      return game;
    }

    repository.update(result);
    return result;
  }

  private boolean noChangeWasPerformedIn(final Game game) { return game == Game.WITHOUT_CHANGES; }
}
