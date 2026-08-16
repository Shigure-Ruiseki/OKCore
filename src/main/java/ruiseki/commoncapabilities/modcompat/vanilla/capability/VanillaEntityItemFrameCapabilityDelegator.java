package ruiseki.commoncapabilities.modcompat.vanilla.capability;

import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.helper.CapabilityHelpers;

/**
 * An abstract capability capability delegator from entity item frame to inner itemstack.
 * 
 * @param <C> The capability type.
 * @author rubensworks
 */
public abstract class VanillaEntityItemFrameCapabilityDelegator<C> {

    private final EntityItemFrame entity;
    private final ForgeDirection side;

    public VanillaEntityItemFrameCapabilityDelegator(EntityItemFrame entity, ForgeDirection side) {
        this.entity = entity;
        this.side = side;
    }

    public EntityItemFrame getEntity() {
        return entity;
    }

    public ForgeDirection getSide() {
        return side;
    }

    protected ItemStack getItemStack() {
        return entity.getDisplayedItem();
    }

    protected void updateItemStack(ItemStack itemStack) {
        entity.setDisplayedItem(itemStack);
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
