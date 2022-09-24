package com.concordium.sdk.transactions;

import com.concordium.sdk.Client;
import com.concordium.sdk.Connection;
import com.concordium.sdk.Credentials;
import com.concordium.sdk.exceptions.AccountNotFoundException;
import com.concordium.sdk.exceptions.ClientInitializationException;
import com.concordium.sdk.exceptions.TransactionCreationException;
import com.concordium.sdk.exceptions.TransactionRejectionException;
import com.concordium.sdk.requests.getaccountinfo.AccountRequest;
import com.concordium.sdk.responses.accountinfo.AccountInfo;
import com.concordium.sdk.responses.consensusstatus.ConsensusStatus;
import com.concordium.sdk.transactions.smartcontracts.WasmModule;
import com.concordium.sdk.types.UInt64;
import lombok.val;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.fail;

public class DeployModuleUtilTest {

    @Test
    public void testDeployModuleUtil() {
        try {
            Connection connection = Connection.builder()
                    .credentials(Credentials.from("rpcadmin"))
                    .host("0.0.0.0")
                    .port(10001)
                    .build();
            Client client = Client.from(connection);
            ConsensusStatus accountConcensusStatus = client.getConsensusStatus();

            Hash blockHash = accountConcensusStatus.getBestBlock();
            AccountRequest accountRequest = AccountRequest.from(AccountAddress.from("48x2Uo8xCMMxwGuSQnwbqjzKtVqK5MaUud4vG7QEUgDmYkV85e"));
            AccountInfo accountInfo;
            try {
                accountInfo = client.getAccountInfo(accountRequest, blockHash);
            } catch (AccountNotFoundException e) {
                throw new RuntimeException(e);
            }
            long nonceValue = accountInfo.getAccountNonce().getValue().getValue();
            long expiry = System.currentTimeMillis()/1000 + 500;

            byte[] array = Files.readAllBytes(Paths.get("path_to_wasm_file"));

            val transaction = TransactionFactory.newDeployModule()
                    .sender(AccountAddress.from("48x2Uo8xCMMxwGuSQnwbqjzKtVqK5MaUud4vG7QEUgDmYkV85e"))
                    .nonce(AccountNonce.from(nonceValue))
                    .expiry(Expiry.from(expiry))
                    .signer(TransactionTestHelper.getValidSigner())
                    .module(WasmModule.from(array, 1))
                    .maxEnergyCost(UInt64.from(10000))
                    .build();
            client.sendTransaction(transaction);

        } catch (TransactionCreationException e) {
            fail("Unexpected error: " + e.getMessage());
        } catch (TransactionRejectionException | ClientInitializationException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}