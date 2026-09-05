package ruiseki.okcore.mixins.early.capabilities;

import net.minecraft.entity.Entity;
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
import ruiseki.okcore.capabilities.CapabilityDispatcher;
import ruiseki.okcore.capabilities.ICapabilityInternal;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.capabilities.ICapabilitySerializable;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.event.OKEventFactory;

@Mixin(Entity.class)
@Implements({ @Interface(iface = ICapabilitySerializable.class, prefix = "okcorecap$"),
    @Interface(iface = ICapabilityInternal.class, prefix = "okcoreinternal$") })
public abstract class MixinEntityCap {

    @Unique
    private CapabilityDispatcher okcore$capabilities;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void okcore$initCaps(CallbackInfo ci) {
        this.okcore$capabilities = OKEventFactory.gatherCapabilities((Class) Entity.class, (ICapabilityProvider) this);
    }

    @Inject(
        method = "writeToNBT",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;writeEntityToNBT(Lnet/minecraft/nbt/NBTTagCompound;)V"))
    private void okcore$writeCapsBeforeWriteEntity(NBTTagCompound tag, CallbackInfo ci) {
        if (this.okcore$capabilities != null) {
            tag.setTag("OKCaps", this.okcore$capabilities.serializeNBT());
        }
    }

    @Inject(
        method = "readFromNBT",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;readEntityFromNBT(Lnet/minecraft/nbt/NBTTagCompound;)V"),
        require = 0)
    private void okcore$readCapsBeforeReadEntity(NBTTagCompound tag, CallbackInfo ci) {
        if (this.okcore$capabilities != null && tag.hasKey("OKCaps")) {
            this.okcore$capabilities.deserializeNBT(tag.getCompoundTag("OKCaps"));
        }
    }

    @Inject(method = "setDead", at = @At("RETURN"))
    private void okcore$invalidate(CallbackInfo ci) {
        if (this.okcore$capabilities != null) {
            this.okcore$capabilities.invalidate();
        }
    }

    public <T> @NotNull LazyOptional<T> okcorecap$getCapability(@NotNull Capability<T> capability,
        @Nullable ForgeDirection facing) {
        return this.okcore$capabilities == null ? LazyOptional.empty()
            : this.okcore$capabilities.getCapability(capability, facing);
    }

    public NBTTagCompound okcorecap$serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        this.okcore$getThis()
            .writeToNBT(tag);
        return tag;
    }

    public void okcorecap$deserializeNBT(NBTTagCompound tag) {
        this.okcore$getThis()
            .readFromNBT(tag);
    }

    public CapabilityDispatcher okcoreinternal$getCapabilities() {
        return okcore$capabilities;
    }

    @Unique
    private Entity okcore$getThis() {
        return (Entity) (Object) this;
    }
}
