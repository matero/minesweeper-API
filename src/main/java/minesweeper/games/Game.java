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

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * They allows to query if a cell has a mine with {@link #hasMine(int, int)} and to fetch the amount of mines surrounding a cell with {@link #sorroundingMines(int, int)}
 */
final class Game
{
  //
  // will avoid bitwise logic to make code clearer
  //
  static final int MINE = 9;

  private static final int UNKNOWN = 12;
  private static final int DISCOVERED_MINE = -10;
  private static final char UNDISCOVERED = '#';
  private static final char MARK = '?';

  @JsonProperty final int id;

  // undiscovered values: all of them represented as '#'
  //   0 (cell with no adjacent mines)
  //   1 (cell with 1 adjacent mine)
  //   2 (cell with 2 adjacent mines)
  //   ...
  //   8 (cell with 8 adjacent mines)
  //   9 MINE
  //
  // discovered safe value: -(undiscovered_value) - 1, represented as 'undiscovered_value'
  //   0 (cell with no adjacent mines) -> -1
  //   1 (cell with 1 adjacent mine) -> -2
  //   2 (cell with 2 adjacent mines) -> -3
  //   ...
  //   8 (cell with 8 adjacent mines) -> -9
  //
  // marked undiscovered value: value + 10, all of them represented as '?'
  //   10 (cell with no adjacent mines)
  //   11 (cell with 1 adjacent mine)
  //   12 (cell with 2 adjacent mines)
  //   ...
  //   18 (cell with 8 adjacent mines)
  //   19 MINE
  //
  // on creation ALL values are between 0..9 -> no cell is known
  @NotNull private final int[][] board;

  Game(final int id, final int[][] board)
  {
    this.id = id;
    this.board = board;
  }

  int sorroundingMines(final int row, final int column)
  {
    final var cell = get(row, column);
    return (cell == MINE) ? UNKNOWN : cell;
  }

  boolean hasMine(final int row, final int column) { return get(row, column) == MINE; }

  int get(final int row, final int column)
  {
    if (row < 0) {
      throw new IllegalArgumentException("row must be 0 or positive");
    }
    if (row >= getRows()) {
      throw new IllegalArgumentException("row is too big. This game has " + getRows() + " rows (and this board access is 0..n-1 indexed)");
    }
    if (column < 0) {
      throw new IllegalArgumentException("column must be 0 or positive");
    }
    if (column >= getColumns()) {
      throw new IllegalArgumentException("row is too big. This game has " + getColumns() + " column (and this board access is 0..n-1 indexed)");
    }
    return board[row][column];
  }

  @JsonProperty char[][] getBoard()
  {
    return hasDiscoveredMine() ? buildBoard(Game::showCell) : buildBoard(Game::translateCell);
  }

  private boolean hasDiscoveredMine()
  {
    final var columns = getColumns();
    final var rows = getRows();
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        if (board[row][column] == DISCOVERED_MINE) {
          return true;
        }
      }
    }
    return false;
  }

  char[][] buildBoard(final IntToCharFunction cellTranslator)
  {
    final var rows = getRows();
    final var columns = getColumns();
    final char[][] cells = new char[rows][];

    for (int row = 0; row < rows; row++) {
      cells[row] = new char[columns];
      for (int column = 0; column < columns; column++) {
        cells[row][column] = cellTranslator.apply(board[row][column]);
      }
    }

    return cells;
  }

  private static char translateCell(final int cell)
  {
    return switch (cell) {
      case -1 -> ' ';
      case -2 -> '1';
      case -3 -> '2';
      case -4 -> '3';
      case -5 -> '4';
      case -6 -> '5';
      case -7 -> '6';
      case -8 -> '7';
      case -9 -> '8';
      case DISCOVERED_MINE -> '*';
      case 0, 1, 2, 3, 4, 5, 6, 7, 8, MINE -> UNDISCOVERED;
      case 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 -> MARK;
      default -> throw new IllegalStateException("unexpected cell value: " + cell);
    };
  }

  private static char showCell(final int cell)
  {
    return switch (cell) {
      case 0, -1, 10 -> ' ';
      case 1, -2, 11 -> '1';
      case 2, -3, 12 -> '2';
      case 3, -4, 13 -> '3';
      case 4, -5, 14 -> '4';
      case 5, -6, 15 -> '5';
      case 6, -7, 16 -> '6';
      case 7, -8, 17 -> '7';
      case 8, -9, 18 -> '8';
      case MINE, DISCOVERED_MINE, 19 -> '*';
      default -> throw new IllegalStateException("unexpected cell value: " + cell);
    };
  }

  @JsonProperty int getRows() { return board.length; }

  @JsonProperty int getColumns() { return board[0].length; }

  @JsonProperty int getMinesCount()
  {
    final int rows = getRows();
    final int columns = getColumns();

    int detectedMines = 0;

    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        final var cell = board[row][column];
        if (cell == MINE || cell == DISCOVERED_MINE) {
          detectedMines++;
        }
      }
    }

    return detectedMines;
  }

  @Override public boolean equals(final Object o) { return (this == o) || (o instanceof Game that && id == that.id); }

  @Override public int hashCode() { return Integer.hashCode(id); }

  @Override public String toString() { return "Game{id=" + id + '}'; }

  String toAsciiTable() { return toAsciiTable(hasDiscoveredMine()); }

  String toAsciiTable(final boolean showCells)
  {
    final var columns = getColumns();
    final var rows = getRows();
    final var table = new StringBuilder(((2 * rows) - 1) * ((2 * columns) - 1));
    final var rowSeparator = buildRowSeparatorUsing(columns);
    final var board = showCells ? buildBoard(Game::showCell) : buildBoard(Game::translateCell);

    addRowTo(table, board[0]);
    for (int row = 1; row < rows; row++) {
      table.append(rowSeparator);
      addRowTo(table, board[row]);
    }
    return table.toString();
  }

  private char[] buildRowSeparatorUsing(final int columns)
  {
    final int length = 2 * columns;
    final var separator = new char[length];
    separator[0] = '-';
    int i = 0;
    while (i < (length - 2)) {
      separator[++i] = '+';
      separator[++i] = '-';
    }
    separator[++i] = '-';
    separator[i] = '\n';
    return separator;
  }

  private void addRowTo(final StringBuilder table, final char[] row)
  {
    table.append(row[0]);
    for (int column = 1; column < row.length; column++) {
      table.append('|').append(row[column]);
    }
    table.append('\n');
  }

  @FunctionalInterface
  private interface IntToCharFunction
  {

    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    char apply(int value);
  }
}
