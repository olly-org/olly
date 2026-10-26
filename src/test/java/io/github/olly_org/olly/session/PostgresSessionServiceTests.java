package io.github.olly_org.olly.session;

import io.github.olly_org.olly.TestcontainersConfiguration;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
class PostgresSessionServiceTests extends AbstractSessionServiceTests {}
