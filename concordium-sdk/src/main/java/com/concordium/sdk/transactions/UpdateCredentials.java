package com.concordium.sdk.transactions;


import com.concordium.sdk.types.UInt64;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.val;

import java.nio.ByteBuffer;

/**
 * Initialize a new smart contract instance.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public final class UpdateCredentials extends Payload {

    /**
     * Payload to update credentials.
     */
    private final UpdateCredentialsPayload payload;


    private UpdateCredentials(UpdateCredentialsPayload payload) {
        this.payload = payload;
    }

    static UpdateCredentials createNew(UpdateCredentialsPayload payload) {
        return new UpdateCredentials(payload);
    }

    /**
     * This function returns the type of the payload.
     */
    @Override
    public PayloadType getType() {
        return PayloadType.UPDATE_CREDENTIALS;
    }

    /**
     * Get the bytes of the payload and put them in a buffer.
     *
     * @return The byte array of the payload.
     */
    @Override
    byte[] getBytes() {
        val payload_bytes = payload.getBytes();
        val buffer = ByteBuffer.allocate(payload_bytes.length + TransactionType.BYTES);
        buffer.put(TransactionType.UPDATE_CREDENTIALS.getValue());
        buffer.put(payload_bytes);
        return buffer.array();
    }

    @Override
    UInt64 getTransactionTypeCost() {
        return UInt64.from(500);
    }
}
