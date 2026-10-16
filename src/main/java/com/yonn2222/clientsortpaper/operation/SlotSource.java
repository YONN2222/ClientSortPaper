package com.yonn2222.clientsortpaper.operation;

import com.yonn2222.clientsortpaper.network.PayloadException;

public interface SlotSource {
    Cell cell(int slotId) throws PayloadException;
}
