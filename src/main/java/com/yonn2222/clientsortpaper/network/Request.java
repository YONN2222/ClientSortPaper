package com.yonn2222.clientsortpaper.network;

import com.yonn2222.clientsortpaper.operation.Operation;

public sealed interface Request {
    int MAX_SLOTS = 1024;

    Operation operation();

    int containerId();

    record Collect(int containerId, int[] slotIds, String id) implements Request {
        @Override
        public Operation operation() {
            return Operation.COLLECT;
        }
    }

    record Sort(int containerId, int[] slotMapping) implements Request {
        @Override
        public Operation operation() {
            return Operation.SORT;
        }
    }

    record StackFill(int containerId, int[] sourceSlots, int[] destinationSlots, boolean reversed) implements Request {
        @Override
        public Operation operation() {
            return Operation.STACK_FILL;
        }
    }

    record Transfer(int containerId, int[] sourceSlots, int[] destinationSlots, boolean reversed) implements Request {
        @Override
        public Operation operation() {
            return Operation.TRANSFER;
        }
    }

    static Request decode(Operation operation, byte[] data) throws PayloadException {
        WireReader in = new WireReader(data);
        Request request = switch (operation) {
            case COLLECT -> new Collect(in.readVarInt(), in.readVarIntArray(MAX_SLOTS), in.readString(64));
            case SORT -> new Sort(in.readVarInt(), in.readVarIntArray(MAX_SLOTS * 2));
            case STACK_FILL -> new StackFill(in.readVarInt(), in.readVarIntArray(MAX_SLOTS), in.readVarIntArray(MAX_SLOTS), in.readBoolean());
            case TRANSFER -> new Transfer(in.readVarInt(), in.readVarIntArray(MAX_SLOTS), in.readVarIntArray(MAX_SLOTS), in.readBoolean());
        };
        in.finish();
        return request;
    }

    static byte[] encodeResult(Operation operation, PayloadResult result, String message, String requestId) {
        String text = message.length() > 512 ? message.substring(0, 512) : message;
        WireWriter out = new WireWriter().writeInt(result.code()).writeString(text);
        if (operation == Operation.COLLECT) {
            out.writeString(requestId == null ? "" : requestId);
        }
        return out.toByteArray();
    }
}
