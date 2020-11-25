package minesweeper.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.ZoneId;
import java.util.Date;

/**
 * Component which provides operations for issuing JWT tokens.
 */
@Component
final class TokenIssuer
{
  private final Key secret;
  /**
   * Identifies the recipients that the JWT token is intended for.
   */
  private final String audience;
  /**
   * Identifies the JWT token issuer.
   */
  private final String issuer;

  TokenIssuer(
      final Key secret,
      final @Value("${security.jwt.audience}") String audience,
      final @Value("${security.jwt.issuer}") String issuer)
  {
    this.secret = secret;
    this.audience = audience;
    this.issuer = issuer;
  }

  String issueTokenFor(final TokenDetails tokenDetails)
  {
    final var issuedAt = Date.from(tokenDetails.issuedDate.atZone(ZoneId.systemDefault()).toInstant());
    final var expiration = Date.from(tokenDetails.expirationDate.atZone(ZoneId.systemDefault()).toInstant());
    return Jwts.builder()
               .setId(tokenDetails.id)
               .setIssuer(issuer)
               .setAudience(audience)
               .setSubject(tokenDetails.email)
               .setIssuedAt(issuedAt)
               .setExpiration(expiration)
               .claim("roles", tokenDetails.roles)
               .claim("refreshCount", tokenDetails.refreshCount)
               .claim("refreshLimit", tokenDetails.refreshLimit)
               .signWith(secret, SignatureAlgorithm.HS256)
               .compact();
  }
}
