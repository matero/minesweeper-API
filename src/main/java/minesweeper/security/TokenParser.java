package minesweeper.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

@Component
final class TokenParser
{
  private final Key secretKey;
  private final String audience;
  private final String issuer;
  private final long clockSkew;

  TokenParser(
      final Key secretKey,
      final @Value("${security.jwt.audience}") String audience,
      final @Value("${security.jwt.issuer}") String issuer,
      final @Value("${security.jwt.clockSkew}") long clockSkew)
  {
    this.secretKey = secretKey;
    this.audience = audience;
    this.issuer = issuer;
    this.clockSkew = clockSkew;
  }

  /**
   * Parse a JWT token.
   *
   * @param token
   * @return
   */
  public TokenDetails parseToken(final String token)
  {
    final var claims = parseClaims(token);
    return new TokenDetails(claims.getId(),
                            getEmailFrom(claims),
                            getRolesFrom(claims),
                            getIssuedDateFrom(claims),
                            getExpirationDateFrom(claims),
                            getRefreshCountFrom(claims),
                            getRefreshLimitFrom(claims));
  }

  private Claims parseClaims(final String token)
  {
    final var jwtParser = Jwts.parserBuilder()
                              .setSigningKey(secretKey)
                              .requireAudience(audience)
                              .requireIssuer(issuer)
                              .setAllowedClockSkewSeconds(clockSkew)
                              .build();
    try {
      return jwtParser.parseClaimsJws(token).getBody();
    } catch (final SignatureException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SecurityException e) {
      throw new InvalidAuthenticationToken("Invalid token", e);
    } catch (final ExpiredJwtException e) {
      throw new InvalidAuthenticationToken("Expired token", e);
    } catch (final InvalidClaimException e) {
      throw new InvalidAuthenticationToken("Invalid value for claim '" + e.getClaimName() + "'.", e);
    }
  }

  private String getEmailFrom(final Claims claims)
  {
    return claims.getSubject();
  }

  private Set<String> getRolesFrom(final Claims claims)
  {
    @SuppressWarnings("unchecked") final var roles = (List<String>) claims.getOrDefault("roles", List.<String>of());
    return Set.copyOf(roles);
  }

  private LocalDateTime getIssuedDateFrom(final Claims claims)
  {
    return LocalDateTime.ofInstant(claims.getIssuedAt().toInstant(), ZoneId.systemDefault());
  }

  /**
   * Extract the expiration date from the token claims.
   *
   * @param claims
   * @return Expiration date of the JWT token
   */
  private LocalDateTime getExpirationDateFrom(final Claims claims)
  {
    return LocalDateTime.ofInstant(claims.getExpiration().toInstant(), ZoneId.systemDefault());
  }

  /**
   * Extract the refresh count from the token claims.
   *
   * @param claims
   * @return Refresh count from the JWT token
   */
  private int getRefreshCountFrom(final Claims claims)
  {
    return (int) claims.get("refreshCount");
  }

  /**
   * Extract the refresh limit from the token claims.
   *
   * @param claims
   * @return Refresh limit from the JWT token
   */
  private int getRefreshLimitFrom(final Claims claims)
  {
    return (int) claims.get("refreshLimit");
  }
}
