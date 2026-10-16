package com.yonn2222.clientsortpaper;

import com.yonn2222.clientsortpaper.network.PayloadException;
import com.yonn2222.clientsortpaper.network.PayloadResult;
import com.yonn2222.clientsortpaper.network.Request;
import com.yonn2222.clientsortpaper.network.WireReader;
import com.yonn2222.clientsortpaper.network.WireWriter;
import com.yonn2222.clientsortpaper.operation.Operation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WireTest {
    @Test
    void varIntsRoundTripIncludingBoundariesAndNegatives() throws PayloadException {
        int[] values = {0, 1, 127, 128, 255, 16383, 16384, 2097151, 2097152, Integer.MAX_VALUE, -1, Integer.MIN_VALUE};
        WireWriter out = new WireWriter();
        for (int value : values) {
            out.writeVarInt(value);
        }
        WireReader in = new WireReader(out.toByteArray());
        for (int value : values) {
            assertEquals(value, in.readVarInt());
        }
        in.finish();
    }

    @Test
    void knownVarIntBytesMatchTheMinecraftSpec() {
        assertArrayEquals(new byte[]{(byte) 0xdd, (byte) 0xc7, 0x01}, new WireWriter().writeVarInt(25565).toByteArray());
        assertArrayEquals(new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x0f}, new WireWriter().writeVarInt(-1).toByteArray());
    }

    @Test
    void intsAreBigEndian() throws PayloadException {
        byte[] bytes = new WireWriter().writeInt(0x01020304).toByteArray();
        assertArrayEquals(new byte[]{1, 2, 3, 4}, bytes);
        assertEquals(0x01020304, new WireReader(bytes).readInt());
    }

    @Test
    void stringsRoundTripWithUnicode() throws PayloadException {
        String text = "Sortieren äöü ☃ 😀";
        WireReader in = new WireReader(new WireWriter().writeString(text).toByteArray());
        assertEquals(text, in.readString(100));
        in.finish();
    }

    @Test
    void truncatedAndOversizedInputIsRejectedNotCrashed() {
        assertThrows(PayloadException.class, () -> new WireReader(new byte[]{(byte) 0x80}).readVarInt());
        assertThrows(PayloadException.class, () -> new WireReader(new byte[]{(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, 0x01}).readVarInt());
        assertThrows(PayloadException.class, () -> new WireReader(new byte[]{1, 2, 3}).readInt());

        byte[] lying = new WireWriter().writeVarInt(1_000_000).toByteArray();
        assertThrows(PayloadException.class, () -> new WireReader(lying).readVarIntArray(Integer.MAX_VALUE));

        byte[] negative = new WireWriter().writeVarInt(-5).toByteArray();
        assertThrows(PayloadException.class, () -> new WireReader(negative).readVarIntArray(10));

        byte[] longString = new WireWriter().writeVarInt(500).toByteArray();
        assertThrows(PayloadException.class, () -> new WireReader(longString).readString(1000));
    }

    @Test
    void everyDecodeErrorIsInvalidData() {
        PayloadException e = assertThrows(PayloadException.class, () -> Request.decode(Operation.SORT, new byte[]{}));
        assertEquals(PayloadResult.INVALID_DATA, e.result());
    }

    @Test
    void sortRequestDecodesLikeTheModWritesIt() throws PayloadException {
        byte[] bytes = new WireWriter().writeVarInt(7).writeVarIntArray(new int[]{9, 10, 10, 9}).toByteArray();
        Request.Sort sort = (Request.Sort) Request.decode(Operation.SORT, bytes);
        assertEquals(7, sort.containerId());
        assertArrayEquals(new int[]{9, 10, 10, 9}, sort.slotMapping());
    }

    @Test
    void collectRequestCarriesItsId() throws PayloadException {
        byte[] bytes = new WireWriter().writeVarInt(3).writeVarIntArray(new int[]{1, 2, 3}).writeString("abc-123").toByteArray();
        Request.Collect collect = (Request.Collect) Request.decode(Operation.COLLECT, bytes);
        assertEquals("abc-123", collect.id());
        assertArrayEquals(new int[]{1, 2, 3}, collect.slotIds());
    }

    @Test
    void transferAndStackFillCarryTheReversedFlag() throws PayloadException {
        byte[] bytes = new WireWriter().writeVarInt(0).writeVarIntArray(new int[]{9}).writeVarIntArray(new int[]{36, 37}).writeBoolean(true).toByteArray();
        Request.Transfer transfer = (Request.Transfer) Request.decode(Operation.TRANSFER, bytes);
        assertTrue(transfer.reversed());
        Request.StackFill fill = (Request.StackFill) Request.decode(Operation.STACK_FILL, bytes);
        assertArrayEquals(new int[]{36, 37}, fill.destinationSlots());
    }

    @Test
    void trailingBytesAreRejected() {
        byte[] bytes = new WireWriter().writeVarInt(0).writeVarIntArray(new int[]{1, 1}).writeVarInt(99).toByteArray();
        assertThrows(PayloadException.class, () -> Request.decode(Operation.SORT, bytes));
    }

    @Test
    void resultEncodingMatchesTheModFormat() throws PayloadException {
        byte[] plain = Request.encodeResult(Operation.SORT, PayloadResult.INVALID_DATA, "bad", null);
        WireReader in = new WireReader(plain);
        assertEquals(3, in.readInt());
        assertEquals("bad", in.readString(100));
        in.finish();

        byte[] collect = Request.encodeResult(Operation.COLLECT, PayloadResult.SUCCESS, "", "id-1");
        WireReader c = new WireReader(collect);
        assertEquals(0, c.readInt());
        assertEquals("", c.readString(100));
        assertEquals("id-1", c.readString(100));
        c.finish();
    }

    @Test
    void resultCodesAreTheOnesOfTheMod() {
        assertEquals(0, PayloadResult.SUCCESS.code());
        assertEquals(1, PayloadResult.FAILURE.code());
        assertEquals(2, PayloadResult.UNSUPPORTED_OP.code());
        assertEquals(3, PayloadResult.INVALID_DATA.code());
        assertEquals(4, PayloadResult.INCONSISTENT_STATE.code());
        assertFalse(PayloadResult.FAILURE.isSuccess());
    }

    @Test
    void channelNamesAreTheOnesOfTheMod() {
        assertEquals("clientsort:collect_c2s", Operation.COLLECT.requestChannel());
        assertEquals("clientsort:sort_c2s", Operation.SORT.requestChannel());
        assertEquals("clientsort:stack_fill_c2s", Operation.STACK_FILL.requestChannel());
        assertEquals("clientsort:transfer_c2s", Operation.TRANSFER.requestChannel());
        assertEquals("clientsort:collect_result_s2c", Operation.COLLECT.resultChannel());
        assertEquals("clientsort:sort_result_s2c", Operation.SORT.resultChannel());
        assertEquals("clientsort:stack_fill_result_s2c", Operation.STACK_FILL.resultChannel());
        assertEquals("clientsort:transfer_result_s2c", Operation.TRANSFER.resultChannel());
    }
}
