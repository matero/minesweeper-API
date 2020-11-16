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
  static final int UNKNOWN = 9;
  private static final int HINT = -2;

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

  @JsonProperty int[][] getBoard() { return board.clone(); }

  @JsonProperty int getRows() { return board.length; }

  @JsonProperty int getColumns() { return board[0].length; }

  @JsonProperty int getMinesCount()
  {
    final int rows = getRows();
    final int columns = getColumns();

    int detectedMines = 0;

    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        if (board[row][column] == Game.MINE) {
          detectedMines++;
        }
      }
    }

    return detectedMines;
  }

  @Override public boolean equals(final Object o) { return (this == o) || (o instanceof Game that && id == that.id); }

  @Override public int hashCode() { return Integer.hashCode(id); }

  @Override public String toString() { return "Game{id=" + id + '}'; }
}
