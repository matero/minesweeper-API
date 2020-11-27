package minesweeper.security;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

@OpenAPIDefinition(info = @Info(description = "provides endpoint to authenticate users."))
@Validated
@RestController
@RequestMapping(produces = "application/json; charset=utf-8")
class Authenticator
{
  private final CredentialsValidator credentialsValidator;
  private final AuthenticationService authenticationService;

  Authenticator(final CredentialsValidator credentialsValidator, final AuthenticationService authenticationService)
  {
    this.credentialsValidator = credentialsValidator;
    this.authenticationService = authenticationService;
  }

  @ApiOperation(value = "Authenticate a registered user.")
  @PostMapping("/login")
  public Map<String, Object> login(final @RequestBody @Valid Credentials credentials)
  {
    final var accountDetails = credentialsValidator.validateCredentials(credentials);
    final var email = accountDetails.getUsername();
    final var token = authenticationService.issueToken(email, AccountDetails.ROLES);
    return Map.of("email", email, "name", accountDetails.name, "token", token);
  }

  @ApiOperation(value = "Refresh the authentication token of a user.", authorizations = @Authorization("Bearer"))
  @PostMapping("/refresh")
  public AuthenticationToken refresh()
  {
    final var authentication = (TokenBasedAuthentication) SecurityContextHolder.getContext().getAuthentication();
    final var refreshedToken = authenticationService.refreshToken(authentication.tokenDetails);
    return new AuthenticationToken(refreshedToken);
  }
}
