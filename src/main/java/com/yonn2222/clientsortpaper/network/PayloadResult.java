package com.yonn2222.clientsortpaper.network;

public enum PayloadResult {
    SUCCESS(0),
    FAILURE(1),
    UNSUPPORTED_OP(2),
    INVALID_DATA(3),
    INCONSISTENT_STATE(4);

    private final int code;

    PayloadResult(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    public boolean isSuccess() {
        return this == SUCCESS;
    }
}
