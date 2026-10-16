package com.yonn2222.clientsortpaper.network;

public final class PayloadException extends Exception {
    private static final long serialVersionUID = 1L;

    private final PayloadResult result;

    public PayloadException(PayloadResult result, String message) {
        super(message);
        this.result = result;
    }

    public PayloadResult result() {
        return result;
    }

    public static PayloadException unsupported(String message) {
        return new PayloadException(PayloadResult.UNSUPPORTED_OP, message);
    }

    public static PayloadException invalid(String message) {
        return new PayloadException(PayloadResult.INVALID_DATA, message);
    }

    public static PayloadException inconsistent(String message) {
        return new PayloadException(PayloadResult.INCONSISTENT_STATE, message);
    }
}
