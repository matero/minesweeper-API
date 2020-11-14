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

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Board_tests
{
  @Test void can_create_easy_boards()
  {
    //given
    final var board = Board.easy();

    //expect
    assertThat(board)
        .isNotNull();
    assertThat(board.rows())
        .isEqualTo(Board.Level.EASY.rows);
    assertThat(board.columns())
        .isEqualTo(Board.Level.EASY.columns);
    assertThat(board.minesCount())
        .isEqualTo(Board.Level.EASY.mines);
  }

  @Test void can_create_intermediate_boards()
  {
    //given
    final var board = Board.intermediate();

    //expect
    assertThat(board)
        .isNotNull();
    assertThat(board.rows())
        .isEqualTo(Board.Level.INTERMEDIATE.rows);
    assertThat(board.columns())
        .isEqualTo(Board.Level.INTERMEDIATE.columns);
    assertThat(board.minesCount())
        .isEqualTo(Board.Level.INTERMEDIATE.mines);
  }

  @Test void can_create_expert_boards()
  {
    //given
    final var board = Board.expert();

    //expect
    assertThat(board)
        .isNotNull();
    assertThat(board.rows())
        .isEqualTo(Board.Level.EXPERT.rows);
    assertThat(board.columns())
        .isEqualTo(Board.Level.EXPERT.columns);
    assertThat(board.minesCount())
        .isEqualTo(Board.Level.EXPERT.mines);
  }

  @Test void can_create_custom_boards()
  {
    //given
    final var desiredRows = 40;
    final var desiredColumns = 50;
    final var desiredMines = 100;
    final var board = Board.custom(desiredRows, desiredColumns, desiredMines);

    //expect
    assertThat(board)
        .isNotNull();
    assertThat(board.rows())
        .isEqualTo(desiredRows);
    assertThat(board.columns())
        .isEqualTo(desiredColumns);
    assertThat(board.minesCount())
        .isEqualTo(desiredMines);
  }
}
