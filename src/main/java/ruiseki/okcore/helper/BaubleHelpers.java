package ruiseki.okcore.helper;

import baubles.api.BaublesApi;
import baubles.api.expanded.BaubleExpandedSlots;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import ruiseki.okcore.lib.LibMods;

public class BaubleHelpers {

    public static boolean checkAndRegisterType(String slotType) {
        if (slotType == null || slotType.isEmpty()) return false;
        if (!BaubleExpandedSlots.isTypeRegistered(slotType)) {
            return BaubleExpandedSlots.tryRegisterType(slotType);
        }
        return true;
    }

    public static void assignSlot(String slotType) {
        BaubleExpandedSlots.tryAssignSlotOfType(slotType);
    }

    public static void assignSlotsUpToMinimum(String slotType, int size) {
        BaubleExpandedSlots.tryAssignSlotsUpToMinimum(slotType, size);
    }

    public static void unassignSlotsDownToMaximum(String slotType, int size) {
        BaubleExpandedSlots.tryUnassignSlotsDownToMaximum(slotType, size);
    }

    public static IInventory getBaubleInventory(EntityPlayer player) {
        if (player == null) return null;
        if (!LibMods.Baubles.isModLoaded()) return null;
        return BaublesApi.getBaubles(player);
    }
}
