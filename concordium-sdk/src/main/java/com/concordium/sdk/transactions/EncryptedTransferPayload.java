package com.concordium.sdk.transactions;

import lombok.*;
import org.apache.commons.codec.DecoderException;

import java.nio.ByteBuffer;

@ToString
@Getter
public class EncryptedTransferPayload {

    private final AccountAddress to;

    private final EncryptedAmountTransferData data;

    public EncryptedTransferPayload(AccountAddress to, EncryptedAmountTransferData data) {
        this.to = to;
        this.data = data;
    }

    public static EncryptedTransferPayload from(AccountAddress to, EncryptedAmountTransferData data) throws Exception {
        return new EncryptedTransferPayload(to, data);
    }

    public byte[] getBytes() throws DecoderException {
        val toAddress = to.getBytes();
        val dataBytes = data.getBytes();
        val buffer = ByteBuffer.allocate(
                TransactionType.BYTES
                        + toAddress.length
                        + dataBytes.length);
        buffer.put(TransactionType.ENCRYPTED_TRANSFER.getValue());
        buffer.put(toAddress);
        buffer.put(dataBytes);

        return buffer.array();
    }
}
