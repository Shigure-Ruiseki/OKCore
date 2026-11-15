package ruiseki.okcore.audio.record;

import ruiseki.okcore.network.ExtendedBuffer;

public class RecordData {

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

    /**
     * Format records.name
     */
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
        buffer.writeString(recordName);
        buffer.writeInt(durationTicks);
        buffer.writeInt(sampleRate);
        buffer.writeLong(totalSamples);
    }

    public static RecordData fromNetwork(ExtendedBuffer buffer) {
        return new RecordData(buffer.readString(), buffer.readInt(), buffer.readInt(), buffer.readLong());
    }
}
