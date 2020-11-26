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

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
class AuthenticationFilter extends OncePerRequestFilter
{
  private static final String BEARER = "Bearer ";
  private static final int BEARER_LENGTH = BEARER.length();

  private final AccountDetailsService accountDetailsService;
  private final AuthenticationService authenticationService;

  AuthenticationFilter(final AccountDetailsService accountDetailsService, final AuthenticationService authenticationService)
  {
    this.accountDetailsService = accountDetailsService;
    this.authenticationService = authenticationService;
  }

  @Override
  protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) throws ServletException, IOException
  {
    final var header = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (header != null) {
      final var token = getAuthorizationTokenFrom(header);
      handleTokenBasedAuthentication(token);
    }

    // Pass request down the chain, except for OPTIONS
    if (!"OPTIONS".equalsIgnoreCase(request.getMethod())) {
      chain.doFilter(request, response);
    }
  }

  private String getAuthorizationTokenFrom(final String header)
  {
    if (header.isEmpty() || header.isBlank() || !header.startsWith(BEARER)) {
      throw new BadCredentialsException("Authorization header must be provided");
    }
    return header.substring(BEARER_LENGTH);
  }

  private void handleTokenBasedAuthentication(final String token)
  {
    final var authenticationTokenDetails = authenticationService.parseToken(token);
    final var authenticatedUserDetails = accountDetailsService.loadUserByUsername(authenticationTokenDetails.email);
    SecurityContextHolder.getContext().setAuthentication(new TokenBasedAuthentication(authenticatedUserDetails, authenticationTokenDetails));
  }
}
