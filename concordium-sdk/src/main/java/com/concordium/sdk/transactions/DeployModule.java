package com.concordium.sdk.transactions;

import com.concordium.sdk.transactions.smartcontracts.WasmModule;
import com.concordium.sdk.types.UInt64;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DeployModule extends Payload {
    /**
     * A compiled Smart Contract Module in WASM with source and version.
     */
    private final WasmModule module;

    private final UInt64 maxEnergyCost;

    @Builder
    private DeployModule(WasmModule module, UInt64 maxEnergyCost) {
        this.module = module;
        this.maxEnergyCost = maxEnergyCost;
    }

    /**
     * It creates a new instance of the DeployModule class.
     *
     * @param module        The module to be deployed.
     * @param maxEnergyCost The maximum amount of energy that can be consumed by the contract.
     * @return A new DeployModule object.
     */
    static DeployModule createNew(WasmModule module, UInt64 maxEnergyCost) {
        return new DeployModule(module, maxEnergyCost);
    }

    /**
     * This function returns the type of payload that this class represents.
     */
    @Override
    public PayloadType getType() {
        return PayloadType.DEPLOY_MODULE;
    }

    /**
     * This function returns the bytecode of the module.
     *
     * @return The byte array of the module.
     */
    @Override
    byte[] getBytes() {
        return module.getBytes();
    }

    @Override
    UInt64 getTransactionTypeCost() {
        return this.maxEnergyCost;
    }
}
