package ruiseki.okcore.mixins.early.capabilities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilitySerializable;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.item.capability.wrapper.CombinedInvWrapper;
import ruiseki.okcore.item.capability.wrapper.EntityArmorInvWrapper;
import ruiseki.okcore.item.capability.wrapper.EntityHandsInvWrapper;
import ruiseki.okcore.item.handler.IItemHandler;
import ruiseki.okcore.item.handler.IItemHandlerModifiable;

@Mixin(EntityLivingBase.class)
@Implements(@Interface(iface = ICapabilitySerializable.class, prefix = "okcorecap$"))
public class MixinEntityLivingBase extends MixinEntityCap {

    @Unique
    private final IItemHandlerModifiable okcore$handHandler = new EntityHandsInvWrapper(this.okcore$getThis());

    @Unique
    private final IItemHandlerModifiable okcore$armorHandler = new EntityArmorInvWrapper(this.okcore$getThis());

    @Unique
    private final IItemHandler okcore$joinedHandler = new CombinedInvWrapper(okcore$handHandler, okcore$armorHandler);

    @Unique
    private final LazyOptional<IItemHandler> okcore$handHandlerOptional = LazyOptional.of(() -> okcore$handHandler);

    @Unique
    private final LazyOptional<IItemHandler> okcore$armorHandlerOptional = LazyOptional.of(() -> okcore$armorHandler);

    @Unique
    private final LazyOptional<IItemHandler> okcore$joinedHandlerOptional = LazyOptional.of(() -> okcore$joinedHandler);

    @Override
    public <T> @NotNull LazyOptional<T> okcorecap$getCapability(@NotNull Capability<T> capability,
        @Nullable ForgeDirection side) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER) {
            if (side == null) {
                return okcore$joinedHandlerOptional.cast();
            }
            if (side == ForgeDirection.UP || side == ForgeDirection.DOWN) {
                return okcore$handHandlerOptional.cast();
            }
            return okcore$armorHandlerOptional.cast();
        }

        return super.okcorecap$getCapability(capability, side);
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void okcore$invalidateCapabilities(CallbackInfo ci) {
        okcore$handHandlerOptional.invalidate();
        okcore$armorHandlerOptional.invalidate();
        okcore$joinedHandlerOptional.invalidate();
    }

    public NBTTagCompound okcorecap$serializeNBT() {
        return super.okcorecap$serializeNBT();
    }

    public void okcorecap$deserializeNBT(NBTTagCompound tag) {
        super.okcorecap$deserializeNBT(tag);
    }

    @Unique
    private EntityLivingBase okcore$getThis() {
        return (EntityLivingBase) (Object) this;
    }
}
