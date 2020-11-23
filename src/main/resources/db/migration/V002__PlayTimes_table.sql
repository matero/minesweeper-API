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
CREATE TABLE minesweeper.PlayTimes
(
    game       INTEGER REFERENCES minesweeper.Games(id),
    startedAt  TIMESTAMP,
    finishedAt TIMESTAMP,

    PRIMARY KEY (game, startedAt)
);

COMMENT ON TABLE minesweeper.PlayTimes IS $$Time elapsed playing Games$$;
COMMENT ON COLUMN minesweeper.PlayTimes.game IS $$Unique ID of the minesweeper game for which the playing time is being tracked.$$;
COMMENT ON COLUMN minesweeper.PlayTimes.startedAt IS $$Instant in which the game was started or resumed.$$;
COMMENT ON COLUMN minesweeper.PlayTimes.finishedAt IS $$Instant in which the game was finished or paused.$$;
