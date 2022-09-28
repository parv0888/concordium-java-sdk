package com.concordium.sdk.transactions;


import com.concordium.sdk.types.UInt64;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.val;
import org.apache.commons.codec.DecoderException;

import java.nio.ByteBuffer;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public final class EncryptedTransfer extends Payload {

    private final static TransactionType TYPE = TransactionType.ENCRYPTED_TRANSFER;
    private final EncryptedTransferPayload payload;
    private final UInt64 maxEnergyCost;

    public EncryptedTransfer(EncryptedTransferPayload payload, UInt64 maxEnergyCost) {
        this.payload = payload;
        this.maxEnergyCost = maxEnergyCost;
    }

    @Override
    public PayloadType getType() {
        return PayloadType.ENCRYPTED_TRANSFER;
    }

    @Override
    byte[] getBytes() {
        byte[] payloadBytes = new byte[0];
        try {
            payloadBytes = payload.getBytes();
        } catch (DecoderException e) {
            throw new RuntimeException(e);
        }

        val buffer = ByteBuffer.allocate(payloadBytes.length);
        buffer.put(payloadBytes);
        return buffer.array();
    }

    @Override
    UInt64 getTransactionTypeCost() {
        return this.maxEnergyCost;
    }

    static EncryptedTransfer createNew(EncryptedTransferPayload payload, UInt64 maxEnergyCost) {
        return new EncryptedTransfer(payload, maxEnergyCost);
    }
}
