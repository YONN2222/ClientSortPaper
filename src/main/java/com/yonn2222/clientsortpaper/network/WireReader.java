package com.yonn2222.clientsortpaper.network;

import java.nio.charset.StandardCharsets;

public final class WireReader {
    public static final int MAX_STRING = 32767;

    private final byte[] data;
    private int position;

    public WireReader(byte[] data) {
        this.data = data;
    }

    public int remaining() {
        return data.length - position;
    }

    public int readVarInt() throws PayloadException {
        int value = 0;
        int shift = 0;
        while (true) {
            if (position >= data.length) {
                throw PayloadException.invalid("Payload ended inside a VarInt");
            }
            byte next = data[position++];
            value |= (next & 0x7F) << shift;
            if ((next & 0x80) == 0) {
                return value;
            }
            shift += 7;
            if (shift >= 35) {
                throw PayloadException.invalid("VarInt is too big");
            }
        }
    }

    public int readInt() throws PayloadException {
        if (remaining() < 4) {
            throw PayloadException.invalid("Payload ended inside an int");
        }
        int value = (data[position] & 0xFF) << 24 | (data[position + 1] & 0xFF) << 16
                | (data[position + 2] & 0xFF) << 8 | (data[position + 3] & 0xFF);
        position += 4;
        return value;
    }

    public boolean readBoolean() throws PayloadException {
        if (remaining() < 1) {
            throw PayloadException.invalid("Payload ended inside a boolean");
        }
        return data[position++] != 0;
    }

    public int[] readVarIntArray(int maxLength) throws PayloadException {
        int length = readVarInt();
        if (length < 0 || length > maxLength) {
            throw PayloadException.invalid("Array length " + length + " is outside 0 to " + maxLength);
        }

        if (length > remaining()) {
            throw PayloadException.invalid("Array is longer than the payload");
        }
        int[] values = new int[length];
        for (int i = 0; i < length; i++) {
            values[i] = readVarInt();
        }
        return values;
    }

    public String readString(int maxChars) throws PayloadException {
        int length = readVarInt();
        if (length < 0 || length > maxChars * 3) {
            throw PayloadException.invalid("String is too long");
        }
        if (length > remaining()) {
            throw PayloadException.invalid("String is longer than the payload");
        }
        String value = new String(data, position, length, StandardCharsets.UTF_8);
        position += length;
        if (value.length() > maxChars) {
            throw PayloadException.invalid("String is too long");
        }
        return value;
    }

    public void finish() throws PayloadException {
        if (remaining() != 0) {
            throw PayloadException.invalid("Payload has " + remaining() + " unexpected trailing bytes");
        }
    }
}
