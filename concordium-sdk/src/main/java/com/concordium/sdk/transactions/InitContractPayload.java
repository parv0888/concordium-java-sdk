package com.concordium.sdk.transactions;

import lombok.Getter;
import lombok.ToString;
import lombok.val;

import java.nio.ByteBuffer;


/**
 * Data needed to initialize a smart contract.
 */
@ToString
@Getter
public final class InitContractPayload {

    /**
     * The amount to be deposited
     */
    private final CCDAmount amount;
    /**
     * Reference to the module to initialize the instance.
     */
    private final Hash moduleRef;
    /**
     * Name of the contract in the module.
     */
    private final InitName initName;
    /**
     * Message to invoke the initialization method with.
     */
    private final Parameter param;

    private InitContractPayload(CCDAmount amount, Hash moduleRef, InitName initName, Parameter param) {
        this.amount = amount;
        this.moduleRef = moduleRef;
        this.initName = initName;
        this.param = param;
    }

    /**
     * Create a new instance of {@link InitContractPayload}, from the given parameters
     *
     * @param amount       CCD amount to deposit
     * @param moduleRef    Hash of smart contract module reference.
     * @param contractName Name of the contract in the module. Expected format: "init_<contract_name>"
     * @param parameter    Message to invoke the initialization method with.
     */
    public static InitContractPayload from(int amount, byte[] moduleRef, String contractName, byte[] parameter) {
        return new InitContractPayload(
                CCDAmount.fromMicro(amount),
                Hash.from(moduleRef),
                InitName.from(contractName),
                Parameter.from(parameter)
        );
    }

    /**
     * @return buffer bytes of InitContractPayload
     */
    public byte[] getBytes() {
        val amountBytes = amount.getBytes();
        val moduleRefBytes = moduleRef.getBytes();
        val initNameBytes = initName.getBytes();
        val param_bytes = param.getBytes();
        val bufferLength = TransactionType.BYTES +
                moduleRefBytes.length +
                amountBytes.length +
                initNameBytes.length +
                param_bytes.length;

        val buffer = ByteBuffer.allocate(bufferLength);

        buffer.put(TransactionType.INITIALIZE_SMART_CONTRACT_INSTANCE.getValue());
        buffer.put(amountBytes);
        buffer.put(moduleRefBytes);
        buffer.put(initNameBytes);
        buffer.put(param_bytes);

        return buffer.array();
    }
}
