package io.github.olly_org.olly;

import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
class PostgresMigrationTests extends AbstractMigrationTests {}
