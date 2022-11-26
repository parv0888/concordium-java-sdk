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
    private final EncryptedAmountTransferData data;
    private final AccountAddress to;

    private final UInt64 maxEnergyCost;

    public EncryptedTransfer(EncryptedAmountTransferData data, AccountAddress to, UInt64 maxEnergyCost) {
        this.data = data;
        this.to = to;
        this.maxEnergyCost = maxEnergyCost;
    }

    @Override
    public PayloadType getType() {
        return PayloadType.ENCRYPTED_TRANSFER;
    }

    @Override
    byte[] getBytes() {
        val toAddress = to.getBytes();
        byte[] dataBytes = new byte[0];
        try {
            dataBytes = data.getBytes();
        } catch (DecoderException e) {
            throw new RuntimeException(e);
        }
        val buffer = ByteBuffer.allocate(
                TransactionType.BYTES
                        + toAddress.length
                        + dataBytes.length);
        buffer.put(TransactionType.ENCRYPTED_TRANSFER.getValue());
        buffer.put(toAddress);
        buffer.put(dataBytes);

        return buffer.array();

    }

    @Override
    UInt64 getTransactionTypeCost() {
        return this.maxEnergyCost;
    }

    static EncryptedTransfer createNew(EncryptedAmountTransferData data, AccountAddress to, UInt64 maxEnergyCost) {
        return new EncryptedTransfer(data, to, maxEnergyCost);
    }
}