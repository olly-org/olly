package io.github.olly_org.olly;

import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.jdbc.core.dialect.JdbcDialect;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.data.jdbc.repository.config.JdbcConfiguration;
import org.springframework.data.relational.RelationalManagedTypes;
import org.springframework.data.relational.core.mapping.NamingStrategy;

@Configuration(proxyBeanMethods = false)
@EnableJdbcAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
class PersistenceConfiguration {
  @Bean
  Clock clock() {
    return Clock.tick(Clock.systemUTC(), Duration.ofNanos(1_000));
  }

  @Bean
  DateTimeProvider auditingDateTimeProvider(Clock clock) {
    return () -> Optional.of(clock.instant());
  }

  @Bean
  JdbcCustomConversions jdbcCustomConversions(
      JdbcDialect dialect, List<JdbcConverterProvider> providers) {
    List<Object> converters = new ArrayList<>();
    providers.forEach(provider -> converters.addAll(provider.converters()));
    return JdbcConfiguration.createCustomConversions(dialect, converters);
  }

  @Bean
  JdbcMappingContext jdbcMappingContext(
      Optional<NamingStrategy> namingStrategy,
      JdbcCustomConversions conversions,
      RelationalManagedTypes managedTypes) {
    JdbcMappingContext context =
        JdbcConfiguration.createMappingContext(
            managedTypes, conversions, namingStrategy.orElse(null));
    context.setForceQuote(false);
    return context;
  }
}
