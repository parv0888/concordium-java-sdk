package com.concordium.sdk.transactions.encryptedtransfer;

import com.concordium.sdk.crypto.bulletproof.BulletproofGenerators;
import com.concordium.sdk.crypto.pedersencommitment.PedersenCommitmentKey;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GlobalContext {
    private final PedersenCommitmentKey onChainCommitmentKey;
    private final BulletproofGenerators bulletproofGenerators;
    private final String genesisString;
}
