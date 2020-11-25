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
package minesweeper.accounts;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@org.springframework.stereotype.Repository
class AccountsRepository
{
  private final JdbcTemplate db;

  AccountsRepository(final JdbcTemplate db) { this.db = db; }

  List<Account> findAll()
  {
    return db.query("SELECT email, name FROM minesweeper.Accounts ORDER BY email", (rs, rowNum) -> {
      final var email = rs.getString(1);
      final var name = rs.getString(2);
      return new Account(email, name);
    });
  }

  void insert(final String email, final String name, final String password)
  {
    try {
      db.update("INSERT INTO minesweeper.Accounts(email, name, password) VALUES (?, ?, ?)", email, name, password);
    } catch (final DuplicateKeyException e) {
      throw new EmailAlreadyUsed(email);
    }
  }
}
