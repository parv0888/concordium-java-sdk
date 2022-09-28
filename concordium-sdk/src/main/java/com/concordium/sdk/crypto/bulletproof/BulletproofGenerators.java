package com.concordium.sdk.crypto.bulletproof;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.ToString;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

@ToString
public class BulletproofGenerators {
    private final byte[] bytes;

    private BulletproofGenerators(final byte[] bytes) {
        this.bytes = bytes;
    }

    @JsonCreator
    BulletproofGenerators(String hex) throws DecoderException {
        this.bytes = Hex.decodeHex(hex);
    }

    public static BulletproofGenerators from(final byte[] bytes) {
        return new BulletproofGenerators(bytes);
    }

    @JsonValue
    public String toHex() {
        return Hex.encodeHexString(this.bytes);
    }
}
