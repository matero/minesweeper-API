package minesweeper.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public final class AuthenticationToken
{
  @Schema(format = "jwt", description = "JWT token provided by system when the user is authenticated.", required = true)
  @JsonProperty final String token;

  @JsonCreator public AuthenticationToken(final @JsonProperty String token)
  {
    this.token = token;
  }

  @Override public boolean equals(final Object o)
  {
    if (this == o) {
      return true;
    }
    return o instanceof AuthenticationToken that && token.equals(that.token);
  }

  @Override public int hashCode()
  {
    return token.hashCode();
  }

  @Override public String toString()
  {
    return "AuthenticationToken(" + token + ')';
  }
}
