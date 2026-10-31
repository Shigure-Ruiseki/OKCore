
package ruiseki.okcore.capabilities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.jetbrains.annotations.Nullable;

/**
 * Defines a capability initialization hook for an {@link ItemStack}.
 *
 * <p>
 * Items implementing this interface can provide custom capabilities
 * when an item stack is initialized.
 * </p>
 *
 * <p>
 * Implementations may override
 * {@link #initCapabilities(ItemStack, NBTTagCompound)}
 * to create and return an {@link ICapabilityProvider} containing
 * the capabilities associated with the given item stack.
 * </p>
 *
 * <p>
 * The returned provider may be used by the capability system
 * to expose additional functionality or state associated with
 * the item stack.
 * </p>
 *
 * <p>
 * By default, no capability provider is created, and
 * {@code null} is returned.
 * </p>
 *
 * @see ICapabilityProvider
 * @see ItemStack
 */
public interface IItemCapability {

    /**
     * Initializes capabilities for the specified item stack.
     *
     * <p>
     * This method allows an item to provide a custom
     * {@link ICapabilityProvider} during stack initialization.
     * </p>
     *
     * <p>
     * The optional NBT compound may contain initialization data
     * used to restore or configure the capability provider.
     * Implementations should handle a {@code null} NBT value.
     * </p>
     *
     * <p>
     * Returning {@code null} indicates that the item does not
     * provide a custom capability provider.
     * </p>
     *
     * @param stack the item stack whose capabilities are being initialized
     * @param nbt   optional NBT data used for capability initialization,
     *              or {@code null} if no data is available
     * @return the initialized capability provider, or {@code null}
     *         if no custom capabilities are provided
     *
     * @see ICapabilityProvider
     */
    @Nullable
    default ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return null;
    }
}
