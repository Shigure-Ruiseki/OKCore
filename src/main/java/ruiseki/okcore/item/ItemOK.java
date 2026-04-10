package ruiseki.okcore.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.capabilities.IItemCapability;

public class ItemOK extends Item implements IItem, IItemCapability, IItemSharedTag {

    private final String name;

    public ItemOK(String name) {
        super();
        this.name = name;
        setUnlocalizedName(name);
    }

    @Override
    public void init() {
        GameRegistry.registerItem(this, name);
    }

    @Override
    public Item getItem() {
        return this;
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return null;
    }

    @Override
    public NBTTagCompound getNBTShareTag(ItemStack stack) {
        return stack.getTagCompound();
    }

    @Override
    public void readNBTShareTag(ItemStack stack, @Nullable NBTTagCompound nbt) {
        stack.setTagCompound(nbt);
    }
}
