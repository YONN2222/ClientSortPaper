package com.yonn2222.clientsortpaper;

import com.yonn2222.clientsortpaper.network.PayloadException;
import com.yonn2222.clientsortpaper.network.PayloadResult;
import com.yonn2222.clientsortpaper.operation.Cell;
import com.yonn2222.clientsortpaper.operation.RateLimiter;
import com.yonn2222.clientsortpaper.operation.SlotSource;
import com.yonn2222.clientsortpaper.operation.SlotValidation;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SlotValidationTest {
    private final Object top = new Object();
    private final Object bottom = new Object();

    private final SlotSource source = id -> {
        if (id == 60) {
            throw PayloadException.unsupported("protected");
        }
        if (id < 0 || id > 53) {
            throw PayloadException.invalid("outside");
        }
        return new FakeCell(id < 27 ? top : bottom, id);
    };

    private PayloadResult resultOf(org.junit.jupiter.api.function.Executable action) {
        return assertThrows(PayloadException.class, action).result();
    }

    @Test
    void aValidArrayPasses() throws PayloadException {
        Cell[] cells = SlotValidation.array(source, new int[]{27, 28, 29});
        assertEquals(3, cells.length);
    }

    @Test
    void arraysRejectEmptyDuplicatesMixedContainersAndBadSlots() {
        assertEquals(PayloadResult.INVALID_DATA, resultOf(() -> SlotValidation.array(source, new int[]{})));
        assertEquals(PayloadResult.INVALID_DATA, resultOf(() -> SlotValidation.array(source, new int[]{1, 2, 1})));
        assertEquals(PayloadResult.INVALID_DATA, resultOf(() -> SlotValidation.array(source, new int[]{1, 30})));
        assertEquals(PayloadResult.INVALID_DATA, resultOf(() -> SlotValidation.array(source, new int[]{1, 99})));
        assertEquals(PayloadResult.INVALID_DATA, resultOf(() -> SlotValidation.array(source, new int[]{-1})));
        assertEquals(PayloadResult.UNSUPPORTED_OP, resultOf(() -> SlotValidation.array(source, new int[]{1, 60})));
    }

    @Test
    void aValidMappingIsAPermutation() throws PayloadException {
        Cell[][] pairs = SlotValidation.mapping(source, new int[]{27, 28, 28, 29, 29, 27});
        assertEquals(3, pairs[0].length);
        assertEquals(28, pairs[1][0].index());
    }

    @Test
    void identityAndSelfMappingsAreFine() throws PayloadException {
        SlotValidation.mapping(source, new int[]{27, 27, 28, 28});
    }

    @Test
    void mappingsRejectEverythingThatIsNotAReordering() {
        assertEquals(PayloadResult.INVALID_DATA, resultOf(() -> SlotValidation.mapping(source, new int[]{27, 28, 28})));

        assertEquals(PayloadResult.INVALID_DATA, resultOf(() -> SlotValidation.mapping(source, new int[]{})));

        assertEquals(PayloadResult.INVALID_DATA, resultOf(() -> SlotValidation.mapping(source, new int[]{27, 28, 27, 29})));

        assertEquals(PayloadResult.INVALID_DATA, resultOf(() -> SlotValidation.mapping(source, new int[]{27, 29, 28, 29})));

        assertEquals(PayloadResult.INVALID_DATA, resultOf(() -> SlotValidation.mapping(source, new int[]{27, 28, 28, 29})));

        assertEquals(PayloadResult.INVALID_DATA, resultOf(() -> SlotValidation.mapping(source, new int[]{1, 30, 30, 1})));

        assertEquals(PayloadResult.UNSUPPORTED_OP, resultOf(() -> SlotValidation.mapping(source, new int[]{27, 60, 60, 27})));
        assertEquals(PayloadResult.INVALID_DATA, resultOf(() -> SlotValidation.mapping(source, new int[]{27, 99, 99, 27})));
    }

    @Test
    void rateLimiterAllowsTheLimitPerSecondAndThenRecovers() {
        AtomicLong clock = new AtomicLong();
        RateLimiter limiter = new RateLimiter(clock::get);
        UUID player = UUID.randomUUID();
        for (int i = 0; i < 3; i++) {
            assertTrue(limiter.allow(player, 3));
        }
        assertFalse(limiter.allow(player, 3));
        assertTrue(limiter.allow(UUID.randomUUID(), 3), "other players are not affected");
        clock.addAndGet(1_000_000_001L);
        assertTrue(limiter.allow(player, 3));
        limiter.forget(player);
    }

    @Test
    void zeroMeansUnlimited() {
        RateLimiter limiter = new RateLimiter(() -> 0L);
        UUID player = UUID.randomUUID();
        for (int i = 0; i < 1000; i++) {
            assertTrue(limiter.allow(player, 0));
        }
    }
}
