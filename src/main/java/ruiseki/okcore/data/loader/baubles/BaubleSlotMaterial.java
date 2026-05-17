package ruiseki.okcore.data.loader.baubles;

import com.google.gson.JsonObject;

import ruiseki.okcore.json.AbstractJsonMaterial;

public class BaubleSlotMaterial extends AbstractJsonMaterial {

    private String slotType;
    private int size;
    private BaubleOperation operation;

    public BaubleSlotMaterial(String slotType) {
        this.slotType = slotType;
    }

    @Override
    public void read(JsonObject json) {
        this.size = getInt(json, "size", 1);

        String opStr = getString(json, "operation", "ADD");
        this.operation = BaubleOperation.fromString(opStr);

        captureUnknownProperties(json, "size", "operation");
    }

    @Override
    public boolean validate() {
        if (slotType == null || slotType.trim()
            .isEmpty()) {
            logValidationError("Slot type identification cannot be null or empty!");
            return false;
        }

        if (size < 0) {
            logValidationError(
                String.format("Invalid slot size '%d' for type '%s'. Size cannot be negative!", size, slotType));
            return false;
        }

        if (operation == null) {
            logValidationError("Unsupported or invalid operation type specified in config!");
            return false;
        }

        return true;
    }

    @Override
    public void write(JsonObject json) {

    }

    public void execute() {
        operation.execute(slotType, size);
    }

    public String getSlotType() {
        return slotType;
    }

    public int getSize() {
        return size;
    }

    public BaubleOperation getOperation() {
        return operation;
    }
}
