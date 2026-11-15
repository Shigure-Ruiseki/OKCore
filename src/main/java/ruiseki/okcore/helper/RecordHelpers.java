package ruiseki.okcore.helper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.audio.record.ClientRecordDataHandler;
import ruiseki.okcore.audio.record.RecordData;
import ruiseki.okcore.audio.record.RecordDataLoader;
import ruiseki.okcore.audio.record.ServerRecordDataHandler;

public class RecordHelpers {

    public static RecordData getData(EntityPlayer player, ItemRecord record) {
        if (record == null || record.recordName == null) return null;
        return player.worldObj.isRemote ? getData(record)
            : ServerRecordDataHandler.getData(player.getUniqueID(), record);
    }

    public static RecordData getData(EntityPlayer player, ItemStack stack) {
        if (ItemHelpers.isEmpty(stack)) return null;
        if (!(ItemHelpers.getItem(stack) instanceof ItemRecord record)) return null;
        return getData(player, record);
    }

    public static RecordData getData(ItemRecord record) {
        if (record == null || record.recordName == null) return null;
        RecordData data = ClientRecordDataHandler.getData(record);
        if (data != null) return data;
        RecordDataLoader.resolveAndSyncDuration(record);
        return ClientRecordDataHandler.getData(record);
    }

    public static int getDurationTicks(EntityPlayer player, ItemRecord record) {
        RecordData data = getData(player, record);
        return data != null ? data.getDurationTicks() : 0;
    }

    public static int getDurationTicks(EntityPlayer player, ItemStack stack) {
        if (ItemHelpers.isEmpty(stack)) return 0;
        if (!(ItemHelpers.getItem(stack) instanceof ItemRecord record)) return 0;
        return getDurationTicks(player, record);
    }

    @SideOnly(Side.CLIENT)
    public static ItemRecord getRecord(RecordData data) {
        if (data == null || data.getRecordName() == null) return null;
        return ItemRecord.getRecord(data.getRecordName());
    }

    @SideOnly(Side.CLIENT)
    public static ResourceLocation getRecordLocation(RecordData data) {
        ItemRecord record = getRecord(data);
        return record != null ? record.getRecordResource(data.getRecordName()) : null;
    }
}
