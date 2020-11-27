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
package minesweeper.security;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class AccountDetailsService implements UserDetailsService
{
  private final JdbcTemplate db;

  AccountDetailsService(final JdbcTemplate db) { this.db = db; }

  @Override @Transactional(readOnly = true) public AccountDetails loadUserByUsername(final String email) throws UsernameNotFoundException
  {
    final var details = db.query("SELECT name, password FROM minesweeper.Accounts WHERE email = ?",
                                 new Object[]{email},
                                 (rs, rowNum) -> {
                                   final var name = rs.getString(1);
                                   final var password = rs.getString(2);
                                   return new AccountDetails(email, name, password);
                                 });
    if (details.isEmpty()) {
      throw new UsernameNotFoundException("Account with email '" + email + "' not found.");
    }
    return details.get(0);
  }
}
