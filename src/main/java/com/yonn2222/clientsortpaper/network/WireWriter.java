package com.yonn2222.clientsortpaper.network;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public final class WireWriter {
    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    public WireWriter writeVarInt(int value) {
        int rest = value;
        while ((rest & ~0x7F) != 0) {
            out.write((rest & 0x7F) | 0x80);
            rest >>>= 7;
        }
        out.write(rest);
        return this;
    }

    public WireWriter writeInt(int value) {
        out.write(value >>> 24);
        out.write(value >>> 16);
        out.write(value >>> 8);
        out.write(value);
        return this;
    }

    public WireWriter writeBoolean(boolean value) {
        out.write(value ? 1 : 0);
        return this;
    }

    public WireWriter writeVarIntArray(int[] values) {
        writeVarInt(values.length);
        for (int value : values) {
            writeVarInt(value);
        }
        return this;
    }

    public WireWriter writeString(String value) {
        String limited = value.length() > WireReader.MAX_STRING ? value.substring(0, WireReader.MAX_STRING) : value;
        byte[] bytes = limited.getBytes(StandardCharsets.UTF_8);
        writeVarInt(bytes.length);
        out.write(bytes, 0, bytes.length);
        return this;
    }

    public byte[] toByteArray() {
        return out.toByteArray();
    }
}
