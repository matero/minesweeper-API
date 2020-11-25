package minesweeper.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

final class TokenBasedAuthentication implements Authentication
{
  final TokenDetails tokenDetails;
  private final AccountDetails accountDetails;

  TokenBasedAuthentication(final AccountDetails accountDetails, final TokenDetails tokenDetails)
  {
    this.accountDetails = accountDetails;
    this.tokenDetails = tokenDetails;
  }

  @Override public Set<? extends GrantedAuthority> getAuthorities() { return accountDetails.getAuthorities(); }

  @Override public Object getCredentials() { return accountDetails.getPassword(); }

  @Override public Object getDetails() { return null; }

  @Override public Object getPrincipal() { return accountDetails.getUsername(); }

  @Override public boolean isAuthenticated() { return true; }

  @Override public void setAuthenticated(final boolean isAuthenticated) { /*nothing to do*/}

  @Override public String getName() { return accountDetails.getUsername(); }
}
