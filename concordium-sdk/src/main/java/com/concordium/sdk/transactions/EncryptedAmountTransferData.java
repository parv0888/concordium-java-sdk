package com.concordium.sdk.transactions;

import com.concordium.sdk.crypto.ed25519.EncryptedTransferSupport;
import com.concordium.sdk.serializing.JsonMapper;
import com.concordium.sdk.transactions.encryptedtransfer.EncryptedAmountTransferDataJniInput;
import com.concordium.sdk.types.UInt64;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import lombok.val;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.nio.ByteBuffer;

@Jacksonized
@Builder
@Data
public class EncryptedAmountTransferData {

    /**
     * Encryption of the remaining amount.
     */
    private final String remainingAmount;

    /**
     * Encryption of the Amount that will be sent.
     */
    private final String transferAmount;

    /**
     * The index such that the encrypted amount used in the transfer represents
     * the aggregate of all encrypted amounts with indices < `index` existing
     * on the account at the time. New encrypted amounts can only add new indices.
     */
    private final UInt64 index;

    /**
     * A collection of all the proofs.
     */
    private final String proof;

    public static EncryptedAmountTransferData from(EncryptedAmountTransferDataJniInput jniInput) throws Exception {
        val inputJsonString = JsonMapper.INSTANCE.writeValueAsString(jniInput);
        val jsonBytes = EncryptedTransferSupport.encrypted_transfer_support(inputJsonString);
        val jsonStr = new java.lang.String(jsonBytes);

        return JsonMapper.INSTANCE.readValue(jsonStr, EncryptedAmountTransferData.class);
    }

    public byte[] getBytes() throws DecoderException {
        val remainingAmountBytes = Hex.decodeHex(this.remainingAmount);
        val transferAmountBytes = Hex.decodeHex(this.transferAmount);
        val proofBytes = Hex.decodeHex(this.proof);
        val buffer = ByteBuffer.allocate(
                        transferAmountBytes.length
                        + remainingAmountBytes.length
                        + UInt64.BYTES
                        + proofBytes.length);
        buffer.put(remainingAmountBytes);
        buffer.put(transferAmountBytes);
        buffer.put(index.getBytes());
        buffer.put(proofBytes);

        return buffer.array();
    }
}
