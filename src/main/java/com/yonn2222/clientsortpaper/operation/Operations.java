package com.yonn2222.clientsortpaper.operation;

import com.yonn2222.clientsortpaper.network.PayloadException;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Operations {
    private Operations() {
    }

    public static void sort(Cell[] from, Cell[] to) {
        ItemStack[] snapshot = new ItemStack[from.length];
        for (int i = 0; i < from.length; i++) {
            snapshot[i] = from[i].get();
        }
        for (int i = 0; i < to.length; i++) {
            to[i].set(snapshot[i]);
        }
    }

    public static void collect(Cell[] cells) {
        for (int i = cells.length - 1; i >= 0; i--) {
            ItemStack source = cells[i].get();
            if (source == null || source.getAmount() >= cells[i].limit(source)) {
                continue;
            }
            for (int j = 0; j < i && cells[i].get() != null; j++) {
                merge(cells[i], cells[j]);
            }
        }
    }

    public static void stackFill(Cell[] source, Cell[] destination, boolean reversed) {
        move(source, destination, reversed, false);
    }

    public static void transfer(Cell[] source, Cell[] destination, boolean reversed) {
        move(source, destination, reversed, true);
    }

    private static void move(Cell[] source, Cell[] destination, boolean reversed, boolean useEmptySlots) {
        int count = source.length;
        for (int n = 0; n < count; n++) {
            Cell from = source[reversed ? count - 1 - n : n];
            if (from.get() == null) {
                continue;
            }
            for (Cell to : destination) {
                if (from.get() == null) {
                    break;
                }
                if (!to.sameSlot(from)) {
                    merge(from, to);
                }
            }
            if (!useEmptySlots || from.get() == null) {
                continue;
            }
            for (Cell to : destination) {
                if (!to.sameSlot(from) && place(from, to)) {
                    break;
                }
            }
        }
    }

    static int merge(Cell source, Cell destination) {
        ItemStack from = source.get();
        ItemStack to = destination.get();
        if (from == null || to == null || !Items.same(from, to) || !destination.mayPlace(from)) {
            return 0;
        }
        int space = destination.limit(to) - to.getAmount();
        if (space <= 0) {
            return 0;
        }
        int moved = Math.min(space, from.getAmount());
        to.setAmount(to.getAmount() + moved);
        destination.set(to);
        from.setAmount(from.getAmount() - moved);
        source.set(from.getAmount() > 0 ? from : null);
        return moved;
    }

    static boolean place(Cell source, Cell destination) {
        ItemStack from = source.get();
        if (from == null || destination.get() != null || !destination.mayPlace(from)) {
            return false;
        }
        int moved = Math.min(destination.limit(from), from.getAmount());
        if (moved <= 0) {
            return false;
        }
        destination.set(Items.withAmount(from, moved));
        from.setAmount(from.getAmount() - moved);
        source.set(from.getAmount() > 0 ? from : null);
        return true;
    }

    public static void guarded(Iterable<Cell> involved, Runnable change) throws PayloadException {
        Map<Slot, Cell> cells = new LinkedHashMap<>();
        for (Cell cell : involved) {
            cells.putIfAbsent(new Slot(cell), cell);
        }
        List<Cell> list = new ArrayList<>(cells.values());
        ItemStack[] backup = new ItemStack[list.size()];
        for (int i = 0; i < backup.length; i++) {
            backup[i] = list.get(i).get();
        }
        Ledger before = Ledger.of(backup);
        try {
            change.run();
        } catch (RuntimeException failure) {
            restore(list, backup);
            throw failure;
        }
        ItemStack[] after = new ItemStack[list.size()];
        for (int i = 0; i < after.length; i++) {
            after[i] = list.get(i).get();
        }
        if (!before.equals(Ledger.of(after))) {
            restore(list, backup);
            throw PayloadException.inconsistent("The items changed during the operation, everything was put back");
        }
    }

    private static void restore(List<Cell> cells, ItemStack[] backup) {
        for (int i = 0; i < backup.length; i++) {
            cells.get(i).set(backup[i]);
        }
    }

    private static final class Slot {
        private final Object container;
        private final int index;

        Slot(Cell cell) {
            this.container = cell.container();
            this.index = cell.index();
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Slot slot && slot.container == container && slot.index == index;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(container) * 31 + index;
        }
    }

    private static final class Ledger {
        private final List<ItemStack> kinds = new ArrayList<>();
        private final List<Long> totals = new ArrayList<>();

        static Ledger of(ItemStack[] stacks) {
            Ledger ledger = new Ledger();
            for (ItemStack stack : stacks) {
                if (stack != null) {
                    ledger.add(stack);
                }
            }
            return ledger;
        }

        private void add(ItemStack stack) {
            for (int i = 0; i < kinds.size(); i++) {
                if (Items.same(kinds.get(i), stack)) {
                    totals.set(i, totals.get(i) + stack.getAmount());
                    return;
                }
            }
            kinds.add(stack);
            totals.add((long) stack.getAmount());
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Ledger ledger) || ledger.kinds.size() != kinds.size()) {
                return false;
            }
            for (int i = 0; i < kinds.size(); i++) {
                boolean found = false;
                for (int j = 0; j < ledger.kinds.size(); j++) {
                    if (Items.same(kinds.get(i), ledger.kinds.get(j))) {
                        found = totals.get(i).equals(ledger.totals.get(j));
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            return kinds.size();
        }
    }
}
