package com.concordium.sdk.crypto.ed25519;

import com.concordium.sdk.exceptions.ED25519Exception;
import lombok.val;

final class ED25519 {

    static {
        loadNatives();
    }

    public static void loadNatives() {
        NativeResolver.loadLib();
    }

    static final int KEY_SIZE = 32;

    static final int SIGNATURE_SIZE = 64;

    static byte[] sign(ED25519SecretKey secretKey, byte[] message) throws ED25519Exception {
        val buff = new byte[SIGNATURE_SIZE];
        val resultCode = ED25519ResultCode.from(sign(secretKey.getBytes(), message, buff));
        if (resultCode.failed()) {
            throw ED25519Exception.from(resultCode);
        }
        return buff;
    }

    static boolean verify(ED25519PublicKey publicKey, byte[] message, byte[] signature) throws ED25519Exception {
        val resultCode = ED25519ResultCode.from(verify(publicKey.getBytes(), message, signature));
        if (resultCode.failed()) {
            throw ED25519Exception.from(resultCode);
        }
        return true;
    }

    static ED25519SecretKey makeSecretKey() throws ED25519Exception {
        val buff = new byte[KEY_SIZE];
        val resultCode = ED25519ResultCode.from(generateSecretKey(buff));
        if (resultCode.failed()) {
            throw ED25519Exception.from(resultCode);
        }
        return ED25519SecretKey.from(buff);
    }

    static ED25519PublicKey makePublicKey(ED25519SecretKey secretKey) throws ED25519Exception {
        val secretKeyBytes = secretKey.getBytes();
        val buff = new byte[KEY_SIZE];
        val resultCode = ED25519ResultCode.from(generatePublicKey(secretKeyBytes, buff));
        if (resultCode.failed()) {
            throw ED25519Exception.from(resultCode);
        }
        return ED25519PublicKey.from(buff);
    }

    static byte[] encrypted_transfer_support(String input) throws Exception {
        val buff = new byte[10000];
        val resultCode = generateEncryptedTransfer(input, buff);
        if (resultCode > 0) {
            val errString = new java.lang.String(buff);
            throw new Exception("Error: " + errString + ", ErrorCode: " + resultCode);
        }

        return buff;
    }

    private static native int sign(byte[] privateKey, byte[] message, byte[] out);

    private static native int verify(byte[] publicKey, byte[] message, byte[] signature);

    private static native int generateSecretKey(byte[] buffer);

    private static native int generatePublicKey(byte[] secretKey, byte[] buffer);

    private static native int generateEncryptedTransfer(String input, byte[] buffer);
}
