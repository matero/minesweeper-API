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
  static final int MINE = -1;
  private static final int DISCOVERED_MINE = MINE + 10;
  private static final int HINT = -2;
  static final int UNKNOWN = -3;

  @JsonProperty final int id;
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

  @JsonProperty int[][] getBoard()
  {
    final var rows = getRows();
    final var columns = getColumns();

    final var played = new int[rows][];
    for (int r = 0; r < rows; r++) {
      played[r] = new int[columns];
      System.arraycopy(board[r], 0, played[r], 0, columns);
    }

    if (!hasDiscoveredMine()) {
      for (int row = 0; row < rows; row++) {
        for (int column = 0; column < columns; column++) {
          final var cell = board[row][column];
          played[row][column] = (cell < DISCOVERED_MINE) ? UNKNOWN : cell;
        }
      }
    }
    return played;
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

  String toAsciiTable(final boolean showBoard)
  {
    final var columns = getColumns();
    final var rows = getRows();
    final var table = new StringBuilder(((2 * rows) - 1) * ((2 * columns) - 1));

    final var rowSeparator = buildRowSeparatorUsing(columns);
    addRowTo(table, 0, columns, showBoard);
    for (int row = 1; row < rows; row++) {
      table.append(rowSeparator);
      addRowTo(table, row, columns, showBoard);
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

  private void addRowTo(final StringBuilder table, final int row, final int columns, final boolean show)
  {
    table.append(cellDescription(row, 0, show));
    for (int column = 1; column < columns; column++) {
      table.append('|').append(cellDescription(row, column, show));
    }
    table.append('\n');
  }

  private char cellDescription(final int row, final int column, final boolean show)
  {
    final int cell = board[row][column];
    if (show) {
      return switch (cell) {
        case MINE, DISCOVERED_MINE -> '*';
        case 0, 10 -> ' ';
        case 1, 11 -> '1';
        case 2, 12 -> '2';
        case 3, 13 -> '3';
        case 4, 14 -> '4';
        case 5, 15 -> '5';
        case 6, 16 -> '6';
        case 7, 17 -> '7';
        case 8, 18 -> '8';
        default -> throw new IllegalStateException("unexpected cell content: " + cell);
      };
    } else {
      if (cell < DISCOVERED_MINE) {
        return '#';
      }
      return switch (cell) {
        case DISCOVERED_MINE -> '*';
        case 10 -> ' ';
        case 11 -> '1';
        case 12 -> '2';
        case 13 -> '3';
        case 14 -> '4';
        case 15 -> '5';
        case 16 -> '6';
        case 17 -> '7';
        case 18 -> '8';
        default -> throw new IllegalStateException("unexpected cell content: " + cell);
      };
    }
  }
}
