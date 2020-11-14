package minesweeper;

import java.util.Random;

class Board
{
  private static final int MINE = Integer.MIN_VALUE;

  private final int[][] cells;

  private Board(final int[][] cells)
  {
    // we don't need to clone the array because its always comes from a factory method that ensures its not shared.
    this.cells = cells;
  }

  static Board easy() { return new Board(createCells(Level.EASY)); }

  static Board intermediate() { return new Board(createCells(Level.INTERMEDIATE)); }

  static Board expert() { return new Board(createCells(Level.EXPERT)); }

  static int[][] createCells(final Level desiredLevel) { return createCells(desiredLevel.rows, desiredLevel.columns, desiredLevel.mines); }

  static Board custom(final int rows, final int columns, final int mines)
  {
    if (rows < 1) {
      throw new IllegalArgumentException("Board must have at least 1 row.");
    }
    if (columns < 1) {
      throw new IllegalArgumentException("Board must have at least 1 column.");
    }
    if (mines < 0) {
      throw new IllegalArgumentException("Board must have 0 or more mines.");
    }

    final int totalCells = rows * columns;
    if (mines >= totalCells) {
      throw new IllegalArgumentException("Board must have less mines than cells.");
    }

    return new Board(createCells(rows, columns, mines));
  }

  static int[][] createCells(final int rows, final int columns, final int mines)
  {
    final var board = new int[rows][columns];
    randomlyPlaceMinesAt(board, rows, columns, mines);
    calculateSurroundingMinesAt(board, rows, columns);
    return board;
  }

  static void randomlyPlaceMinesAt(final int cells[][], final int rows, final int columns, final int mines)
  {
    {
      final var r = new Random();
      for (int minesToPlace = mines; minesToPlace > 0; ) {
        final int row = r.nextInt(rows);
        final int column = r.nextInt(columns);
        final var cell = cells[row][column];

        if (cell == 0) {
          cells[row][column] = MINE;
          minesToPlace--;
        }
      }
    }
  }

  static void calculateSurroundingMinesAt(final int[][] cells, final int rows, final int columns)
  {
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        if (!mineAt(cells, row, column)) {
          final var isNotAtLastRow = (row + 1) != rows;
          final var isNotAtLastColumn = (column + 1) != columns;
          cells[row][column] = surroundingMinesOf(cells, row, isNotAtLastRow, column, isNotAtLastColumn);
        }
      }
    }
  }

  private static int surroundingMinesOf(final int[][] cells, final int row, final boolean isNotAtLastRow, final int column, final boolean isNotAtLastColumn)
  {
    int surroundingMines = 0;

    if (isNotAtFirst(column)) {
      if (mineAt(cells, row, column - 1)) {
        surroundingMines++;
      }
    }
    if (isNotAtFirst(row)) {
      if (mineAt(cells, row - 1, column)) {
        surroundingMines++;
      }
    }
    if (isNotAtFirst(row) && isNotAtFirst(column)) {
      if (mineAt(cells, row - 1, column - 1)) {
        surroundingMines++;
      }
    }
    if (isNotAtFirst(row) && isNotAtLastColumn) {
      if (mineAt(cells, row - 1, column + 1)) {
        surroundingMines++;
      }
    }
    if (isNotAtLastRow) {
      if (mineAt(cells, row + 1, column)) {
        surroundingMines++;
      }
    }
    if (isNotAtLastColumn) {
      if (mineAt(cells, row, column + 1)) {
        surroundingMines++;
      }
    }
    if (isNotAtLastRow && isNotAtLastColumn) {
      if (mineAt(cells, row + 1, column + 1)) {
        surroundingMines++;
      }
    }
    if (isNotAtLastRow && isNotAtFirst(column)) {
      if (mineAt(cells, row + 1, column - 1)) {
        surroundingMines++;
      }
    }

    return surroundingMines;
  }

  static boolean mineAt(final int[][] cells, final int row, final int column) { return cells[row][column] == MINE; }

  static boolean isNotAtFirst(final int n) { return (n - 1) != -1; }

  private enum Level
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
}
