package com.concordium.sdk.transactions;

import com.concordium.sdk.types.UInt16;
import com.concordium.sdk.types.UInt64;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.val;

import java.nio.ByteBuffer;

/**
 * Transfer an amount with schedule.
 */

@ToString
@Getter
@EqualsAndHashCode(callSuper = true)
public class TransferSchedule extends Payload {
    /**
     * The account address of the recepient.
     */
    private final AccountAddress to;
    /**
     * The release schedule. This can be at most 255 elements.
     */
    private final Schedule[] amount;

    public TransferSchedule(AccountAddress to, Schedule[] amount) {
        this.to = to;
        this.amount = amount;
    }

    static TransferSchedule createNew(AccountAddress to, Schedule[] amount) {
        return new TransferSchedule(to, amount);
    }

    /**
     * This is a method that returns the type of the payload.
     */
    @Override
    public PayloadType getType() {
        return PayloadType.TRANSFER_WITH_SCHEDULE;
    }

    /**
     * This function returns the transaction type of the transaction.
     */
    public byte getTransactionType() {
        return TransactionType.TRANSFER_WITH_SCHEDULE.getValue();
    }

    /**
     * @return The byte array of the transaction.
     */
    @Override
    byte[] getBytes() {
        val scheduleLen = amount.length;
        val scheduleBufferSize = UInt64.BYTES * scheduleLen * 2;

        val buffer = ByteBuffer.allocate(TransactionType.BYTES + TransactionType.BYTES + AccountAddress.BYTES + scheduleBufferSize);
        buffer.put(this.getTransactionType());

        buffer.put(to.getBytes());

        buffer.put((byte) scheduleLen);
        for (int i = 0; i < scheduleLen; i++) {
            val schedule_buffer = amount[i].getBytes();
            buffer.put(schedule_buffer);
        }
        return buffer.array();
    }

    @Override
    UInt64 getTransactionTypeCost() {
        UInt16 scheduleLen = UInt16.from(amount.length);
        val maxEnergyCost = UInt64.from(scheduleLen.getValue()).getValue() * (300 + 64);
        return UInt64.from(maxEnergyCost);
    }
}
