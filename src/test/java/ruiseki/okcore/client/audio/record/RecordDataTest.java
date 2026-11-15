package ruiseki.okcore.client.audio.record;

import static org.junit.Assert.*;

import org.junit.Test;

import io.netty.buffer.Unpooled;
import ruiseki.okcore.network.ExtendedBuffer;

public class RecordDataTest {

    private void assertData(RecordData data, String name, int ticks, int rate, long samples) {
        assertEquals(name, data.getRecordName());
        assertEquals(ticks, data.getDurationTicks());
        assertEquals(rate, data.getSampleRate());
        assertEquals(samples, data.getTotalSamples());
    }

    @Test
    public void networkRoundTripPreservesUnicodeAndLongSampleCounts() {
        ExtendedBuffer buffer = new ExtendedBuffer(Unpooled.buffer());
        try {
            new RecordData("test:音楽", 2400, 48000, 5_000_000_000L).toNetwork(buffer);
            assertData(RecordData.fromNetwork(buffer), "test:音楽", 2400, 48000, 5_000_000_000L);
            assertEquals(0, buffer.readableBytes());
        } finally {
            buffer.release();
        }
    }

    @Test
    public void wireFormatUsesStringIntIntLongInThatOrder() {
        ExtendedBuffer buffer = new ExtendedBuffer(Unpooled.buffer());
        try {
            new RecordData("", 0, Integer.MAX_VALUE, Long.MAX_VALUE).toNetwork(buffer);
            assertEquals("", buffer.readString());
            assertEquals(0, buffer.readInt());
            assertEquals(Integer.MAX_VALUE, buffer.readInt());
            assertEquals(Long.MAX_VALUE, buffer.readLong());
        } finally {
            buffer.release();
        }
    }

    @Test
    public void decodesConsecutiveRecordsWithoutConsumingTheNext() {
        ExtendedBuffer buffer = new ExtendedBuffer(Unpooled.buffer());
        try {
            new RecordData("first", 1, 2, 3).toNetwork(buffer);
            new RecordData("second", 4, 5, 6).toNetwork(buffer);
            assertData(RecordData.fromNetwork(buffer), "first", 1, 2, 3);
            assertData(RecordData.fromNetwork(buffer), "second", 4, 5, 6);
            assertEquals(0, buffer.readableBytes());
        } finally {
            buffer.release();
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void rejectsTruncatedRecord() {
        ExtendedBuffer buffer = new ExtendedBuffer(Unpooled.buffer());
        try {
            buffer.writeString("incomplete");
            buffer.writeInt(1);
            RecordData.fromNetwork(buffer);
        } finally {
            buffer.release();
        }
    }
}
