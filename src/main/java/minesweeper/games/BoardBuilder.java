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

final class BoardBuilder
{
  private final int[][] cells;
  private final int rows;
  private final int columns;

  BoardBuilder(final int rows, final int columns)
  {
    if (rows < 1) {
      throw new IllegalArgumentException("Board must have at least 1 row.");
    }
    if (columns < 1) {
      throw new IllegalArgumentException("Board must have at least 1 column.");
    }
    this.rows = rows;
    this.columns = columns;
    cells = new int[rows][columns];
  }

  int[][] build() { return cells; }

  BoardBuilder randomlyPlaceMines(final GameLevel level) { return randomlyPlaceMines(level.mines); }

  BoardBuilder randomlyPlaceMines(final int amount)
  {
    if (amount < 0) {
      throw new IllegalArgumentException("Board must have 0 or more mines.");
    }
    final int totalCells = rows * columns;
    if (amount >= totalCells) {
      throw new IllegalArgumentException("Board must have less mines than cells.");
    }

    final var r = java.util.concurrent.ThreadLocalRandom.current();
    for (int minesToPlace = amount; minesToPlace > 0; ) {
      final int row = r.nextInt(rows);
      final int column = r.nextInt(columns);
      final var cell = cells[row][column];

      if (cell == 0) {
        cells[row][column] = Game.MINE;
        minesToPlace--;
      }
    }

    return this;
  }

  BoardBuilder calculateSurroundingMines()
  {
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        if (!mineAt(row, column)) {
          cells[row][column] = surroundingMinesOf(row, column);
        }
      }
    }
    return this;
  }

  private int surroundingMinesOf(final int row, final int column)
  {
    final var isNotAtLastRow = (row + 1) != rows;
    final var isNotAtLastColumn = (column + 1) != columns;
    int surroundingMines = 0;

    if (isNotAtFirst(column)) {
      if (mineAt(row, column - 1)) {
        surroundingMines++;
      }
    }
    if (isNotAtFirst(row)) {
      if (mineAt(row - 1, column)) {
        surroundingMines++;
      }
    }
    if (isNotAtFirst(row) && isNotAtFirst(column)) {
      if (mineAt(row - 1, column - 1)) {
        surroundingMines++;
      }
    }
    if (isNotAtFirst(row) && isNotAtLastColumn) {
      if (mineAt(row - 1, column + 1)) {
        surroundingMines++;
      }
    }
    if (isNotAtLastRow) {
      if (mineAt(row + 1, column)) {
        surroundingMines++;
      }
    }
    if (isNotAtLastColumn) {
      if (mineAt(row, column + 1)) {
        surroundingMines++;
      }
    }
    if (isNotAtLastRow && isNotAtLastColumn) {
      if (mineAt(row + 1, column + 1)) {
        surroundingMines++;
      }
    }
    if (isNotAtLastRow && isNotAtFirst(column)) {
      if (mineAt(row + 1, column - 1)) {
        surroundingMines++;
      }
    }

    return surroundingMines;
  }

  private boolean mineAt(final int row, final int column) { return cells[row][column] == Game.MINE; }

  private boolean isNotAtFirst(final int n) { return n != 0; }
}
