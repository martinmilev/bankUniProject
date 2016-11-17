package com.clouway.core;

import com.google.common.io.BaseEncoding;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * @author Martin Milev <martinmariusmilev@gmail.com>
 */
public class UuidGenerator implements Provider<String> {

  @Override
  public String get() {
    UUID uuid = UUID.randomUUID();
    return BaseEncoding.base64Url().encode(
            ByteBuffer.allocate(16).putLong(uuid.getMostSignificantBits()).putLong(uuid.getLeastSignificantBits()).array()
    );
  }
}