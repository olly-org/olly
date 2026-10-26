package io.github.olly_org.olly.session;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

@Component
class JoinCodes {

  private static final char[] ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
  private static final int LENGTH = 6;

  private final SecureRandom random = new SecureRandom();

  String next() {
    char[] code = new char[LENGTH];
    for (int i = 0; i < LENGTH; i++) {
      code[i] = ALPHABET[random.nextInt(ALPHABET.length)];
    }
    return new String(code);
  }
}
