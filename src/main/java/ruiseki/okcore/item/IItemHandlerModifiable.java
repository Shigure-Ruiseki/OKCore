package ruiseki.okcore.item;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.common.Optional;

@Optional.Interface(
    iface = "com.cleanroommc.modularui.utils.item.IItemHandlerModifiable",
    modid = "modularui2",
    striprefs = true)
public interface IItemHandlerModifiable
    extends IItemHandler, com.cleanroommc.modularui.utils.item.IItemHandlerModifiable {

    /**
     * Overrides the stack in the given slot. This method is used by the
     * standard Forge helper methods and classes. It is not intended for
     * general use by other mods, and the handler may throw an error if it
     * is called unexpectedly.
     *
     * @param slot  Slot to modify
     * @param stack ItemStack to set slot to (may be empty).
     * @throws RuntimeException if the handler is called in a way that the handler
     *                          was not expecting.
     **/
    void setStackInSlot(int slot, @Nullable ItemStack stack);
}
