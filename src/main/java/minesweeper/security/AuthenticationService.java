package minesweeper.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
public final class AuthenticationService
{
  private static final int NO_REFRESHES_DONE = 0;

  private final long tokenLifeInSeconds;

  private final int refreshLimit;

  private final TokenIssuer tokenIssuer;

  private final TokenParser tokenParser;

  AuthenticationService(
      final @Value("${security.jwt.tokenLifeInSeconds}") long tokenLifeInSeconds,
      final @Value("${security.jwt.refreshLimit}") int refreshLimit,
      final TokenIssuer tokenIssuer,
      final TokenParser tokenParser)
  {
    this.tokenLifeInSeconds = tokenLifeInSeconds;
    this.refreshLimit = refreshLimit;
    this.tokenIssuer = tokenIssuer;
    this.tokenParser = tokenParser;
  }

  public String issueToken(final String email, final Set<String> roles)
  {
    final var id = generateTokenIdentifier();
    final var issuedDate = LocalDateTime.now();
    final var expirationDate = expirationDateFor(issuedDate);

    final var authenticationTokenDetails = new TokenDetails(id, email, roles, issuedDate, expirationDate, NO_REFRESHES_DONE, refreshLimit);

    return tokenIssuer.issueTokenFor(authenticationTokenDetails);
  }

  public String currentAccountEmail()
  {
    final var authentication = (TokenBasedAuthentication) SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      return null;
    } else {
      return authentication.getName();
    }
  }

  private String generateTokenIdentifier()
  {
    return UUID.randomUUID().toString();
  }

  private LocalDateTime expirationDateFor(final LocalDateTime issuedDate)
  {
    return issuedDate.plusSeconds(tokenLifeInSeconds);
  }

  TokenDetails parseToken(final String token)
  {
    return tokenParser.parseToken(token);
  }

  String refreshToken(final TokenDetails tokenDetails)
  {
    if (!tokenDetails.isEligibleForRefreshment()) {
      throw AuthenticationTokenCannotBeRefreshed.Unique.INSTANCE;
    }

    final var issuedDate = LocalDateTime.now();
    final var expirationDate = expirationDateFor(issuedDate);

    final var newTokenDetails = new TokenDetails(tokenDetails.id, tokenDetails.email, tokenDetails.roles, issuedDate, expirationDate, tokenDetails.refreshCount + 1, refreshLimit);

    return tokenIssuer.issueTokenFor(newTokenDetails);
  }
}
