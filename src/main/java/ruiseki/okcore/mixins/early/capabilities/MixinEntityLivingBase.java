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
    private IItemHandlerModifiable okcore$handHandler;

    @Unique
    private IItemHandlerModifiable okcore$armorHandler;

    @Unique
    private IItemHandler okcore$joinedHandler;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void okcore$initHandlers(CallbackInfo ci) {
        EntityLivingBase entity = (EntityLivingBase) (Object) this;

        okcore$handHandler = new EntityHandsInvWrapper(entity);
        okcore$armorHandler = new EntityArmorInvWrapper(entity);
        okcore$joinedHandler = new CombinedInvWrapper(okcore$handHandler, okcore$armorHandler);
    }

    @Override
    public <T> @NotNull LazyOptional<T> okcorecap$getCapability(@NotNull Capability<T> capability,
        @Nullable ForgeDirection side) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER) {
            if (side == null) {
                return LazyOptional.of(() -> okcore$joinedHandler)
                    .cast();
            }
            if (side == ForgeDirection.UP || side == ForgeDirection.DOWN) {
                return LazyOptional.of(() -> okcore$handHandler)
                    .cast();
            }
            return LazyOptional.of(() -> okcore$joinedHandler)
                .cast();
        }

        return super.okcorecap$getCapability(capability, side);
    }

    public NBTTagCompound okcorecap$serializeNBT() {
        return super.okcorecap$serializeNBT();
    }

    public void okcorecap$deserializeNBT(NBTTagCompound tag) {
        super.okcorecap$deserializeNBT(tag);
    }
}
