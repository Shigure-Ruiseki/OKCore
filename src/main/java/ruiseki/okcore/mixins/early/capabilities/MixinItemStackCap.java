
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
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.item.IItemStackCap;

@Mixin(ItemStack.class)
@Implements({ @Interface(iface = ICapabilitySerializable.class, prefix = "okcorecap$"),
    @Interface(iface = ICapabilityInternal.class, prefix = "okcoreinternal$"),
    @Interface(iface = IItemStackCap.class, prefix = "okcorenbtcap$") })
public abstract class MixinItemStackCap {

    @Unique
    private static final String OKCORE_CAPS_KEY = "OKCaps";

    @Unique
    private CapabilityDispatcher okcore$capabilities;

    @Unique
    private NBTTagCompound okcore$capNBT;

    @Unique
    private boolean okcore$initialized;

    /*
     * LAZY INITIALIZATION
     */

    @Unique
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void okcore$initCapabilitiesLazy() {
        if (this.okcore$initialized) {
            return;
        }

        ItemStack stack = this.okcore$getThis();
        Item item = stack.getItem();
        if (ItemHelpers.isEmpty(stack)) return;

        ICapabilityProvider provider = null;
        if (item instanceof IItemCapability capItem) {
            provider = capItem.initCapabilities(stack, this.okcore$capNBT);
        }

        CapabilityDispatcher dispatcher = OKEventFactory
            .gatherCapabilities((Class) ItemStack.class, (ICapabilityProvider) this, provider);

        if (dispatcher != null && this.okcore$capNBT != null) {
            dispatcher.deserializeNBT(this.okcore$capNBT);
        }

        this.okcore$capabilities = dispatcher;
        this.okcore$initialized = true;

        if (dispatcher != null) {
            this.okcore$capNBT = null;
        }
    }

    /*
     * NBT SERIALIZATION
     */

    @Unique
    @Nullable
    private NBTTagCompound okcore$serializeCaps() {
        if (this.okcore$initialized && this.okcore$capabilities != null) {
            return this.okcore$capabilities.serializeNBT();
        }
        return this.okcore$capNBT;
    }

    @Inject(method = "readFromNBT", at = @At("RETURN"))
    private void okcore$readFromNBT(NBTTagCompound tag, CallbackInfo ci) {
        this.okcore$capabilities = null;
        this.okcore$initialized = false;
        this.okcore$capNBT = tag != null && tag.hasKey(OKCORE_CAPS_KEY, 10)
            ? (NBTTagCompound) tag.getCompoundTag(OKCORE_CAPS_KEY)
                .copy()
            : null;
    }

    @Inject(method = "writeToNBT", at = @At("RETURN"))
    private void okcore$writeToNBT(NBTTagCompound tag, CallbackInfoReturnable<NBTTagCompound> cir) {
        NBTTagCompound output = cir.getReturnValue();
        if (output == null) return;
        NBTTagCompound caps = this.okcore$serializeCaps();
        if (caps != null && !caps.hasNoTags()) {
            output.setTag(OKCORE_CAPS_KEY, caps.copy());
        } else {
            output.removeTag(OKCORE_CAPS_KEY);
        }
    }

    /*
     * COPY
     */

    @Inject(method = "copy", at = @At("RETURN"))
    private void okcore$copyCaps(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack copy = cir.getReturnValue();
        if (copy == null) return;
        MixinItemStackCap target = (MixinItemStackCap) (Object) copy;
        NBTTagCompound serialized = this.okcore$serializeCaps();
        target.okcore$capabilities = null;
        target.okcore$initialized = false;
        target.okcore$capNBT = serialized != null ? (NBTTagCompound) serialized.copy() : null;
    }

    /*
     * CAPABILITY API
     */

    @NotNull
    public <T> LazyOptional<T> okcorecap$getCapability(@NotNull Capability<T> capability,
        @Nullable ForgeDirection facing) {
        this.okcore$initCapabilitiesLazy();
        return this.okcore$capabilities == null ? LazyOptional.empty()
            : this.okcore$capabilities.getCapability(capability, facing);
    }

    public NBTTagCompound okcorecap$serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        return this.okcore$getThis()
            .writeToNBT(tag);
    }

    public void okcorecap$deserializeNBT(NBTTagCompound tag) {
        this.okcore$getThis()
            .readFromNBT(tag);
    }

    public CapabilityDispatcher okcoreinternal$getCapabilities() {
        this.okcore$initCapabilitiesLazy();
        return this.okcore$capabilities;
    }

    @Nullable
    public NBTTagCompound okcorenbtcap$getCapNBT() {
        return this.okcore$capNBT == null ? null : (NBTTagCompound) this.okcore$capNBT.copy();
    }

    @Unique
    private ItemStack okcore$getThis() {
        return (ItemStack) (Object) this;
    }
}
