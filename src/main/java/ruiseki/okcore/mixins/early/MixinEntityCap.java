package ruiseki.okcore.mixins.early;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityDispatcher;
import ruiseki.okcore.capabilities.ICapabilitySerializable;
import ruiseki.okcore.event.OKEventFactory;

@Mixin(Entity.class)
@Implements(@Interface(iface = ICapabilitySerializable.class, prefix = "okcorecap$"))
public abstract class MixinEntityCap {

    @Shadow
    public abstract void writeToNBT(NBTTagCompound tagCompund);

    @Shadow
    public abstract void readFromNBT(NBTTagCompound tagCompund);

    @Unique
    private CapabilityDispatcher okcore$capabilities;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void okcore$initCaps(CallbackInfo ci) {
        this.okcore$capabilities = OKEventFactory.gatherCapabilities((Entity) (Object) this);
    }

    @Inject(
        method = "writeToNBT",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/nbt/NBTTagCompound;setTag(Ljava/lang/String;Lnet/minecraft/nbt/NBTBase;)V",
            shift = At.Shift.AFTER),
        slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=ForgeData")))
    private void okcore$writeCapsAfterForgeData(NBTTagCompound tag, CallbackInfo ci) {

        if (this.okcore$capabilities != null) {
            tag.setTag("OKCaps", this.okcore$capabilities.serializeNBT());
        }
    }

    @Inject(
        method = "readFromNBT",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/nbt/NBTTagCompound;getCompoundTag(Ljava/lang/String;)Lnet/minecraft/nbt/NBTTagCompound;",
            shift = At.Shift.AFTER),
        slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=ForgeData")))
    private void okcore$readCapsAfterForgeData(NBTTagCompound tag, CallbackInfo ci) {
        if (this.okcore$capabilities != null && tag.hasKey("OKCaps")) {
            this.okcore$capabilities.deserializeNBT(tag.getCompoundTag("OKCaps"));
        }
    }

    public boolean okcorecap$hasCapability(@NotNull Capability<?> capability, @Nullable ForgeDirection facing) {
        return this.okcore$capabilities != null && this.okcore$capabilities.hasCapability(capability, facing);
    }

    public <T> T okcorecap$getCapability(Capability<T> capability, ForgeDirection facing) {
        return this.okcore$capabilities == null ? null : this.okcore$capabilities.getCapability(capability, facing);
    }

    public NBTTagCompound okcorecap$serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return tag;
    }

    public void okcorecap$deserializeNBT(NBTTagCompound tag) {
        this.readFromNBT(tag);
    }
}
