package ruiseki.commoncapabilities.modcompat.vanilla.capability;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.helper.CapabilityHelpers;

/**
 * An abstract capability delegator from entity item to inner itemstack using LazyOptional.
 * 
 * @param <C> The capability type.
 * @author rubensworks
 */
public abstract class VanillaEntityItemCapabilityDelegator<C> {

    private final EntityItem entity;
    private final ForgeDirection side;

    public VanillaEntityItemCapabilityDelegator(EntityItem entity, @Nullable ForgeDirection side) {
        this.entity = entity;
        this.side = side;
    }

    public EntityItem getEntity() {
        return entity;
    }

    @Nullable
    public ForgeDirection getSide() {
        return side;
    }

    protected ItemStack getItemStack() {
        return entity.getEntityItem();
    }

    protected void updateItemStack(ItemStack itemStack) {
        entity.setEntityItemStack(itemStack);
    }

    protected abstract Capability<C> getCapabilityType();

    @NotNull
    protected LazyOptional<C> getCapability(ItemStack stack) {
        if (stack != null) {
            return CapabilityHelpers.getCapability(stack, getCapabilityType(), getSide());
        }
        return LazyOptional.empty();
    }

    @NotNull
    protected LazyOptional<C> getCapability() {
        return getCapability(getItemStack());
    }
}
