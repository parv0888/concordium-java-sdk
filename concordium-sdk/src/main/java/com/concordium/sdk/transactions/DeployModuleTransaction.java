package com.concordium.sdk.transactions;

import com.concordium.sdk.exceptions.TransactionCreationException;
import com.concordium.sdk.transactions.smartcontracts.WasmModule;
import com.concordium.sdk.types.UInt64;
import lombok.Builder;
import lombok.Getter;
import lombok.val;

import java.util.Objects;

@Getter
public class DeployModuleTransaction extends AbstractTransaction {

    private final AccountAddress sender;
    private final AccountNonce nonce;
    private final Expiry expiry;
    private final TransactionSigner signer;
    private final WasmModule module;
    private final UInt64 maxEnergyCost;
    private BlockItem blockItem;

    @Builder
    DeployModuleTransaction(AccountAddress sender, AccountNonce nonce, Expiry expiry, TransactionSigner signer, WasmModule module, UInt64 maxEnergyCost) throws TransactionCreationException {
        this.sender = sender;
        this.nonce = nonce;
        this.expiry = expiry;
        this.signer = signer;
        this.module = module;
        this.maxEnergyCost = maxEnergyCost;
    }

    @Override
    public BlockItem getBlockItem() {
        return blockItem;
    }

    public static DeployModuleTransaction.DeployModuleTransactionBuilder builder() {
        return new DeployModuleTransaction.CustomBuilder();
    }

    private static class CustomBuilder extends DeployModuleTransaction.DeployModuleTransactionBuilder {
        @Override
        public DeployModuleTransaction build() throws TransactionCreationException {
            val transaction = super.build();
            Transaction.verifyDeployModuleInput(transaction.sender, transaction.nonce, transaction.expiry, transaction.module, transaction.signer);
            if (Objects.isNull(transaction.module)) {
                throw TransactionCreationException.from(new IllegalArgumentException("WasmModule cannot be null"));
            }
            transaction.blockItem = createNewTransaction(transaction).toBlockItem();
            return transaction;
        }

        private Payload createNewTransaction(DeployModuleTransaction transaction) throws TransactionCreationException {
            return DeployModule.createNew(transaction.getModule(), transaction.maxEnergyCost)
                    .withHeader(TransactionHeader.builder()
                            .sender(transaction.getSender())
                            .accountNonce(transaction.getNonce().getNonce())
                            .expiry(transaction.getExpiry().getValue())
                            .build())
                    .signWith(transaction.getSigner());
        }
    }
}
