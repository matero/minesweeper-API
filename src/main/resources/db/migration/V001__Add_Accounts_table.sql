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
CREATE TABLE minesweeper.Accounts
(
    email    VARCHAR(255) NOT NULL PRIMARY KEY,
    name     VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

COMMENT ON TABLE minesweeper.Accounts IS $$Accounts that can access **minesweeper** system.$$;

COMMENT ON COLUMN minesweeper.Accounts.email IS $$Email of the **minesweeper** user, every action or calculation related
to Accounts is performed using its `email`.$$;

COMMENT ON COLUMN minesweeper.Accounts.name IS $$Value defined by the user to be named in every interaction with him.$$;

COMMENT ON COLUMN minesweeper.Accounts.password IS $$Hashed form of the password assigned to the user. At this level we
don't know which kind of algorithm is used to perform the hashing.$$;

