package com.yonn2222.clientsortpaper;

import com.yonn2222.clientsortpaper.network.PayloadException;
import com.yonn2222.clientsortpaper.network.PayloadResult;
import com.yonn2222.clientsortpaper.operation.Cell;
import com.yonn2222.clientsortpaper.operation.Operations;
import org.bukkit.Material;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OperationsTest {
    private static final Object CHEST = new Object();
    private static final Object PLAYER = new Object();

    @BeforeAll
    static void startServer() {
        MockBukkit.mock();
    }

    @AfterAll
    static void stopServer() {
        MockBukkit.unmock();
    }

    private static int total(FakeCell[] cells, Material type) {
        int sum = 0;
        for (FakeCell cell : cells) {
            if (cell.type() == type) {
                sum += cell.amount();
            }
        }
        return sum;
    }

    @Test
    void collectMergesPartialStacksTowardsTheFront() {
        FakeCell[] cells = FakeCell.row(CHEST, 5);
        cells[0].with(Material.STONE, 40);
        cells[2].with(Material.STONE, 30);
        cells[4].with(Material.STONE, 10);
        Operations.collect(cells);
        assertEquals(64, cells[0].amount());
        assertEquals(16, cells[2].amount());
        assertEquals(0, cells[4].amount());
        assertEquals(80, total(cells, Material.STONE));
    }

    @Test
    void collectLeavesDifferentItemsAlone() {
        FakeCell[] cells = FakeCell.row(CHEST, 3);
        cells[0].with(Material.STONE, 10);
        cells[1].with(Material.DIRT, 10);
        cells[2].with(Material.STONE, 10);
        Operations.collect(cells);
        assertEquals(20, cells[0].amount());
        assertEquals(10, cells[1].amount());
        assertEquals(0, cells[2].amount());
    }

    @Test
    void collectDoesNotStackUnstackableItems() {
        FakeCell[] cells = FakeCell.row(CHEST, 2);
        cells[0].with(Material.DIAMOND_SWORD, 1);
        cells[1].with(Material.DIAMOND_SWORD, 1);
        Operations.collect(cells);
        assertEquals(1, cells[0].amount());
        assertEquals(1, cells[1].amount());
    }

    @Test
    void sortMovesEveryItemToItsTarget() {
        FakeCell[] cells = FakeCell.row(CHEST, 4);
        cells[0].with(Material.DIRT, 5);
        cells[1].with(Material.STONE, 7);
        cells[3].with(Material.COBBLESTONE, 9);

        Cell[] from = {cells[3], cells[1], cells[0], cells[2]};
        Cell[] to = {cells[2], cells[0], cells[1], cells[3]};
        Operations.sort(from, to);
        assertEquals(Material.STONE, cells[0].type());
        assertEquals(Material.DIRT, cells[1].type());
        assertEquals(Material.COBBLESTONE, cells[2].type());
        assertEquals(Material.AIR, cells[3].type());
        assertEquals(7, cells[0].amount());
    }

    @Test
    void sortHandlesAFullRotationWithoutLosingAnything() {
        FakeCell[] cells = FakeCell.row(CHEST, 3);
        cells[0].with(Material.STONE, 1);
        cells[1].with(Material.DIRT, 2);
        cells[2].with(Material.SAND, 3);
        Operations.sort(new Cell[]{cells[0], cells[1], cells[2]}, new Cell[]{cells[1], cells[2], cells[0]});
        assertEquals(Material.SAND, cells[0].type());
        assertEquals(Material.STONE, cells[1].type());
        assertEquals(Material.DIRT, cells[2].type());
    }

    @Test
    void stackFillOnlyToppsUpAndNeverUsesEmptySlots() {
        FakeCell[] source = FakeCell.row(PLAYER, 3);
        FakeCell[] target = FakeCell.row(CHEST, 3);
        source[0].with(Material.STONE, 50);
        source[1].with(Material.DIRT, 10);
        target[0].with(Material.STONE, 20);
        target[2].with(Material.GRAVEL, 5);
        Operations.stackFill(source, target, false);
        assertEquals(64, target[0].amount());
        assertEquals(6, source[0].amount());
        assertEquals(10, source[1].amount(), "dirt has no partial stack to fill");
        assertEquals(Material.AIR, target[1].type(), "empty slots stay empty");
    }

    @Test
    void transferFillsPartialStacksThenEmptySlots() {
        FakeCell[] source = FakeCell.row(PLAYER, 2);
        FakeCell[] target = FakeCell.row(CHEST, 3);
        source[0].with(Material.STONE, 50);
        source[1].with(Material.DIRT, 10);
        target[0].with(Material.STONE, 30);
        Operations.transfer(source, target, false);
        assertEquals(64, target[0].amount());
        assertEquals(16, target[1].amount(), "the rest of the stone goes into the first empty slot");
        assertEquals(Material.STONE, target[1].type());
        assertEquals(Material.DIRT, target[2].type());
        assertEquals(0, source[0].amount());
        assertEquals(0, source[1].amount());
    }

    @Test
    void transferKeepsWhatDoesNotFit() {
        FakeCell[] source = FakeCell.row(PLAYER, 3);
        FakeCell[] target = FakeCell.row(CHEST, 1);
        source[0].with(Material.STONE, 10);
        source[1].with(Material.DIRT, 10);
        source[2].with(Material.SAND, 10);
        Operations.transfer(source, target, false);
        assertEquals(Material.STONE, target[0].type());
        assertEquals(10, source[1].amount());
        assertEquals(10, source[2].amount());
    }

    @Test
    void reversedTransferStartsAtTheLastSourceSlot() {
        FakeCell[] source = FakeCell.row(PLAYER, 2);
        FakeCell[] target = FakeCell.row(CHEST, 1);
        source[0].with(Material.STONE, 10);
        source[1].with(Material.DIRT, 10);
        Operations.transfer(source, target, true);
        assertEquals(Material.DIRT, target[0].type());
        assertEquals(10, source[0].amount());
    }

    @Test
    void shulkerBoxesAreNotMovedIntoShulkerBoxes() {
        FakeCell[] source = FakeCell.row(PLAYER, 1);
        FakeCell[] target = {new FakeCell(CHEST, 0, true), new FakeCell(CHEST, 1, true)};
        source[0].with(Material.SHULKER_BOX, 1);
        Operations.transfer(source, target, false);
        assertEquals(1, source[0].amount());
        assertEquals(Material.AIR, target[0].type());
    }

    @Test
    void anOperationThatLosesItemsIsUndone() {
        FakeCell[] cells = FakeCell.row(CHEST, 2);
        cells[0].with(Material.STONE, 10);
        cells[1].with(Material.DIRT, 10);
        PayloadException failure = assertThrows(PayloadException.class,
                () -> Operations.guarded(List.of(cells), () -> cells[0].set(null)));
        assertEquals(PayloadResult.INCONSISTENT_STATE, failure.result());
        assertEquals(10, cells[0].amount(), "the lost stack is back");
    }

    @Test
    void anOperationThatDuplicatesItemsIsUndone() {
        FakeCell[] cells = FakeCell.row(CHEST, 2);
        cells[0].with(Material.STONE, 10);
        assertThrows(PayloadException.class,
                () -> Operations.guarded(List.of(cells), () -> cells[1].set(cells[0].get())));
        assertEquals(0, cells[1].amount());
    }

    @Test
    void aCrashInTheMiddleRestoresEverything() {
        FakeCell[] cells = FakeCell.row(CHEST, 2);
        cells[0].with(Material.STONE, 10);
        assertThrows(IllegalStateException.class, () -> Operations.guarded(List.of(cells), () -> {
            cells[0].set(null);
            throw new IllegalStateException("boom");
        }));
        assertEquals(10, cells[0].amount());
    }

    @Test
    void aCorrectOperationPassesTheCheck() throws PayloadException {
        FakeCell[] cells = FakeCell.row(CHEST, 3);
        cells[0].with(Material.STONE, 40);
        cells[2].with(Material.STONE, 40);
        List<Cell> involved = new ArrayList<>(List.of(cells));
        involved.addAll(List.of(cells));
        Operations.guarded(involved, () -> Operations.collect(cells));
        assertEquals(64, cells[0].amount());
        assertEquals(16, cells[2].amount());
    }
}
