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
package minesweeper;

/**
 * A {@link Board} instance represents the cells in a game.
 * <p>
 * {@link Board} instances are immutable, and knows where the mines are located. They allows to query if a cell has a
 * mine with {@link Board#hasMine(int, int)} and to fetch the amount of mines surrounding a cell with {@link Board#sorroundingMines(int, int)}.
 */
final class Board
{
  private static final int MINE = Integer.MIN_VALUE;
  private static final int UNKNOWN = 9;

  private final int[][] cells;

  private Board(final int[][] cells)
  {
    // we don't need to clone the array because its always comes from a factory method that ensures its not shared.
    this.cells = cells;
  }

  int rows() {return cells.length;}

  int columns() {return cells[0].length;}

  int minesCount()
  {
    final int rows = rows();
    final int columns = columns();
    int detectedMines = 0;

    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        if (cells[row][column] == MINE) {
          detectedMines++;
        }
      }
    }

    return detectedMines;
  }

  int sorroundingMines(final int row, final int column)
  {
    final var cell = get(row, column);
    return (cell == MINE) ? UNKNOWN : cells[row][column];
  }

  boolean hasMine(final int row, final int column) { return get(row, column) == MINE; }

  int get(final int row, final int column)
  {
    if (row < 0) {
      throw new IllegalArgumentException("row must be 0 or positive");
    }
    if (row >= rows()) {
      throw new IllegalArgumentException("row is too big. This board has " + rows() + " rows (and this board access is 0..n-1 indexed)");
    }
    if (column < 0) {
      throw new IllegalArgumentException("column must be 0 or positive");
    }
    if (column >= columns()) {
      throw new IllegalArgumentException("row is too big. This board has " + columns() + " column (and this board access is 0..n-1 indexed)");
    }
    return cells[row][column];
  }

  static Board easy() { return new Builder(Level.EASY).randomlyPlaceMines(Level.EASY).build(); }

  static Board intermediate() { return new Builder(Level.INTERMEDIATE).randomlyPlaceMines(Level.INTERMEDIATE).build(); }

  static Board expert() { return new Builder(Level.EXPERT).randomlyPlaceMines(Level.EXPERT).build(); }

  static Board custom(final int desiredRows, final int desiredColumns, final int desiredMines)
  {
    return new Builder(desiredRows, desiredColumns).randomlyPlaceMines(desiredMines).build();
  }

  enum Level
  {
    EASY(8, 8, 10),
    INTERMEDIATE(16, 16, 40),
    EXPERT(16, 30, 99);

    final int rows, columns, mines;

    Level(final int rows, final int columns, final int mines)
    {
      this.rows = rows;
      this.columns = columns;
      this.mines = mines;
    }
  }

  static final class Builder
  {
    private final int[][] cells;
    private final int rows;
    private final int columns;

    Builder(final Level desiredLevel) { this(desiredLevel.rows, desiredLevel.columns); }

    Builder(final int rows, final int columns)
    {
      if (rows < 1) {
        throw new IllegalArgumentException("Board must have at least 1 row.");
      }
      if (columns < 1) {
        throw new IllegalArgumentException("Board must have at least 1 column.");
      }
      this.rows = rows;
      this.columns = columns;
      this.cells = new int[rows][columns];
    }

    Board build() { return new Board(cells); }

    Builder randomlyPlaceMines(final Level level) { return randomlyPlaceMines(level.mines); }

    Builder randomlyPlaceMines(final int amount)
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
          cells[row][column] = MINE;
          minesToPlace--;
        }
      }

      return this;
    }

    Builder calculateSurroundingMines()
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

    private boolean mineAt(final int row, final int column) { return cells[row][column] == MINE; }

    private boolean isNotAtFirst(final int n) { return (n - 1) != -1; }
  }
}
