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

import minesweeper.JdbcTemplateRepositoryTestCase;
import minesweeper.NotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

final class Games_Repository_tests extends JdbcTemplateRepositoryTestCase
{
  Repository repo;

  @BeforeEach void setup() { repo = new Repository(db()); }

  @Test void when_no_Game_exists_for_provided_id_then_findById_should_fail_with_NotFound()
  {
    //given:
    noGamesAreDefined();

    //when: findById should fail with NotFound
    final var notFound = assertThrows(NotFound.class, () -> repo.findById(1));
    assertEquals("No Game is defined with id=1", notFound.getMessage());
  }

  @Sql @Test void when_Game_exists_for_provided_id_then_findById_should_return_it()
  {
    //given:
    oneGameIsDefinedWithId(1);

    //when: look for Game with id=1
    final var game = repo.findById(1);

    //then
    assertAll("findById(1)",
              () -> assertThat(game, is(notNullValue())),
              () -> assertThat(game.id, is(1)),
              () -> assertThat(game.status, is(GameStatus.WON)),
              () -> assertThat(game.creation, is(LocalDateTime.of(2008, Month.MARCH, 20, 0, 0))),
              () -> assertThat(game.finishedAt, is(LocalDateTime.of(2008, Month.MARCH, 21, 0, 0))),
              () -> assertThat(game.playTime, is(notNullValue())),
              () -> assertThat(game.board, is(notNullValue())),
              () -> assertThat(game.getRows(), is(3)),
              () -> assertThat(game.getColumns(), is(3)));
  }

  @Sql @Test void when_Game_has_no_play_times_then_findById_should_assign_it_a_ZERO_playtime()
  {
    //given:
    oneGameIsDefinedWithId(1);

    //when: look for Game with id=1
    final var game = repo.findById(1);

    //then
    assertThat(game.playTime, is(Duration.ZERO));
  }

  @Sql @Test void when_Game_has_one_unfinished_play_time_then_findById_should_assign_time_lapsed_since_playtime_has_started_as_playtime()
  {
    //given:
    oneGameIsDefinedWithId(1);

    //when: look for Game with id=1
    final var game = repo.findById(1);

    //then
    assertThat(game.playTime, is(greaterThanOrEqualTo(Duration.ofSeconds(2))));
  }

  @Sql @Test void when_Game_has_one_finished_play_time_then_findById_should_assign_it_the_time_lapsed_between_start_and_finish_as_playtime()
  {
    //given:
    oneGameIsDefinedWithId(1);

    //when: look for Game with id=1
    final var game = repo.findById(1);

    //then
    assertThat(game.playTime, is(Duration.ofMillis(700)));
  }

  @Sql @Test void when_Game_has_multiple_playTimes_then_findById_should_assign_the_sum_of_times_lapsed_between_start_and_finish_of_them_as_playtime()
  {
    //given:
    oneGameIsDefinedWithId(1);

    //when: look for Game with id=1
    final var game = repo.findById(1);

    //then
    assertThat(game.playTime, is(Duration.ofMillis(2700)));
  }

  @Test void when_no_Game_is_defined_then_findAll_should_be_an_empty_list()
  {
    //given:
    noGamesAreDefined();

    //when: look for Game with id=1
    final var games = repo.findAll();

    //then
    assertThat(games, is(empty()));
  }

  @Sql @Test void when_N_Games_are_defined_then_findAll_should_return_all_of_them_sorted_by_creation_date()
  {
    //given:
    definedGamesCountIs(3);

    //when: look for Game with id=1
    final var games = repo.findAll();

    //then
    assertThat(games, hasSize(3));
    assertThat(games.get(0).id, is(equalTo(1))); // cant use bean property matcher as it has no getter
    assertThat(games.get(1).id, is(equalTo(3)));
    assertThat(games.get(2).id, is(equalTo(2)));
  }

  private void noGamesAreDefined()
  {
    definedGamesCountIs(0);
  }

  private void definedGamesCountIs(final int amount)
  {
    assumeTrue(selectBoolean("SELECT count(*) = ? FROM minesweeper.Games", amount));
  }

  private void oneGameIsDefinedWithId(final int id)
  {
    assumeTrue(selectBoolean("SELECT count(*) = 1 FROM minesweeper.Games WHERE id=?", id));
  }
}
