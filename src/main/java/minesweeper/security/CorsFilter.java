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

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements javax.servlet.Filter
{
  private static final String VALID_METHODS = "DELETE, HEAD, GET, OPTIONS, POST, PUT";

  @Override public void destroy() { /* nothing to do */ }

  @Override public void init(final FilterConfig config) { /* nothing to do */ }

  @Override public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws ServletException, IOException
  {
    final var httpServletRequest = (HttpServletRequest) request;
    final var httpServletResponse = (HttpServletResponse) response;

    // No Origin header present means this is not a cross-domain request
    final var origin = httpServletRequest.getHeader("Origin");
    if (origin == null) {
      // Return standard response if OPTIONS request w/o Origin header
      if ("OPTIONS".equalsIgnoreCase(httpServletRequest.getMethod())) {
        httpServletResponse.setHeader("Allow", VALID_METHODS);
        httpServletResponse.setStatus(200);
        return;
      }
    } else {
      // This is a cross-domain request, add headers allowing access
      httpServletResponse.setHeader("Access-Control-Allow-Origin", origin);
      httpServletResponse.setHeader("Access-Control-Allow-Methods", VALID_METHODS);

      final var headers = httpServletRequest.getHeader("Access-Control-Request-Headers");
      if (headers != null) {
        httpServletResponse.setHeader("Access-Control-Allow-Headers", headers);
      }

      // Allow caching cross-domain permission
      httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
    }
    // Pass request down the chain, except for OPTIONS
    if (!"OPTIONS".equalsIgnoreCase(httpServletRequest.getMethod())) {
      filterChain.doFilter(request, response);
    }
  }
}
