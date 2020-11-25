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

import minesweeper.JdbcTemplateRepositoryTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class Accounts_Repository_tests extends JdbcTemplateRepositoryTestCase
{
  AccountsRepository repo;

  @BeforeEach void setup() { repo = new AccountsRepository(db()); }

  @Test void when_try_to_create_an_Account_with_Existing_email_then_it_should_fail_with_EmailAlreadyUsed()
  {
    //given
    repo.insert("email@email.com", "name", "password");

    //expect
    final var emailAlreadyUsed = assertThrows(EmailAlreadyUsed.class, () -> repo.insert("email@email.com", "name", "password"));
    assertEquals("The email 'email@email.com' is already used.", emailAlreadyUsed.getMessage());
  }
}
