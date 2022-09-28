use anyhow::bail;
use concordium_contracts_common::Amount;
use encrypted_transfers::types::{
    AggregatedDecryptedAmount, IndexedEncryptedAmount, EncryptedAmount
};
use id::{constants::ArCurve, types::GlobalContext};
use rand::thread_rng;
use serde_json::{from_str, from_value, to_string, Value};
use crypto_common::Get;


use core::convert::Into;
use core::slice;
use ed25519_dalek::*;

use jni::{
    objects::{JClass, JString},
    sys::{jbyteArray, jint},
    JNIEnv,
};
use std::i8;

use std::convert::{From, TryFrom};
use std::io::Cursor;

const SUCCESS: i32 = 0;
const NATIVE_CONVERSION_ERROR: i32 = 1;
const MALFORMED_SECRET_KEY: i32 = 2;
const MALFORMED_PUBLIC_KEY: i32 = 3;
const SIGNATURE_VERIFICATION_FAILURE: i32 = 4;
const SECRET_KEY_GENERATION_FAILURE: i32 = 5;
const PUBLIC_KEY_GENERATION_FAILURE: i32 = 6;

#[no_mangle]
#[allow(non_snake_case)]
pub extern "system" fn Java_com_concordium_sdk_crypto_ed25519_ED25519_sign(
    env: JNIEnv,
    _class: JClass,
    secretKeyBytes: jbyteArray,
    messageBytes: jbyteArray,
    out: jbyteArray,
) -> jint {
    let secretKeyBytes = match env.convert_byte_array(secretKeyBytes) {
        Ok(x) => x,
        _ => return NATIVE_CONVERSION_ERROR,
    };

    let secret_key = match SecretKey::from_bytes(&secretKeyBytes) {
        Ok(sk) => sk,
        _ => return MALFORMED_SECRET_KEY,
    };

    let public_key: PublicKey = (&secret_key).into();

    let bytesToSign = match env.convert_byte_array(messageBytes) {
        Ok(x) => x,
        _ => return NATIVE_CONVERSION_ERROR,
    };

    let expanded_secret_key = ExpandedSecretKey::from(&secret_key);
    let signature = expanded_secret_key.sign(&bytesToSign, &public_key);
    let signatureBytesU8 = signature.to_bytes();

    let signatureBytesI8: &[i8] = unsafe {
        slice::from_raw_parts(
            signatureBytesU8.as_ptr() as *const i8,
            signatureBytesU8.len(),
        )
    };

    match env.set_byte_array_region(out, 0, signatureBytesI8) {
        Ok(_) => SUCCESS,
        _ => NATIVE_CONVERSION_ERROR,
    }
}

#[no_mangle]
#[allow(non_snake_case)]
pub extern "system" fn Java_com_concordium_sdk_crypto_ed25519_ED25519_verify(
    env: JNIEnv,
    _class: JClass,
    pub_key_bytes: jbyteArray,
    msg_bytes: jbyteArray,
    sig_bytes: jbyteArray,
) -> jint {
    let public_key_bytes = match env.convert_byte_array(pub_key_bytes) {
        Ok(x) => x,
        _ => return NATIVE_CONVERSION_ERROR,
    };
    let public_key = match PublicKey::from_bytes(&public_key_bytes) {
        Ok(x) => x,
        _ => return MALFORMED_PUBLIC_KEY,
    };

    let message_bytes = match env.convert_byte_array(msg_bytes) {
        Ok(x) => x,
        _ => return NATIVE_CONVERSION_ERROR,
    };

    let signature_bytes = match env.convert_byte_array(sig_bytes) {
        Ok(x) => x,
        _ => return NATIVE_CONVERSION_ERROR,
    };

    let signature: Signature = match Signature::try_from(&signature_bytes[..]) {
        Ok(sig) => sig,
        _ => return NATIVE_CONVERSION_ERROR,
    };

    match public_key.verify(&message_bytes, &signature) {
        Ok(_) => SUCCESS,
        _ => SIGNATURE_VERIFICATION_FAILURE,
    }
}

#[no_mangle]
pub extern "system" fn Java_com_concordium_sdk_crypto_ed25519_ED25519_generateSecretKey(
    env: JNIEnv,
    _class: JClass,
    out: jbyteArray,
) -> jint {
    let mut csprng = rand::rngs::OsRng {};
    let secret_key = SecretKey::generate(&mut csprng);
    let secret_key_bytes = secret_key.to_bytes();

    let secret_key_bytes_i8: &[i8] = unsafe {
        slice::from_raw_parts(
            secret_key_bytes.as_ptr() as *const i8,
            secret_key_bytes.len(),
        )
    };

    match env.set_byte_array_region(out, 0, secret_key_bytes_i8) {
        Ok(_) => SUCCESS,
        _ => SECRET_KEY_GENERATION_FAILURE,
    }
}

#[no_mangle]
#[allow(non_snake_case)]
pub extern "system" fn Java_com_concordium_sdk_crypto_ed25519_ED25519_generatePublicKey(
    env: JNIEnv,
    _class: JClass,
    secretKeyBytes: jbyteArray,
    out: jbyteArray,
) -> jint {
    let secretKeyBytes = match env.convert_byte_array(secretKeyBytes) {
        Ok(x) => x,
        _ => return NATIVE_CONVERSION_ERROR,
    };

    let secret_key = match SecretKey::from_bytes(&secretKeyBytes) {
        Ok(sk) => sk,
        _ => return MALFORMED_SECRET_KEY,
    };

    let public_key: PublicKey = (&secret_key).into();
    let public_key_bytes = public_key.to_bytes();

    let public_key_bytes_i8: &[i8] = unsafe {
        slice::from_raw_parts(
            public_key_bytes.as_ptr() as *const i8,
            public_key_bytes.len(),
        )
    };

    match env.set_byte_array_region(out, 0, public_key_bytes_i8) {
        Ok(_) => SUCCESS,
        _ => PUBLIC_KEY_GENERATION_FAILURE,
    }
}


