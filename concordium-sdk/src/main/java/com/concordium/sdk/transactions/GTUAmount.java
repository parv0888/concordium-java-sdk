package com.concordium.sdk.transactions;

import com.concordium.sdk.types.UInt64;
import lombok.Getter;

import java.nio.ByteBuffer;

@Getter
public class GTUAmount {
    private final UInt64 value;

    private GTUAmount(UInt64 value) {
        this.value = value;
    }

    public static GTUAmount fromMicro(long val) {
        return new GTUAmount(UInt64.from(val));
    }

    public static GTUAmount fromMicro(String val) {
        return new GTUAmount(UInt64.from(val));
    }

    byte[] getBytes() {
        return value.getBytes();
    }

    public static GTUAmount fromBytes(ByteBuffer source) {
        UInt64 value = UInt64.fromBytes(source);
        return new GTUAmount(value);
    }
    @Override
    public String toString() {
        return value.toString();
    }
}
