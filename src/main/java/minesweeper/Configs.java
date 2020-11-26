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
package minesweeper;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import javax.sql.DataSource;
import java.util.List;

import static java.util.function.Predicate.not;
import static springfox.documentation.builders.PathSelectors.ant;

@Configuration
class WebConfig implements WebMvcConfigurer
{
  private static final long MAX_AGE_SECS = 3600;

  @Override public void addCorsMappings(final CorsRegistry registry)
  {
    registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods("HEAD", "OPTIONS", "GET", "POST", "PUT", "PATCH", "DELETE")
            .maxAge(MAX_AGE_SECS);
  }
}

@Configuration
class DatabaseConfig
{
  @Value("${spring.datasource.url:}") private String url;
  @Value("${spring.datasource.username:}") private String username;
  @Value("${spring.datasource.password:}") private String password;
  @Value("${spring.datasource.schema:}") private String schema;

  @Bean @Primary public DataSource dataSource()
  {
    final var config = datasourceConfig();
    if (schema != null && !schema.isEmpty()) {
      config.setSchema(schema);
    }
    return new HikariDataSource(config);
  }

  @Bean public DataSourceHealthIndicator dataSourceHealthIndicator() { return new DataSourceHealthIndicator(dataSource()); }

  @Bean @FlywayDataSource public DataSource flywayDataSource() { return new HikariDataSource(datasourceConfig()); }

  private HikariConfig datasourceConfig()
  {
    if (url == null || url.isEmpty()) {
      throw new IllegalStateException("no datasource configured.");
    }

    final var config = new HikariConfig();
    config.setJdbcUrl(url);
    if (username != null && !username.isEmpty()) {
      config.setUsername(username);
    }
    if (password != null && !password.isEmpty()) {
      config.setPassword(password);
    }
    return config;
  }
}

@Component
final class Flyway__7_2__MigrationStrategy implements FlywayMigrationStrategy
{
  @Override public void migrate(final Flyway flyway) { flyway.migrate(); }
}

@Configuration
@Import(springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration.class)
class SwaggerConfiguration
{
  @Bean Docket docket()
  {
    return new Docket(DocumentationType.SWAGGER_2)
               .ignoredParameterTypes(AuthenticationPrincipal.class)
               .select()
               .apis(not(RequestHandlerSelectors.basePackage("org.springframework.boot")))
               .paths(not(PathSelectors.regex("/error.*")))
               .build()
               .securitySchemes(List.of(new ApiKey("Bearer", "Authorization", "header")))
               .securityContexts(List.of(securityContext()));
  }

  private SecurityContext securityContext()
  {
    return SecurityContext.builder()
                          .securityReferences(defaultAuth())
                          .forPaths(ant("/swagger-ui/**"))
                          .forPaths(ant("/webjars/**"))
                          .forPaths(ant("/swagger-resources/**"))
                          .forPaths(ant("/v3/api-docs/**"))
                          .build();
  }

  List<SecurityReference> defaultAuth()
  {
    final AuthorizationScope[] authorizationScopes = {new AuthorizationScope("global", "accessEverything")};
    return List.of(new SecurityReference("anyone", authorizationScopes));
  }
}
