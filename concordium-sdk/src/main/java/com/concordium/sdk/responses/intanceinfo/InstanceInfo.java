package com.concordium.sdk.responses.intanceinfo;

import com.concordium.sdk.responses.transactionstatus.ContractVersion;
import com.concordium.sdk.serializing.JsonMapper;
import com.concordium.sdk.transactions.AccountAddress;
import com.concordium.sdk.transactions.CCDAmount;
import com.concordium.sdk.transactions.Hash;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableList;
import concordium.ConcordiumP2PRpc;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.val;

import java.util.List;
import java.util.Optional;

/**
 * Smart Contract Instance Info.
 */
@Data
@EqualsAndHashCode
public class InstanceInfo {

    private final AccountAddress owner;
    private final CCDAmount amount;
    private final ImmutableList<String> methods;
    private final String name;
    private final Hash sourceModule;
    private final ContractVersion version;

    @JsonCreator
    public InstanceInfo(
            @JsonProperty("owner") AccountAddress owner,
            @JsonProperty("amount") CCDAmount amount,
            @JsonProperty("methods") List<String> methods,
            @JsonProperty("name") String name,
            @JsonProperty("sourceModule") Hash sourceModule,
            @JsonProperty("version") ContractVersion version
    ) {
        this.owner = owner;
        this.amount = amount;
        this.methods = ImmutableList.copyOf(methods);
        this.name = name;
        this.sourceModule = sourceModule;
        this.version = version;
    }

    public static Optional<InstanceInfo> fromJson(ConcordiumP2PRpc.JsonResponse res) {
        try {
            val ret = JsonMapper.INSTANCE.readValue(res.getValue(), InstanceInfo.class);

            return Optional.ofNullable(ret);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Could not deserialize Contract Instance Info JSON");
        }
    }
}
