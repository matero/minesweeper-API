package minesweeper.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Schema
final class Credentials
{
  @Schema(description = "email of the user to identify", format = "email")
  @JsonProperty @Email @NotNull final String email;
  @Schema(description = "password of the user to identify", format = "password")
  @JsonProperty @NotEmpty final String password;

  @JsonCreator Credentials(final String email, final String password)
  {
    this.email = email;
    this.password = password;
  }

  @Override public boolean equals(final Object o)
  {
    if (this == o) {
      return true;
    }
    return o instanceof Credentials that && email.equals(that.email) && password.equals(that.password);
  }

  @Override public int hashCode()
  {
    int result = email.hashCode();
    result = 31 * result + password.hashCode();
    return result;
  }

  @Override public String toString()
  {
    return "Credentials(email='" + email + "', password='" + password + "')";
  }
}
