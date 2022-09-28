package com.concordium.sdk.crypto.pedersencommitment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.ToString;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

@ToString
public class PedersenCommitmentKey {
    private final byte[] bytes;

    private PedersenCommitmentKey(final byte[] bytes) {
        this.bytes = bytes;
    }

    @JsonCreator
    PedersenCommitmentKey(String hex) throws DecoderException {
        this.bytes = Hex.decodeHex(hex);
    }

    public static PedersenCommitmentKey from(final byte[] bytes) {
        return new PedersenCommitmentKey(bytes);
    }

    @JsonValue
    public String toHex() {
        return Hex.encodeHexString(this.bytes);
    }
}
