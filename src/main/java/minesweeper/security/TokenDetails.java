package minesweeper.security;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Model that holds details about an authentication token.
 */
final class TokenDetails
{
  final String id;
  final String email;
  final Set<String> roles;
  final LocalDateTime issuedDate;
  final LocalDateTime expirationDate;
  final int refreshCount;
  final int refreshLimit;

  TokenDetails(
      final String id,
      final String email,
      final Set<String> roles,
      final LocalDateTime issuedDate,
      final LocalDateTime expirationDate,
      final int refreshCount,
      final int refreshLimit)
  {
    this.id = id;
    this.email = email;
    this.roles = roles;
    this.issuedDate = issuedDate;
    this.expirationDate = expirationDate;
    this.refreshCount = refreshCount;
    this.refreshLimit = refreshLimit;
  }

  boolean isEligibleForRefreshment()
  {
    return refreshCount < refreshLimit;
  }

  @Override public boolean equals(final Object o)
  {
    return (this == o) || (o instanceof TokenDetails that && id.equals(that.id));
  }

  @Override public int hashCode()
  {
    return id.hashCode();
  }

  @Override public String toString()
  {
    return "AuthenticationTokenDetails(id='" + id + "', email='" + email + "', roles=" + roles + ", issuedDate=" + issuedDate + ", expirationDate=" + expirationDate + ", "
           + "refreshCount=" + refreshCount + ", refreshLimit=" + refreshLimit + ')';
  }
}
