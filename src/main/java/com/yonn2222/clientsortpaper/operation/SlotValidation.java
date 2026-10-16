package com.yonn2222.clientsortpaper.operation;

import com.yonn2222.clientsortpaper.network.PayloadException;

import java.util.HashSet;
import java.util.Set;

public final class SlotValidation {
    private SlotValidation() {
    }

    public static Cell[] array(SlotSource source, int[] slotIds) throws PayloadException {
        if (slotIds.length < 1) {
            throw PayloadException.invalid("Slot array contains too few slots");
        }
        Cell[] cells = new Cell[slotIds.length];
        Set<Integer> seen = new HashSet<>();
        Object container = null;
        for (int i = 0; i < slotIds.length; i++) {
            Cell cell = source.cell(slotIds[i]);
            if (container == null) {
                container = cell.container();
            } else if (container != cell.container()) {
                throw PayloadException.invalid("Slot array contains slots from different containers");
            }
            if (!seen.add(slotIds[i])) {
                throw PayloadException.invalid("Slot array contains duplicate slot " + slotIds[i]);
            }
            cells[i] = cell;
        }
        return cells;
    }

    public static Cell[][] mapping(SlotSource source, int[] mapping) throws PayloadException {
        if (mapping.length < 2) {
            throw PayloadException.invalid("Slot mapping contains too few slots");
        }
        if (mapping.length % 2 != 0) {
            throw PayloadException.invalid("Slot mapping contains an uneven number of slots");
        }
        int pairs = mapping.length / 2;
        Cell[] from = new Cell[pairs];
        Cell[] to = new Cell[pairs];
        Set<Integer> sources = new HashSet<>();
        Set<Integer> destinations = new HashSet<>();
        Object container = null;
        for (int i = 0; i < pairs; i++) {
            int fromId = mapping[i * 2];
            int toId = mapping[i * 2 + 1];
            from[i] = source.cell(fromId);
            to[i] = source.cell(toId);
            if (container == null) {
                container = from[i].container();
            }
            if (container != from[i].container() || container != to[i].container()) {
                throw PayloadException.invalid("Slot mapping contains slots from different containers");
            }
            if (!sources.add(fromId)) {
                throw PayloadException.invalid("Slot mapping contains duplicate source slot " + fromId);
            }
            if (!destinations.add(toId)) {
                throw PayloadException.invalid("Slot mapping contains duplicate destination slot " + toId);
            }
        }
        if (!sources.equals(destinations)) {
            throw PayloadException.invalid("Slot mapping destinations are not the same slots as the sources");
        }
        return new Cell[][]{from, to};
    }
}
