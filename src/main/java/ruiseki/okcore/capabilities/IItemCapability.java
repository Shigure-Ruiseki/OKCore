package ruiseki.okcore.capabilities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.jetbrains.annotations.Nullable;

public interface IItemCapability {

    @Nullable
    ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt);
}
