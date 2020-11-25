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

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import minesweeper.security.AuthenticationService;
import minesweeper.security.AuthenticationToken;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Set;

@Validated
@RestController
@RequestMapping(produces = "application/json; charset=utf-8")
class Accounts
{
  public static final Set<String> USER_ROLE = Set.of("user");

  private final AccountsService service;

  private final AuthenticationService authenticationService;

  Accounts(final AccountsService service, final AuthenticationService authenticationService)
  {
    this.service = service;
    this.authenticationService = authenticationService;
  }

  /**
   * Registers an {@link Account}.
   *
   * @param registration {@link Registration} of the account.
   * @return a newly created {@link Account}.
   */
  @ApiOperation("Registers an account.")
  @PostMapping("/register")
  AuthenticationToken register(
      @ApiParam(value = "data of the account to create.", required = true, readOnly = true) @RequestBody @Validated final Registration registration,
      final HttpServletResponse response)
  {
    final var account = service.createAccountWith(registration);
    final var token = authenticationService.issueToken(account.email, USER_ROLE);
    response.setStatus(HttpStatus.CREATED.value());
    return new AuthenticationToken(token);
  }
}
