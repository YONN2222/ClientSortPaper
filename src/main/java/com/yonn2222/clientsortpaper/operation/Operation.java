package com.yonn2222.clientsortpaper.operation;

public enum Operation {
    COLLECT("collect", "sort", "clientsort:collect_c2s", "clientsort:collect_result_s2c"),
    SORT("sort", "sort", "clientsort:sort_c2s", "clientsort:sort_result_s2c"),
    STACK_FILL("stack-fill", "stack-fill", "clientsort:stack_fill_c2s", "clientsort:stack_fill_result_s2c"),
    TRANSFER("transfer", "transfer", "clientsort:transfer_c2s", "clientsort:transfer_result_s2c");

    private final String label;
    private final String settingKey;
    private final String requestChannel;
    private final String resultChannel;

    Operation(String label, String settingKey, String requestChannel, String resultChannel) {
        this.label = label;
        this.settingKey = settingKey;
        this.requestChannel = requestChannel;
        this.resultChannel = resultChannel;
    }

    public String label() {
        return label;
    }

    public String settingKey() {
        return settingKey;
    }

    public String requestChannel() {
        return requestChannel;
    }

    public String resultChannel() {
        return resultChannel;
    }

    public static Operation byRequestChannel(String channel) {
        for (Operation operation : values()) {
            if (operation.requestChannel.equals(channel)) {
                return operation;
            }
        }
        return null;
    }
}
