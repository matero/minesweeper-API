package minesweeper;

import java.util.Random;

/**
 * A {@link Board} instance represents the cells in a game.
 * <p>
 * {@link Board} instances are immutable, and knows where the mines are located. They allows to query if a cell has a
 * mine with {@link Board#hasMine(int, int)} and to fetch the amount of mines surrounding a cell with {@link Board#sorroundingMines(int, int)}.
 */
class Board
{
  private static final int MINE = Integer.MIN_VALUE;

  private final int[][] cells;

  private Board(final int[][] cells)
  {
    // we don't need to clone the array because its always comes from a factory method that ensures its not shared.
    this.cells = cells;
  }

  int sorroundingMines(final int row, final int column)
  {
    if (row < 0) {
      throw new IllegalArgumentException("row must be 0 or positive");
    }
    if (row >= cells.length) {
      throw new IllegalArgumentException("row is too big. This board has " + cells.length + " rows (and this board access is 0..n-1 indexed)");
    }
    if (column < 0) {
      throw new IllegalArgumentException("column must be 0 or positive");
    }
    if (column >= cells[0].length) {
      throw new IllegalArgumentException("row is too big. This board has " + cells[0].length + " column (and this board access is 0..n-1 indexed)");
    }
    return (cells[row][column] == MINE)? -1 : cells[row][column];
  }

  boolean hasMine(final int row, final int column)
  {
    if (row < 0) {
      throw new IllegalArgumentException("row must be 0 or positive");
    }
    if (row >= cells.length) {
      throw new IllegalArgumentException("row is too big. This board has " + cells.length + " rows (and this board access is 0..n-1 indexed)");
    }
    if (column < 0) {
      throw new IllegalArgumentException("column must be 0 or positive");
    }
    if (column >= cells[0].length) {
      throw new IllegalArgumentException("row is too big. This board has " + cells[0].length + " column (and this board access is 0..n-1 indexed)");
    }
    return cells[row][column] == MINE;
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
    randomlyPlaceMinesAt(board, mines);
    calculateSurroundingMinesAt(board);
    return board;
  }

  static void randomlyPlaceMinesAt(final int[][] cells, final int mines)
  {
    final var r = new Random();
    final int rows = cells.length;
    final int columns = cells[0].length;
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

  static void calculateSurroundingMinesAt(final int[][] cells)
  {
    final int rows = cells.length;
    final int columns = cells[0].length;

    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        if (!mineAt(cells, row, column)) {
          cells[row][column] = surroundingMinesOf(cells, row, column);
        }
      }
    }
  }

  private static int surroundingMinesOf(final int[][] cells, final int row, final int column)
  {
    final var isNotAtLastRow = (row + 1) != cells.length;
    final var isNotAtLastColumn = (column + 1) != cells[0].length;
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
