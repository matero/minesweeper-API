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

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Validated
@RestController
@RequestMapping(path = "/games", produces = "application/json; charset=utf-8")
class GamesEndPoint
{
  private final Service service;

  GamesEndPoint(final Service service) { this.service = service; }

  /**
   * Creates a {@link Game} for desired level.
   *
   * @param level {@link GameLevel} of the {@link Game} to create.
   * @return a newly created {@link Game}, with all its cells marked as {@link Game#UNKNOWN}.
   */
  @ApiOperation(value = "Creates a Game for desired level.")
  @PostMapping("create/{level}")
  Game create(@ApiParam(value = "level of the Game to create.", required = true, readOnly = true) @PathVariable final GameLevel level)
  {
    return service.createGameOfLevel(level);
  }

  /**
   * Creates a {@link Game} with custom configuration.
   *
   * @param rows    rows of the {@link Game}'s board.
   * @param columns columns of the {@link Game}'s board.
   * @param mines   mines in the {@link Game}'s board.
   * @return a newly created {@link Game}, with all its cells marked as {@link Game#UNKNOWN}.
   */
  @ApiOperation(value = "Creates a Game with custom configuration.")
  @PostMapping("create/custom")
  Game create(
      @ApiParam(value = "rows of the Game's board.", readOnly = true) @RequestParam @NotNull @Positive final Integer rows,
      @ApiParam(value = "columns of the Game's board.", readOnly = true) @RequestParam @NotNull @Positive final Integer columns,
      @ApiParam(value = "mines in the Game's board.", readOnly = true) @RequestParam @NotNull @Positive final Integer mines)
  {
    return service.createCustomGame(rows, columns, mines);
  }
}
