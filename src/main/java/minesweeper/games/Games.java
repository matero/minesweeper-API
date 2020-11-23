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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/games", produces = "application/json; charset=utf-8")
class Games
{
  private final Service service;

  Games(final Service service) { this.service = service; }

  /**
   * Gets all the {@link Game}s (being?) played.
   *
   * @return the list of known {@link Game}s, sorted by start time.
   */
  @ApiOperation("Gets all the Games (being?) played, sorted by start time.")
  @GetMapping
  List<Game> index()
  {
    return service.findAll();
  }

  /**
   * Creates a {@link Game} for desired level.
   *
   * @param level {@link GameLevel} of the {@link Game} to create.
   * @return a newly created {@link Game}, with all its cells obfuscated.
   */
  @ApiOperation("Creates a Game for desired level.")
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
   * @return a newly created {@link Game}, with all its cells obfuscated.
   */
  @ApiOperation("Creates a Game with custom configuration.")
  @PostMapping("create/custom")
  Game create(
      @ApiParam(value = "rows of the Game's board.", readOnly = true) @RequestParam @NotNull @Positive final Integer rows,
      @ApiParam(value = "columns of the Game's board.", readOnly = true) @RequestParam @NotNull @Positive final Integer columns,
      @ApiParam(value = "mines in the Game's board.", readOnly = true) @RequestParam @NotNull @Positive final Integer mines)
  {
    return service.createCustomGame(rows, columns, mines);
  }

  /**
   * Reveals a cell in a {@link Game}.
   *
   * @param gameId unique gameId of the {@link Game}.
   * @param row    row of the {@link Game}'s board's cell to reveal.
   * @param column column of the {@link Game}'s board's cell to reveal.
   * @return {@link Game}, cell revealed. If it was a mine game is marked as FINISHED.
   */
  @ApiOperation("""
                Reveals a Game's board cell.
                                        
                If the game wasn't started at the time, then it is marked as PLAYING.
                If the cell was already revealed, nothing happens.
                """)
  @PutMapping("{gameId}/reveal/{row}/{column}")
  Game reveal(
      @ApiParam(value = "gameId of the game on which the cell must be revealed.", readOnly = true) @PathVariable final int gameId,
      @ApiParam(value = "row of the cell to reveal.", readOnly = true) @PositiveOrZero @PathVariable final int row,
      @ApiParam(value = "column of the cell to reveal.", readOnly = true) @PositiveOrZero @PathVariable final int column)
  {
    return service.reveal(gameId, row, column);
  }

  /**
   * Flags a cell in a {@link Game}.
   *
   * @param gameId unique gameId of the {@link Game}.
   * @param row    row of the {@link Game}'s board's cell to flag.
   * @param column column of the {@link Game}'s board's cell to flag.
   * @return {@link Game}, with cell flagged.
   */
  @ApiOperation("""
                Flags a Game's board cell.
                                        
                If the game wasn't started at the time, then it is marked as PLAYING.
                If the cell was already revealed, nothing happens.
                If the cell was already flagged, nothing happens.
                """)
  @PutMapping("{gameId}/flag/{row}/{column}")
  Game flag(
      @ApiParam(value = "gameId of the game on which the cell must be flagged.", readOnly = true) @PathVariable final int gameId,
      @ApiParam(value = "row of the cell to flag.", readOnly = true) @PositiveOrZero @PathVariable final int row,
      @ApiParam(value = "column of the cell to flag.", readOnly = true) @PositiveOrZero @PathVariable final int column)
  {
    return service.flag(gameId, row, column);
  }

  /**
   * Removes the flag in a {@link Game}'s cell.
   *
   * @param gameId unique gameId of the {@link Game}.
   * @param row    row of the {@link Game}'s board's cell to un-flag.
   * @param column column of the {@link Game}'s board's cell to un-flag.
   * @return {@link Game}, with cell un-flagged.
   */
  @ApiOperation("""
                Un-flags a Game's board cell.
                                        
                If the game wasn't started at the time, then it is marked as PLAYING.
                If the cell was already revealed, nothing happens.
                If the cell wasn't flagged, nothing happens.
                """)
  @PutMapping("{gameId}/unflag/{row}/{column}")
  Game unflag(
      @ApiParam(value = "gameId of the game on which the cell must be flagged.", readOnly = true) @PathVariable final int gameId,
      @ApiParam(value = "row of the cell to flag.", readOnly = true) @PositiveOrZero @PathVariable final int row,
      @ApiParam(value = "column of the cell to flag.", readOnly = true) @PositiveOrZero @PathVariable final int column)
  {
    return service.unflag(gameId, row, column);
  }
}
