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

CREATE
TYPE minesweeper.GameStatus AS ENUM ('CREATED', 'PLAYING', 'PAUSED', 'WON', 'LOOSE');

CREATE
CAST
    (varchar AS minesweeper.GameStatus)
    WITH INOUT AS IMPLICIT;

COMMENT ON TYPE minesweeper.GameStatus IS $$Possible status of a minesweeper game.

Possible values are:

1. **CREATED**: The game has been created, but no cell was revealed.
2. **PLAYING**: The game has been created, and at least ONE cell was revealed (and no revealed cell was a mine).
1. **WON**: ALL cells has been revelead.
1. **LOOSE**: Last revealed cell was a MINE.
$$;

CREATE TABLE minesweeper.Games
(
    id         SERIAL,
    owner      VARCHAR(255)                             NOT NULL REFERENCES minesweeper.Accounts (email),
    status     minesweeper.GameStatus DEFAULT 'CREATED' NOT NULL,
    creation   TIMESTAMP DEFAULT current_timestamp      NOT NULL,
    finishedAt TIMESTAMP,
    board      INTEGER[][]                              NOT NULL,

    PRIMARY KEY (id, owner)
);

COMMENT ON TABLE minesweeper.Games IS $$Games played or being, by now they only have a unique ID and the board definition$$;
COMMENT ON COLUMN minesweeper.Games.id IS $$Unique ID of the minesweeper game$$;
COMMENT ON COLUMN minesweeper.Games.owner IS $$Accounts which is playing the game, no one else should be able to access the game.$$;
COMMENT ON COLUMN minesweeper.Games.status IS $$Current status of the game$$;
COMMENT ON COLUMN minesweeper.Games.creation IS $$Instant in which the game was created$$;
COMMENT ON COLUMN minesweeper.Games.finishedAt IS $$Instant in which the game was finished (the status passed to `WON` or `LOOSE`)$$;
COMMENT ON COLUMN minesweeper.Games.board IS $$Cells defined for the game's board$$;
