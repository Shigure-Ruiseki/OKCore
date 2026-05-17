package ruiseki.okcore.helper;

import baubles.api.expanded.BaubleExpandedSlots;

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
}
