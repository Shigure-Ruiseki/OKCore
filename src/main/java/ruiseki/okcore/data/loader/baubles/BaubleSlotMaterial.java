package ruiseki.okcore.data.loader.baubles;

import static ruiseki.okcore.data.loader.conditional.LoadConditionHandler.CONDITION_KEY;

import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ruiseki.okcore.json.AbstractJsonMaterial;

public class BaubleSlotMaterial extends AbstractJsonMaterial {

    protected ResourceLocation id;
    protected JsonArray conditionsArray;
    private String slotType;
    private int size;
    private BaubleOperation operation;

    public BaubleSlotMaterial(ResourceLocation id, String slotType) {
        this.id = id;
        this.slotType = slotType;
    }

    @Override
    public void read(JsonObject json) {
        if (json.has(CONDITION_KEY) && json.get(CONDITION_KEY)
            .isJsonArray()) {
            this.conditionsArray = json.getAsJsonArray(CONDITION_KEY);
        } else {
            this.conditionsArray = new JsonArray();
        }

        this.size = getInt(json, "size", 1);

        String opStr = getString(json, "operation", "ADD");
        this.operation = BaubleOperation.fromString(opStr);

        captureUnknownProperties(json, "size", "operation");
    }

    @Override
    public boolean validate() {
        if (slotType == null || slotType.trim()
            .isEmpty()) {
            logValidationError(
                String
                    .format("Bauble Slot %s validation failed: Slot type identification string is null or empty!", id));
            return false;
        }

        if (size < 0) {
            logValidationError(
                String.format(
                    "Bauble Slot %s validation failed: Invalid slot size '%d' assigned for type '%s'. Size capacity cannot be negative!",
                    id,
                    size,
                    slotType));
            return false;
        }

        if (operation == null) {
            logValidationError(
                String.format(
                    "Bauble Slot %s validation failed: Unsupported or unrecognizable operation action specified in JSON!",
                    id));
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
