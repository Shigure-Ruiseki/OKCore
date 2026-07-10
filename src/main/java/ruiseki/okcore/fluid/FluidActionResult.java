package ruiseki.okcore.fluid;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

public class FluidActionResult {

    public static final FluidActionResult FAILURE = new FluidActionResult(false, null);

    public final boolean success;
    public final ItemStack result;

    public FluidActionResult(ItemStack result) {
        this(true, result);
    }

    private FluidActionResult(boolean success, ItemStack result) {
        this.success = success;
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    @Nonnull
    public ItemStack getResult() {
        return result;
    }
}
