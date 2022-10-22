package com.concordium.sdk.crypto.encryptedtransfers;

import com.concordium.sdk.responses.accountinfo.AccountEncryptedAmount;
import com.concordium.sdk.transactions.EncryptedAmount;
import com.concordium.sdk.transactions.EncryptedAmountIndex;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
class IndexedEncryptedAmount {
    private final EncryptedAmount encryptedChunks;
    private final EncryptedAmountIndex index;

    public static IndexedEncryptedAmount from(AccountEncryptedAmount encryptedAmount) {
        return new IndexedEncryptedAmount(
                encryptedAmount.getSelfAmount(),
                encryptedAmount.getStartIndex()
        );
    }
}