package com.concordium.sdk.transactions;

import com.concordium.sdk.types.UInt64;
import lombok.*;

import java.nio.ByteBuffer;

/**
 * Payload of the `ConfigureBaker` transaction. This transaction contains the keys
 * with which the baker registered.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@ToString
public final class ConfigureBaker extends Payload {

    /**
     * The keys with which the baker registered.
     */
    private final ConfigureBakerPayload payload;

    @Override
    public PayloadType getType() {
        return PayloadType.CONFIGURE_BAKER;
    }

    /**
     * Get the cost of the transaction based on the keys with proofs
     * @return UInt64
     */
    @Override
    UInt64 getTransactionTypeCost() {
        if (this.payload.getKeysWithProofs() != null)
            return TransactionTypeCost.CONFIGURE_BAKER_WITH_PROOFS.getValue();
        else
            return TransactionTypeCost.CONFIGURE_BAKER.getValue();
    }

    /**
     * Get the bytes representation of the payload
     * @return byte[]
     */
    public byte[] getBytes() {
        val payloadBytes = payload.getBytes();
        val bufferLength = TransactionType.BYTES +
                payloadBytes.length;

        val buffer = ByteBuffer.allocate(bufferLength);

        buffer.put(TransactionType.CONFIGURE_BAKER.getValue());
        buffer.put(payloadBytes);
        return buffer.array();
    }

    static ConfigureBaker createNew(ConfigureBakerPayload payload) {
        return new ConfigureBaker(payload);
    }
}