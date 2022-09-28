package com.concordium.sdk.transactions;


import com.concordium.sdk.exceptions.TransactionCreationException;
import com.concordium.sdk.types.UInt64;
import lombok.Builder;
import lombok.Getter;
import lombok.val;

@Getter
public class EncryptedTransferTransaction extends AbstractTransaction {

    private final EncryptedTransferPayload payload;
    private final AccountAddress sender;
    private final AccountNonce nonce;
    private final Expiry expiry;
    private final TransactionSigner signer;

    private BlockItem blockItem;
    private final UInt64 maxEnergyCost;

    @Builder
    public EncryptedTransferTransaction(
            EncryptedTransferPayload payload,
            AccountAddress sender,
            AccountNonce nonce,
            Expiry expiry,
            TransactionSigner signer,
            BlockItem blockItem,
            UInt64 maxEnergyCost) throws TransactionCreationException {
        this.payload = payload;
        this.sender = sender;
        this.nonce = nonce;
        this.expiry = expiry;
        this.signer = signer;
        this.blockItem = blockItem;
        this.maxEnergyCost = maxEnergyCost;
    }

    public static EncryptedTransferTransactionBuilder builder() {
        return new CustomBuilder();
    }

    @Override
    public BlockItem getBlockItem() {
        return blockItem;
    }

    private static class CustomBuilder extends EncryptedTransferTransactionBuilder {
        @Override
        public EncryptedTransferTransaction build() throws TransactionCreationException {
            val transaction = super.build();

            Transaction.verifyEncryptedTransferInput(
                    transaction.sender,
                    transaction.nonce,
                    transaction.expiry,
                    transaction.signer,
                    transaction.payload);
            transaction.blockItem = EncryptedTransferInstance(transaction).toBlockItem();
            return transaction;
        }

        private Payload EncryptedTransferInstance(EncryptedTransferTransaction transaction) throws TransactionCreationException {
            return EncryptedTransfer.createNew(
                            transaction.payload,
                            transaction.maxEnergyCost).
                    withHeader(TransactionHeader.builder()
                            .sender(transaction.sender)
                            .accountNonce(transaction.nonce.getNonce())
                            .expiry(transaction.expiry.getValue())
                            .build())
                    .signWith(transaction.signer);
        }
    }
}
