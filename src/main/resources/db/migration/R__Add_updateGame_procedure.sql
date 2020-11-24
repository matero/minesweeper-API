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
CREATE OR REPLACE PROCEDURE minesweeper.updateGame(IN gameId INTEGER, IN gameStatus minesweeper.GameStatus, IN gameBoard INTEGER[][])
LANGUAGE plpgsql AS $$
BEGIN
    CASE gameStatus
        WHEN 'PLAYING' THEN

            UPDATE minesweeper.Games
            SET status = gameStatus, board = gameBoard
            WHERE id = gameId;

            INSERT INTO minesweeper.playtimes(game) VALUES (gameId);

        WHEN 'WON', 'LOOSE' THEN

            UPDATE minesweeper.Games
            SET status = gameStatus, board = gameBoard, finishedAt = current_timestamp
            WHERE id = gameId;

            UPDATE minesweeper.PlayTimes
            SET finishedAt = current_timestamp
            WHERE game = gameId AND finishedAt IS NULL;

            IF NOT FOUND THEN
                INSERT INTO minesweeper.playtimes(game, startedAt, finishedAt)
                VALUES (gameId, current_timestamp, current_timestamp);
            END IF;

        ELSE
            RAISE 'Unexpected gameStatus "%"', gameStatus::text
                USING ERRCODE = 'invalid_parameter_value',
                         HINT = 'Only PLAYING / WON / LOOSE GameStatus are allowed.';
    END CASE;
END; $$;

COMMENT ON PROCEDURE minesweeper.updateGame(IN INTEGER, IN minesweeper.GameStatus, IN INTEGER[][]) IS $$Updates a game
to a new status and board.

It assumes that preconditions are accomplished (the game exists, it is not transitioning to `PAUSED` status -see
pauseGame for that-, and is not in WON or LOOSE status).

#### Parameters
1. `gameId`: unique identifier of the game to pause.
2. `gameStatus`: status to which the game is transitioning.
3. `gameBoard`: definition of the desired game cells board.
$$;
