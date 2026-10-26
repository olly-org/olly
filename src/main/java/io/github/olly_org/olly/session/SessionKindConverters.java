package io.github.olly_org.olly.session;

import io.github.olly_org.olly.JdbcConverterProvider;
import java.util.List;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@Component
class SessionKindConverters implements JdbcConverterProvider {

  @Override
  public List<?> converters() {
    return List.of(ToString.INSTANCE, FromString.INSTANCE);
  }

  @WritingConverter
  enum ToString implements Converter<SessionKind, String> {
    INSTANCE;

    @Override
    public String convert(SessionKind kind) {
      return kind.id();
    }
  }

  @ReadingConverter
  enum FromString implements Converter<String, SessionKind> {
    INSTANCE;

    @Override
    public SessionKind convert(String id) {
      return new SessionKind(id);
    }
  }
}
