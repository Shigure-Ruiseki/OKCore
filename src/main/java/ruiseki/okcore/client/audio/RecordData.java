package ruiseki.okcore.client.audio;

import net.minecraft.item.ItemRecord;

import ruiseki.okcore.network.ExtendedBuffer;

public final class RecordData {

    private final String recordName;
    private final int durationTicks;
    private final int sampleRate;
    private final long totalSamples;

    public RecordData(String recordName, int durationTicks, int sampleRate, long totalSamples) {
        this.recordName = recordName;
        this.durationTicks = durationTicks;
        this.sampleRate = sampleRate;
        this.totalSamples = totalSamples;
    }

    public ItemRecord getItemRecord() {
        return ItemRecord.getRecord(this.recordName);
    }

    public String getRecordName() {
        return recordName;
    }

    public int getDurationTicks() {
        return durationTicks;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public long getTotalSamples() {
        return totalSamples;
    }

    public void toNetwork(ExtendedBuffer buffer) {
        buffer.writeString(this.recordName);
        buffer.writeInt(this.durationTicks);
        buffer.writeInt(this.sampleRate);
        buffer.writeLong(this.totalSamples);
    }

    public static RecordData fromNetwork(ExtendedBuffer buffer) {
        String recordName = buffer.readString();
        int durationTicks = buffer.readInt();
        int sampleRate = buffer.readInt();
        long totalSamples = buffer.readLong();
        return new RecordData(recordName, durationTicks, sampleRate, totalSamples);
    }
}
