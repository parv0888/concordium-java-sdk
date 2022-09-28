package com.concordium.sdk.crypto.ed25519;

public class EncryptedTransferSupport {
    public static byte[] encrypted_transfer_support(String input) throws Exception {
        return ED25519.encrypted_transfer_support(input);
    }
}
