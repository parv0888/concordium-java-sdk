package com.concordium.sdk.transactions;


import lombok.Getter;
import lombok.ToString;
import lombok.val;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.TreeMap;

/**
 * Data needed to update the account's credentials.
 */
@ToString
@Getter
public class UpdateCredentialsPayload {

    private final Map<Integer, CredentialDeploymentInfo> newCredInfos = new TreeMap<>();

    /**
     * Ids of credentials to remove.
     */
    private final CredentialRegistrationId[] removeCredIds;
    /**
     * The new account threshold.
     */
    private final int newThreshold;

    private UpdateCredentialsPayload(CredentialRegistrationId[] removeCredIds, int newThreshold) {
        this.removeCredIds = removeCredIds;
        this.newThreshold = newThreshold;
    }

    /**
     * @return buffer bytes of UpdateCredentialsPayload
     */
    public static UpdateCredentialsPayload from(CredentialRegistrationId[] removeCredIds, int threshold) {
        return new UpdateCredentialsPayload(removeCredIds, threshold);
    }

    public byte[] getBytes() {
        int removeCredIdsBufferLength = TransactionType.BYTES;
        val removeCredIdsLength = removeCredIds.length;
        for (CredentialRegistrationId credId : removeCredIds) {
            removeCredIdsBufferLength += credId.getRegId().length;
        }

        val buffer = ByteBuffer.allocate(removeCredIdsBufferLength +
                TransactionType.BYTES);

        buffer.put((byte) removeCredIdsLength);
        for (CredentialRegistrationId credId : removeCredIds) {
            buffer.put(credId.getRegId());
        }

        buffer.put((byte) this.newThreshold);
        return new byte[0];
    }
}
