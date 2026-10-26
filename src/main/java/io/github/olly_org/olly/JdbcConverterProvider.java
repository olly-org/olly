package io.github.olly_org.olly;

import java.util.List;

public interface JdbcConverterProvider {
  List<?> converters();
}
