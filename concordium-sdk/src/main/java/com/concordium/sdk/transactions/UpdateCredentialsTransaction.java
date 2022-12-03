package com.concordium.sdk.transactions;


import com.concordium.sdk.exceptions.TransactionCreationException;
import com.concordium.sdk.types.UInt64;
import lombok.Builder;
import lombok.Getter;
import lombok.val;

import java.util.Objects;


/**
 * Construct a transaction to initialise a smart contract.
 */
@Getter
public class UpdateCredentialsTransaction extends AbstractTransaction {

    /**
     * Payload to update credentials.
     */
    private final UpdateCredentialsPayload payload;

    /**
     * Account Address of the sender.
     */
    private final AccountAddress sender;
    /**
     * The senders account next available nonce.
     */
    private final AccountNonce nonce;
    /**
     * Indicates when the transaction should expire.
     */
    private final Expiry expiry;
    /**
     * A signer object that is used to sign the transaction.
     */
    private final TransactionSigner signer;

    private BlockItem blockItem;

    /**
     * A constructor of {@link UpdateCredentialsTransaction} class.
     */
    @Builder
    public UpdateCredentialsTransaction(UpdateCredentialsPayload payload,
                                   AccountAddress sender,
                                   AccountNonce nonce,
                                   Expiry expiry,
                                   TransactionSigner signer) throws TransactionCreationException {
        this.payload = payload;
        this.sender = sender;
        this.nonce = nonce;
        this.expiry = expiry;
        this.signer = signer;
    }

    /**
     * @return A new instance of the {@link UpdateCredentialsTransaction}  class.
     */
    public static UpdateCredentialsTransactionBuilder builder() {
        return new CustomBuilder();
    }

    @Override
    public BlockItem getBlockItem() {
        return blockItem;
    }

    private static class CustomBuilder extends UpdateCredentialsTransactionBuilder {
        static void verifyUpdateCredentialsInput(AccountAddress sender, AccountNonce nonce, Expiry expiry, TransactionSigner signer, UpdateCredentialsPayload payload) throws TransactionCreationException {
            Transaction.verifyAccountTransactionHeaders(sender, nonce, expiry, signer);
            if (Objects.isNull(payload)) {
                throw TransactionCreationException.from(new IllegalArgumentException("Payload cannot be null"));
            }
        }

        // Overriding the build method of the super class.
        @Override
        public UpdateCredentialsTransaction build() throws TransactionCreationException {
            val transaction = super.build();
            verifyUpdateCredentialsInput(
                    transaction.sender,
                    transaction.nonce,
                    transaction.expiry,
                    transaction.signer,
                    transaction.payload
            );
            transaction.blockItem = updateCredentialsInstance(transaction).toBlockItem();

            return transaction;
        }

        private Payload updateCredentialsInstance(UpdateCredentialsTransaction transaction) throws TransactionCreationException {
            return UpdateCredentials.createNew(
                            transaction.payload).
                    withHeader(TransactionHeader.builder()
                            .sender(transaction.sender)
                            .accountNonce(transaction.nonce.getNonce())
                            .expiry(transaction.expiry.getValue())
                            .build())
                    .signWith(transaction.signer);
        }

    }
}
