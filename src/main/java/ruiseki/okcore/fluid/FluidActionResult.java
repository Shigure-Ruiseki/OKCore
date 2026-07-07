package ruiseki.okcore.fluid;

import net.minecraft.item.ItemStack;

public class FluidActionResult {

    private final boolean success;
    private final ItemStack result;

    public FluidActionResult(boolean success, ItemStack result) {
        this.success = success;
        this.result = result;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public ItemStack getResult() {
        return this.result;
    }
}
