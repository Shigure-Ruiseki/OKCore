package ruiseki.okcore.client.audio.record;

import net.minecraft.item.ItemRecord;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.network.ExtendedBuffer;

public final class RecordData {

    private final String recordName;

    @SideOnly(Side.CLIENT)
    private ItemRecord itemRecord;

    @SideOnly(Side.CLIENT)
    private ResourceLocation recordResource;

    private final int durationTicks;
    private final int sampleRate;
    private final long totalSamples;

    public RecordData(String recordName, int durationTicks, int sampleRate, long totalSamples) {
        this.recordName = recordName;
        this.durationTicks = durationTicks;
        this.sampleRate = sampleRate;
        this.totalSamples = totalSamples;
    }

    @SideOnly(Side.CLIENT)
    public ItemRecord getItemRecord() {
        if (this.itemRecord == null) {
            this.itemRecord = ItemRecord.getRecord(this.recordName);
        }
        return this.itemRecord;
    }

    @SideOnly(Side.CLIENT)
    public ResourceLocation getRecordResource() {
        if (this.recordResource == null) {
            this.recordResource = this.getItemRecord()
                .getRecordResource(this.recordName);
        }
        return this.recordResource;
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
