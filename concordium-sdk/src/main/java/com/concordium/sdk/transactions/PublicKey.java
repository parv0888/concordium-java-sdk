package com.concordium.sdk.transactions;

import lombok.Getter;

/**
 * Public verification key for a given signature scheme.
 */

public enum PublicKey {
    ED25519((byte) 0);

    public static int BYTES = 1;

    @Getter
    private final byte value;

    PublicKey(byte type) {
        this.value = type;
    }

}
