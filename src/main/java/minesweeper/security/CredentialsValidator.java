package minesweeper.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class CredentialsValidator
{
  private final AccountDetailsService accountDetailsService;
  private final PasswordEncoder passwordEncoder;

  CredentialsValidator(final AccountDetailsService accountDetailsService, final PasswordEncoder passwordEncoder)
  {
    this.accountDetailsService = accountDetailsService;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional(readOnly = true) AccountDetails validateCredentials(final Credentials credentials)
  {
    final var authenticatedAccount = accountDetailsService.loadUserByUsername(credentials.email);

    if (!passwordEncoder.matches(credentials.password, authenticatedAccount.getPassword())) {
      throw BadCredentialsProvided.Unique.INSTANCE;
    }

    return authenticatedAccount;
  }
}
