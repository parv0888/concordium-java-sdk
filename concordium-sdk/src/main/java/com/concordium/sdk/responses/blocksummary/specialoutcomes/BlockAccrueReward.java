package com.concordium.sdk.responses.blocksummary.specialoutcomes;

import com.concordium.sdk.responses.AccountIndex;
import com.concordium.sdk.transactions.CCDAmount;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Amounts accrued to accounts for each baked block.
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public final class BlockAccrueReward extends SpecialOutcome {
    /**
     * The total fees paid for transactions in the block.
     */
    private final CCDAmount transactionFees;

    /**
     * The old balance of the GAS account.
     */
    private final CCDAmount oldGASAccount;

    /**
     * The new balance of the GAS account.
     */
    private final CCDAmount newGASAccount;

    /**
     * The amount awarded to the baker.
     */
    private final CCDAmount bakerReward;

    /**
     * The amount awarded to the passive delegators.
     */
    private final CCDAmount passiveReward;

    /**
     * The amount awarded to the foundation.
     */
    private final CCDAmount foundationCharge;

    /**
     * The baker of the block, who will receive the award.
     */
    private final AccountIndex bakerId;

    BlockAccrueReward(
            @JsonProperty("transactionFees") CCDAmount transactionFees,
            @JsonProperty("oldGASAccount") CCDAmount oldGASAccount,
            @JsonProperty("newGASAccount") CCDAmount newGASAccount,
            @JsonProperty("bakerReward") CCDAmount bakerReward,
            @JsonProperty("passiveReward") CCDAmount passiveReward,
            @JsonProperty("foundationCharge") CCDAmount foundationCharge,
            @JsonProperty("bakerId") AccountIndex bakerId) {
        this.transactionFees = transactionFees;
        this.oldGASAccount = oldGASAccount;
        this.newGASAccount = newGASAccount;
        this.bakerReward = bakerReward;
        this.passiveReward = passiveReward;
        this.foundationCharge = foundationCharge;
        this.bakerId = bakerId;
    }
}
