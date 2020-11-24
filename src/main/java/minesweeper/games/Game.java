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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;

final class Game
{
  //
  // will avoid bitwise logic to make code clearer
  //
  static final int MINE = 9;
  private static final int FLAG = 10;
  private static final int MARKED_MINE = MINE + FLAG;

  private static final char UNDISCOVERED = '#';
  private static final char FLAG_MARK = '?';

  @JsonProperty final int id;
  @JsonProperty final GameStatus status;
  @JsonProperty final LocalDateTime creation;
  @JsonProperty final LocalDateTime finishedAt;
  @JsonProperty final Duration playTime;

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
  @NotNull @JsonIgnore final int[][] board;

  Game(final int id, final GameStatus status, final LocalDateTime creation, final LocalDateTime finishedAt, final Duration playTime, final int[][] board)
  {
    this.id = id;
    this.status = status;
    this.creation = creation;
    this.finishedAt = finishedAt;
    this.playTime = playTime;
    this.board = board;
  }

  int get(final int row, final int column)
  {
    if (row < 0) {
      throw new IllegalArgumentException("row must be 0 or positive");
    }
    if (row >= getRows()) {
      throw new IllegalArgumentException("row is too big. This game has " + getRows() + " rows (and board access is 0..n-1 indexed)");
    }
    if (column < 0) {
      throw new IllegalArgumentException("column must be 0 or positive");
    }
    if (column >= getColumns()) {
      throw new IllegalArgumentException("row is too big. This game has " + getColumns() + " column (and board access is 0..n-1 indexed)");
    }
    return board[row][column];
  }

  @JsonProperty char[][] getBoard()
  {
    return isFinished() ? buildBoard(Game::showCell) : buildBoard(Game::translateCell);
  }

  private char[][] buildBoard(final IntToCharFunction cellTranslator)
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
      case 0, 1, 2, 3, 4, 5, 6, 7, 8, MINE -> UNDISCOVERED;
      case 10, 11, 12, 13, 14, 15, 16, 17, 18, 19 -> FLAG_MARK;
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
      case MINE, 19 -> '*';
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
        if (hasMine(cell)) {
          detectedMines++;
        }
      }
    }

    return detectedMines;
  }

  private boolean hasMine(@NotNull final int cell)
  {
    return cell == MINE || cell == MARKED_MINE;
  }

  @JsonIgnore boolean isFinished() { return status == GameStatus.WON || status == GameStatus.LOOSE; }

  @Override public boolean equals(final Object o) { return (this == o) || (o instanceof Game that && id == that.id); }

  @Override public int hashCode() { return Integer.hashCode(id); }

  @Override public String toString() { return "Game{id=" + id + '}'; }

  String toAsciiTable() { return toAsciiTable(isFinished()); }

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

  GameChange reveal(final int row, final int column)
  {
    if (isFinished()) {
      return GameChange.none();
    }

    final var cell = get(row, column);
    if (isRevealed(cell)) {
      return GameChange.none();
    }

    final var resultBoard = cloneBoard();

    if (hasMine(cell)) {
      return new GameChange(id, GameStatus.LOOSE, resultBoard);
    }

    if (hasAdjacentMines(cell)) {
      resultBoard[row][column] = doReveal(cell);
    }

    if (doesntHaveAdjacentMines(cell)) {
      revealSorroundings(resultBoard, row, column);
    }

    if (allCellsWithoutMinesAreRevealed(resultBoard)) {
      return new GameChange(id, GameStatus.WON, resultBoard);
    } else {
      return new GameChange(id, GameStatus.PLAYING, resultBoard);
    }
  }

  private int doReveal(final int cell)
  {
    if (isFlagged(cell)) {
      return -(cell - 10) - 1;
    } else {
      return -cell - 1;
    }
  }

  private boolean isRevealed(final int cell)
  {
    return cell < 0;
  }

  private boolean allCellsWithoutMinesAreRevealed(final int[][] resultBoard)
  {
    final int rows = getRows();
    final int columns = getColumns();

    int mines = 0;
    int discoverableCells = 0;

    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        final var cell = resultBoard[row][column];
        if (hasMine(cell)) {
          mines++;
          discoverableCells++;
        } else {
          if (isDiscoverable(cell)) {
            discoverableCells++;
          }
        }
      }
    }

    return mines == discoverableCells;
  }

  private boolean isDiscoverable(final int cell) { return cell >= 0; }

  private boolean hasAdjacentMines(final int cell) { return (cell > 0 && cell < MINE) || (cell > 10 && cell < MARKED_MINE); }

  private void revealSorroundings(final int[][] board, final int row, final int column)
  {
    board[row][column] = -1; // we known that board(row, column) is 0 or 10

    final var isNotAtLastRow = (row + 1) != getRows();
    final var isNotAtLastColumn = (column + 1) != getColumns();

    if (isNotAtFirst(column)) {
      revealCell(board, row, column - 1);
    }
    if (isNotAtFirst(row)) {
      revealCell(board, row - 1, column);
    }
    if (isNotAtFirst(row) && isNotAtFirst(column)) {
      revealCell(board, row - 1, column - 1);
    }
    if (isNotAtFirst(row) && isNotAtLastColumn) {
      revealCell(board, row - 1, column + 1);
    }
    if (isNotAtLastRow) {
      revealCell(board, row + 1, column);
    }
    if (isNotAtLastColumn) {
      revealCell(board, row, column + 1);
    }
    if (isNotAtLastRow && isNotAtLastColumn) {
      revealCell(board, row + 1, column + 1);
    }
    if (isNotAtLastRow && isNotAtFirst(column)) {
      revealCell(board, row + 1, column - 1);
    }
  }

  private void revealCell(final int[][] board, final int row, final int column)
  {
    final var cell = board[row][column];
    if (hasAdjacentMines(cell)) {
      board[row][column] = doReveal(cell);
    }
    if (doesntHaveAdjacentMines(cell)) {
      revealSorroundings(board, row, column);
    }
  }

  private boolean doesntHaveAdjacentMines(final int cell)
  {
    return cell == 0 || cell == 10;
  }

  private boolean isNotAtFirst(final int n) { return n != 0; }

  private int[][] cloneBoard()
  {
    final var clonedBoard = board.clone();
    final var columns = getColumns();
    for (int row = 0; row < getRows(); row++) {
      clonedBoard[row] = new int[columns];
      System.arraycopy(board[row], 0, clonedBoard[row], 0, columns);
    }
    return clonedBoard;
  }

  GameChange flag(final int row, final int column)
  {
    if (isFinished()) {
      return GameChange.none();
    }

    final var cell = get(row, column);
    if (isFlagged(cell)) {
      return GameChange.none();
    }
    if (isRevealed(cell)) {
      return GameChange.none();
    }

    final var resultBoard = cloneBoard();
    resultBoard[row][column] = cell + FLAG;
    return new GameChange(id, GameStatus.PLAYING, resultBoard);
  }

  private boolean isFlagged(final int cell) { return cell > MINE; }

  GameChange unflag(final int row, final int column)
  {
    if (isFinished()) {
      return GameChange.none();
    }

    final var cell = get(row, column);
    if (isRevealed(cell)) {
      return GameChange.none();
    }
    if (!isFlagged(cell)) {
      return GameChange.none();
    }

    final var resultBoard = cloneBoard();
    resultBoard[row][column] = cell - FLAG;
    return new GameChange(id, GameStatus.PLAYING, resultBoard);
  }

  boolean canBePaused() { return status == GameStatus.PLAYING; }
}
