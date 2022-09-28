package com.concordium.sdk.transactions;

import com.concordium.sdk.Client;
import com.concordium.sdk.Connection;
import com.concordium.sdk.Credentials;
import com.concordium.sdk.crypto.bulletproof.BulletproofGenerators;
import com.concordium.sdk.crypto.pedersencommitment.PedersenCommitmentKey;
import com.concordium.sdk.requests.getaccountinfo.AccountRequest;
import com.concordium.sdk.responses.consensusstatus.ConsensusStatus;
import com.concordium.sdk.transactions.encryptedtransfer.AggregatedDecryptedAmount;
import com.concordium.sdk.transactions.encryptedtransfer.GlobalContext;
import com.concordium.sdk.transactions.encryptedtransfer.EncryptedAmountTransferDataJniInput;
import com.concordium.sdk.types.UInt64;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;

public class EncryptedTransferUtilTest {

    @SneakyThrows
    @Test
    public void testEncryptedTransferUtil() {
        Connection connection = Connection.builder()
                .credentials(Credentials.from("rpcadmin"))
                .host("127.0.0.1")
                .port(10001)
                .build();
        Client client = Client.from(connection);
        ConsensusStatus accountConcensusStatus = client.getConsensusStatus();

        Hash blockHash = accountConcensusStatus.getBestBlock();
        val accountAddress = AccountAddress.from("48x2Uo8xCMMxwGuSQnwbqjzKtVqK5MaUud4vG7QEUgDmYkV85e");
        val accountInfo = client.getAccountInfo(AccountRequest.from(accountAddress), blockHash);
        long nonceValue = accountInfo.getAccountNonce().getValue().getValue();
        long expiry = System.currentTimeMillis() / 1000 + 500;
        val cryptographicParameters = client.getCryptographicParameters(blockHash);

        val toAccountAddress = AccountAddress.from("3aYdMM4CFQSH7P16sCN51p3J6TYhACgV9EYBeVJoLTfZxRFwp4");
        val accountInfo2 = client.getAccountInfo(AccountRequest.from(toAccountAddress), blockHash);
        val receiver_pk = accountInfo2.getAccountEncryptionKey();

        val x = EncryptedAmountTransferDataJniInput.builder()
                .global(GlobalContext.builder()
                        .genesisString(cryptographicParameters.getGenesisString())
                        .bulletproofGenerators(BulletproofGenerators.from(cryptographicParameters.getBulletproofGenerators()))
                        .onChainCommitmentKey(PedersenCommitmentKey.from(cryptographicParameters.getOnChainCommitmentKey()))
                        .build())
                .receiverPublicKey(receiver_pk)
                .senderSecretKey("b14cbfe44a02c6b1f78711176d5f437295367aa4f2a8c2551ee10d25a03adc69d61a332a058971919dad7312e1fc94c573c28a63523116b128d7d33037cdbdf5bf6a30048fa27a121b4d950d1f5caecc")
                .amountToSend("1")
                .inputAmount(AggregatedDecryptedAmount.builder()
                        .encryptedChunks(accountInfo.getAccountEncryptedAmount().getSelfAmount())
                        .index(accountInfo.getAccountEncryptedAmount().getStartIndex())
                        .build())
                .build();
        val payload = EncryptedTransferPayload.from(toAccountAddress, EncryptedAmountTransferData.from(x));
        EncryptedTransferTransaction transaction = TransactionFactory.newEncryptedTransfer()
                .sender(accountAddress)
                .nonce(AccountNonce.from(nonceValue))
                .expiry(Expiry.from(expiry))
                .signer(TransactionTestHelper.getValidSigner())
                .payload(payload)
                .maxEnergyCost(UInt64.from(30000))
                .build();

        val txnHash = client.sendTransaction(transaction);
        Assert.assertEquals(Hash.from(""), txnHash);
    }
}
