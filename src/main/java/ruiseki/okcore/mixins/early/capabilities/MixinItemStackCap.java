package ruiseki.okcore.mixins.early.capabilities;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityDispatcher;
import ruiseki.okcore.capabilities.ICapabilityInternal;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.capabilities.ICapabilitySerializable;
import ruiseki.okcore.capabilities.IItemCapability;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.event.OKEventFactory;

@Mixin(ItemStack.class)
@Implements({ @Interface(iface = ICapabilitySerializable.class, prefix = "okcorecap$"),
    @Interface(iface = ICapabilityInternal.class, prefix = "okcoreinternal$") })
public abstract class MixinItemStackCap {

    @Unique
    private CapabilityDispatcher okcore$capabilities = null;
    @Unique
    private NBTTagCompound okcore$capNBT = null;
    @Unique
    private boolean okcore$initialized = false;

    /*
     * LAZY INITIALIZER
     */
    @Unique
    private void okcore$initCapabilitiesLazy() {
        if (this.okcore$initialized) return;
        this.okcore$initialized = true;

        ItemStack stack = this.okcore$getThis();
        Item item = stack.getItem();
        if (item == null) return;

        ICapabilityProvider provider = null;
        if (item instanceof IItemCapability capItem) {
            provider = capItem.initCapabilities(stack, this.okcore$capNBT);
        }

        this.okcore$capabilities = OKEventFactory
            .gatherCapabilities((Class) ItemStack.class, (ICapabilityProvider) this, provider);

        if (this.okcore$capabilities != null && this.okcore$capNBT != null) {
            this.okcore$capabilities.deserializeNBT(this.okcore$capNBT);
            this.okcore$capNBT = null;
        }
    }

    /*
     * NBT SERIALIZATION / DESERIALIZATION
     */
    @Inject(method = "readFromNBT", at = @At("HEAD"))
    private void okcore$readFromNBT(NBTTagCompound tag, CallbackInfo ci) {
        if (tag != null && tag.hasKey("OKCaps", 10)) { // 10 = NBTTagCompound
            this.okcore$capNBT = tag.getCompoundTag("OKCaps");
            if (this.okcore$initialized && this.okcore$capabilities != null) {
                this.okcore$capabilities.deserializeNBT(this.okcore$capNBT);
                this.okcore$capNBT = null;
            }
        } else {
            this.okcore$capNBT = null;
        }
    }

    @Inject(method = "writeToNBT", at = @At("RETURN"))
    private void okcore$writeToNBT(NBTTagCompound tag, CallbackInfoReturnable<NBTTagCompound> cir) {
        if (this.okcore$initialized && this.okcore$capabilities != null) {
            NBTTagCompound cnbt = this.okcore$capabilities.serializeNBT();
            if (!cnbt.hasNoTags()) {
                tag.setTag("OKCaps", cnbt);
            }
        } else if (this.okcore$capNBT != null && !this.okcore$capNBT.hasNoTags()) {
            tag.setTag("OKCaps", this.okcore$capNBT.copy());
        }
    }

    /*
     * FAST COPY
     */
    @Inject(method = "copy", at = @At("RETURN"))
    private void okcore$copyCaps(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack copy = cir.getReturnValue();
        if (copy == null) return;

        MixinItemStackCap copyMixin = (MixinItemStackCap) (Object) copy;

        if (this.okcore$initialized && this.okcore$capabilities != null) {
            NBTTagCompound serialized = this.okcore$capabilities.serializeNBT();
            if (serialized != null && !serialized.hasNoTags()) {
                copyMixin.okcore$capNBT = serialized;
            }
        } else if (this.okcore$capNBT != null) {
            copyMixin.okcore$capNBT = (NBTTagCompound) this.okcore$capNBT.copy();
        }
    }

    /*
     * CAPABILITY API
     */
    public <T> @NotNull LazyOptional<T> okcorecap$getCapability(@NotNull Capability<T> capability,
        @Nullable ForgeDirection facing) {
        okcore$initCapabilitiesLazy();
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
        okcore$initCapabilitiesLazy();
        return this.okcore$capabilities;
    }

    @Unique
    private ItemStack okcore$getThis() {
        return (ItemStack) (Object) this;
    }
}