/// Try to extract a field with a given name from the JSON value.
fn try_get<A: serde::de::DeserializeOwned>(v: &Value, fname: &str) -> anyhow::Result<A> {
    match v.get(fname) {
        Some(v) => Ok(from_value(v.clone())?),
        None => bail!(format!("Field {} not present, but should be.", fname)),
    }
}

#[no_mangle]
#[allow(non_snake_case)]
/// The JNI wrapper for the `create_sec_to_pub_transfer` method.
/// The `input` parameter must be a properly initalized `java.lang.String` that
/// is non-null. The input must be valid JSON according to specified format
pub extern "system" fn Java_com_concordium_sdk_crypto_ed25519_ED25519_generateEncryptedTransfer(
    env: JNIEnv,
    _: JClass,
    input: JString,
    out: jbyteArray,
) -> jint {
    let v: Value = match env.get_string(input) {
        Ok(s) => match s.to_str() {
            Ok(js) => match from_str(js) {
                Ok(v) => v,
                // Err(_) => return NATIVE_CONVERSION_ERROR,
                Err(_) => return 1,
            },
            // Err(_) => return NATIVE_CONVERSION_ERROR,
            Err(_) => return 2,
        },
        // Err(_) => return NATIVE_CONVERSION_ERROR,
        Err(_) => return 3,
    };

    // context with parameters
    let global_context: GlobalContext<ArCurve> = match try_get(&v, "global") {
        Ok(v) => v,
        // Err(_) => return NATIVE_CONVERSION_ERROR,
        Err(_) => return 4,
    };

    let receiver_pk: elgamal::PublicKey<ArCurve> = match try_get(&v, "receiverPublicKey") {
        Ok(receiver_pk) => receiver_pk,
        // Err(_) => return NATIVE_CONVERSION_ERROR,
        Err(_) => return 5,
    };

    let sender_sk: elgamal::SecretKey<ArCurve> = match try_get(&v, "senderSecretKey") {
        Ok(sender_sk) => sender_sk,
        // Err(_) => return NATIVE_CONVERSION_ERROR,
        Err(_) => return 6,
    };

    let input_amount: AggregatedDecryptedAmount<ArCurve> =
        match try_get::<IndexedEncryptedAmount<ArCurve>>(&v, "inputAmount") {
            Ok(a) => AggregatedDecryptedAmount {
                agg_encrypted_amount: a.encrypted_chunks.clone(),
                agg_index: encrypted_transfers::types::EncryptedAmountAggIndex {
                    index: a.index.index,
                },
                agg_amount: match decrypt_encrypted_amount(a.encrypted_chunks, sender_sk.clone()) {
                    Ok(amount) => amount,
                    Err(err) => {
                        to_ret(err.to_string(), env, out);
                        return 8;
                    }
                },
            },
            // Err(_) => return NATIVE_CONVERSION_ERROR,
            Err(err) => {
                to_ret(err.to_string(), env, out);
                return 7;
            }
        };

    // plaintext amount to transfer
    let amount_to_send: Amount = match try_get(&v, "amountToSend") {
        Ok(amount_to_send) => amount_to_send,
        // Err(_) => return NATIVE_CONVERSION_ERROR,
        Err(_) => return 8,
    };

    // Should be safe on iOS and Android, by calling SecRandomCopyBytes/getrandom,
    // respectively.
    let mut csprng = thread_rng();

    let payload = encrypted_transfers::make_transfer_data(
        &global_context,
        &receiver_pk,
        &sender_sk,
        &input_amount,
        amount_to_send,
        &mut csprng,
    );

    let payload = match payload {
        Some(p) => p,
        None => {
            // return NATIVE_CONVERSION_ERROR;
            return 9;
        }
    };

    let json_str = match to_string(&payload) {
        Ok(s) => s,
        // Err(_) => return NATIVE_CONVERSION_ERROR,
        Err(_) => return 10,
    };

    to_ret(json_str, env, out)
}

fn to_ret(json_str: String, env: JNIEnv, out: *mut jni::sys::_jobject) -> i32 {
    let json_str_bytes = json_str.as_bytes();
    let json_str_bytes_i8 = to_i8_slice(json_str_bytes);
    match env.set_byte_array_region(out, 0, json_str_bytes_i8) {
        Ok(_) => SUCCESS,
        // _ => NATIVE_CONVERSION_ERROR,
        _ => 10,
    }
}

fn to_i8_slice(json_str_bytes: &[u8]) -> &[i8] {
    let json_str_bytes_i8: &[i8] = unsafe {
        slice::from_raw_parts(json_str_bytes.as_ptr() as *const i8, json_str_bytes.len())
    };

    json_str_bytes_i8
}


static TABLE_BYTES: &[u8] = include_bytes!("table_bytes.bin");

fn decrypt_encrypted_amount(
    encrypted_amount: EncryptedAmount<ArCurve>,
    secret: elgamal::SecretKey<ArCurve>,
) -> anyhow::Result<Amount> {
    let table = (&mut Cursor::new(TABLE_BYTES)).get()?;
    Ok(
        encrypted_transfers::decrypt_amount::<id::constants::ArCurve>(
            &table,
            &secret,
            &encrypted_amount,
        ),
    )
}
