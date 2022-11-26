package com.concordium.sdk.transactions;


import com.concordium.sdk.exceptions.TransactionCreationException;
import com.concordium.sdk.types.UInt64;
import lombok.Builder;
import lombok.Getter;
import lombok.val;

import java.util.Objects;

/**
 * Construct a transaction to update signing keys of a specific credential.
 */
@Getter
public class UpdateCredentialKeysTransaction extends AbstractTransaction {
    /**
     * Id of the credential whose keys are to be updated.
     */
    private final CredentialRegistrationId credentialRegistrationID;
    /**
     *  The new public keys
     */
    private final CredentialPublicKeys keys;

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

    /**
     * Maximum energy **allowed** for the transaction to use.
     */
    private final UInt64 maxEnergyCost;
    private BlockItem blockItem;


    @Builder
    public UpdateCredentialKeysTransaction(CredentialRegistrationId credentialRegistrationID,
                                           CredentialPublicKeys keys,
                                           AccountAddress sender,
                                           AccountNonce nonce,
                                           Expiry expiry,
                                           TransactionSigner signer,
                                           BlockItem blockItem,
                                           UInt64 maxEnergyCost) throws TransactionCreationException {
        this.credentialRegistrationID = credentialRegistrationID;
        this.keys = keys;
        this.sender = sender;
        this.nonce = nonce;
        this.expiry = expiry;
        this.signer = signer;
        this.blockItem = blockItem;
        this.maxEnergyCost = maxEnergyCost;
    }

    /**
     * @return A new instance of the {@link UpdateCredentialKeysTransaction}  class.
     */
    public static UpdateCredentialKeysTransactionBuilder builder() {
        return new CustomBuilder();
    }

    @Override
    public BlockItem getBlockItem() {
        return blockItem;
    }

    private static class CustomBuilder extends UpdateCredentialKeysTransactionBuilder {
        @Override
        public UpdateCredentialKeysTransaction build() throws TransactionCreationException {
            val transaction = super.build();
            verifyUpdateCredentialKeysInput(
                    transaction.sender,
                    transaction.nonce,
                    transaction.expiry,
                    transaction.signer,
                    transaction.credentialRegistrationID,
                    transaction.keys
            );
            transaction.blockItem = updateCredentialKeysInstance(transaction).toBlockItem();

            return transaction;
        }


        private Payload updateCredentialKeysInstance(UpdateCredentialKeysTransaction transaction) throws TransactionCreationException {
            return UpdateCredentialKeys.createNew(
                            transaction.credentialRegistrationID,
                            transaction.keys,
                            transaction.maxEnergyCost)
                    .withHeader(TransactionHeader.builder()
                            .sender(transaction.sender)
                            .accountNonce(transaction.nonce.getNonce())
                            .expiry(transaction.expiry.getValue())
                            .build())
                    .signWith(transaction.signer);
        }

        static void verifyUpdateCredentialKeysInput(AccountAddress sender, AccountNonce nonce, Expiry expiry, TransactionSigner signer, CredentialRegistrationId credentialRegistrationID, CredentialPublicKeys keys) throws TransactionCreationException {
            Transaction.verifyCommonInput(sender, nonce, expiry, signer);
            if (Objects.isNull(credentialRegistrationID)) {
                throw TransactionCreationException.from(new IllegalArgumentException("credentialRegistrationID cannot be null"));
            }
            if (Objects.isNull(keys)) {
                throw TransactionCreationException.from(new IllegalArgumentException("keys cannot be null"));
            }
        }

    }
}
