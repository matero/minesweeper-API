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

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

class AccountDetails implements UserDetails
{
  static final Set<String> ROLES = Set.of("user");
  private static final Set<? extends GrantedAuthority> AUTHORITIES = Set.of(UserGrantedAuthority.INSTANCE);

  private final String email;
  private final String password;

  AccountDetails(final String email, final String password)
  {
    this.password = password;
    this.email = email;
  }

  @Override public Set<? extends GrantedAuthority> getAuthorities() { return AUTHORITIES; }

  @Override public String getPassword() { return password; }

  @Override public String getUsername() { return email; }

  @Override public boolean isAccountNonExpired() { return true; }

  @Override public boolean isAccountNonLocked() { return true; }

  @Override public boolean isCredentialsNonExpired() { return true; }

  @Override public boolean isEnabled() { return true; }

  @Override public boolean equals(final Object o) { return (this == o) || (o instanceof AccountDetails that && email.equals(that.email)); }

  @Override public int hashCode() { return email.hashCode(); }

  @Override public String toString() { return "AccountDetails{email=" + email + '}'; }

  private enum UserGrantedAuthority implements GrantedAuthority
  {
    INSTANCE;

    @Override public String getAuthority() { return "user"; }
  }
}
