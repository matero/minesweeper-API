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

import minesweeper.boards.Board;

final class Game
{
  private static final int HINT = Integer.MAX_VALUE;
  private static final int CLICKED = -2;

  private final int id;
  private final int[][] cells;
  private final Board board;

  Game(final int id, final Board board)
  {
    this.id = id;
    this.board = board;
    this.cells = board.makeCells();
  }

  @Override public boolean equals(final Object o) { return (this == o) || (o instanceof Game that && id == that.id); }

  @Override public int hashCode() { return Integer.hashCode(id); }

  @Override public String toString() { return "Game{id=" + id + '}'; }
}
