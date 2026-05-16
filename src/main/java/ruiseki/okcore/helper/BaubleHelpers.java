package ruiseki.okcore.helper;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.apache.logging.log4j.Level;

import baubles.api.expanded.BaubleExpandedSlots;
import ruiseki.okcore.OKCore;

public class BaubleHelpers {

    private static ArrayList<String> cachedRegisteredTypes = null;
    private static ArrayList<String> cachedAssignedSlots = null;

    @SuppressWarnings("unchecked")
    private static ArrayList<String> getRegisteredTypesList() {
        if (cachedRegisteredTypes != null) return cachedRegisteredTypes;
        try {
            Field field = BaubleExpandedSlots.class.getDeclaredField("registeredTypes");
            field.setAccessible(true);
            cachedRegisteredTypes = (ArrayList<String>) field.get(null);
            return cachedRegisteredTypes;
        } catch (Exception e) {
            OKCore.okLog(
                Level.ERROR,
                "[OKCore-Baubles] Critical: Failed to access 'registeredTypes' field via Reflection!");
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static ArrayList<String> getAssignedSlotsList() {
        if (cachedAssignedSlots != null) return cachedAssignedSlots;
        try {
            Field field = BaubleExpandedSlots.class.getDeclaredField("assignedSlots");
            field.setAccessible(true);
            cachedAssignedSlots = (ArrayList<String>) field.get(null);
            return cachedAssignedSlots;
        } catch (Exception e) {
            OKCore.okLog(
                Level.ERROR,
                "[OKCore-Baubles] Critical: Failed to access 'assignedSlots' field via Reflection!");
            e.printStackTrace();
            return null;
        }
    }

    public static boolean checkAndRegisterType(String slotType) {
        if (slotType == null || slotType.isEmpty()) return false;

        if (!BaubleExpandedSlots.isTypeRegistered(slotType)) {
            ArrayList<String> registeredTypes = getRegisteredTypesList();
            if (registeredTypes != null) {
                registeredTypes.add(slotType);
                OKCore.okLog(
                    Level.INFO,
                    "[OKCore-Baubles] Bypass success: Registered custom type '{}' post-PreInit.",
                    slotType);
                return true;
            }
            return false;
        }
        return true;
    }

    public static void assignSlot(String slotType) {
        if (slotType == null || slotType.equals(BaubleExpandedSlots.unknownType)) return;
        if (!checkAndRegisterType(slotType)) return;

        ArrayList<String> assignedSlots = getAssignedSlotsList();
        if (assignedSlots != null) {
            assignedSlots.add(slotType);
        }
    }

    public static void assignSlotsUpToMinimum(String slotType, int size) {
        if (size < 1 || slotType == null || slotType.equals(BaubleExpandedSlots.unknownType)) return;
        if (!checkAndRegisterType(slotType)) return;

        ArrayList<String> assignedSlots = getAssignedSlotsList();
        if (assignedSlots == null) return;

        int currentCount = 0;
        for (String slot : assignedSlots) {
            if (slot.equals(slotType)) {
                currentCount++;
            }
        }

        if (currentCount >= size) return;

        int needed = size - currentCount;
        for (int i = 0; i < needed; i++) {
            assignedSlots.add(slotType);
        }
        OKCore.okLog(Level.INFO, "[OKCore-Baubles] Force-assigned '{}' slots up to minimum: {}.", slotType, size);
    }

    public static void unassignSlotsDownToMaximum(String slotType, int size) {
        if (size < 0) size = 0;
        if (slotType == null || slotType.equals(BaubleExpandedSlots.unknownType)) return;
        if (!BaubleExpandedSlots.isTypeRegistered(slotType)) return;

        ArrayList<String> assignedSlots = getAssignedSlotsList();
        if (assignedSlots == null || assignedSlots.isEmpty()) return;

        int currentCount = 0;
        for (String slot : assignedSlots) {
            if (slot.equals(slotType)) {
                currentCount++;
            }
        }

        if (currentCount <= size) return;

        int toRemove = currentCount - size;
        int removed = 0;

        for (int i = assignedSlots.size() - 1; i >= 0; i--) {
            if (removed >= toRemove) break;

            if (slotType.equals(assignedSlots.get(i))) {
                assignedSlots.remove(i);
                removed++;
            }
        }
        OKCore.okLog(Level.INFO, "[OKCore-Baubles] Force-unassigned '{}' slots down to maximum: {}.", slotType, size);
    }
}
