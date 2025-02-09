package com.concordium.sdk.crypto;

public class CryptoJniNative {

    /**
     * Signs a message using the provided private key.
     *
     * @param privateKey The private key to use for signing.
     * @param message The message to sign.
     * @param out The buffer to write the signature to.
     * @return 0 on success, non-zero on error.
     */
    public static native int sign(byte[] privateKey, byte[] message, byte[] out);

    /**
     * Verifies a signature for a message using the provided public key.
     *
     * @param publicKey The public key to use for verification.
     * @param message The message to verify the signature for.
     * @param signature The signature to verify.
     * @return 0 if the signature is valid, non-zero on error or if the signature is invalid.
     */
    public static native int verify(byte[] publicKey, byte[] message, byte[] signature);

    /**
     * Generates a new secret key and stores it in the provided buffer.
     *
     * @param buffer The buffer to write the secret key to.
     * @return 0 on success, non-zero on error.
     */
    public static native int generateSecretKey(byte[] buffer);

    /**
     * Generates the public key corresponding to the given secret key, and stores it in the provided buffer.
     *
     * @param secretKey The secret key to generate the public key for.
     * @param buffer The buffer to write the public key to.
     * @return 0 on success, non-zero on error.
     */
    public static native int generatePublicKey(byte[] secretKey, byte[] buffer);

    /**
     * Creates a transfer from the encrypted amount to a public account payload, using the provided input string.
     *
     *
     * @param input The input string to use for creating the transfer.
     * @return The transfer data as a JSON string.
     */
    public static native String createSecToPubTransfer(String input);

    /**
     * Generates an encrypted transfer payload, using the provided input string.
     *
     * @param input The input string to use for generating the encrypted transfer.
     * @return The encrypted transfer data as a JSON string.
     */
    public static native String generateEncryptedTransfer(String input);


}
