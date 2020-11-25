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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class SecurityContext extends WebSecurityConfigurerAdapter
{
  private final AccountDetailsService accountDetailsService;
  private final AuthenticationFilter authenticationFilter;

  @Autowired SecurityContext(final AccountDetailsService accountDetailsService, final AuthenticationFilter authenticationFilter)
  {
    this.accountDetailsService = accountDetailsService;
    this.authenticationFilter = authenticationFilter;
  }

  @Autowired void configureGlobal(final AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception
  {
    authenticationManagerBuilder.userDetailsService(accountDetailsService);
  }

  @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

  @Override protected void configure(final HttpSecurity httpSecurity) throws Exception
  {
    httpSecurity.cors().disable()
                .csrf().disable()
                .authorizeRequests()
                // account registration endpoint
                .antMatchers("/register").anonymous()
                // authenticate endpoint
                .antMatchers("/login").anonymous()
                // swagger3 endpoints
                .antMatchers("/swagger-ui/**", "/webjars/**", "/swagger-resources/**", "/v3/api-docs/**").permitAll()
                .anyRequest()
                .authenticated()
                .and().exceptionHandling()
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    httpSecurity.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
  }
}
