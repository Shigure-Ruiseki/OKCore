package ruiseki.okcore.data.loader.baubles;

import java.util.HashMap;
import java.util.Map;

import ruiseki.okcore.helper.BaubleHelpers;

public enum BaubleOperation {

    ADD {

        @Override
        public boolean execute(String slotType, int size) {
            if (!super.execute(slotType, size)) return false;

            for (int i = 0; i < size; i++) {
                BaubleHelpers.assignSlot(slotType);
            }
            return true;
        }
    },

    SET_MINIMUM {

        @Override
        public boolean execute(String slotType, int size) {
            if (!super.execute(slotType, size)) return false;

            BaubleHelpers.assignSlotsUpToMinimum(slotType, size);
            return true;
        }
    },

    SET_MAXIMUM {

        @Override
        public boolean execute(String slotType, int size) {
            if (!super.execute(slotType, size)) return false;

            BaubleHelpers.unassignSlotsDownToMaximum(slotType, size);
            return true;
        }
    }

    ;

    BaubleOperation() {}

    public boolean execute(String slotType, int size) {
        return BaubleHelpers.checkAndRegisterType(slotType);
    }

    private static final Map<String, BaubleOperation> NAME_MAP = new HashMap<>();

    static {
        for (BaubleOperation op : values()) {
            NAME_MAP.put(op.name(), op);
        }
        NAME_MAP.put("ADD_UNTIL_MINIMUM", SET_MINIMUM);
        NAME_MAP.put("REMOVE_DOWN_TO_MAXIMUM", SET_MAXIMUM);
    }

    public static BaubleOperation fromString(String name) {
        if (name == null) return null;
        return NAME_MAP.get(
            name.trim()
                .toUpperCase());
    }
}
