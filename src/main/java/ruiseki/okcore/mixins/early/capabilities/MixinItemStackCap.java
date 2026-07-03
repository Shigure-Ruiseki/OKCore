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
import org.spongepowered.asm.mixin.Shadow;
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

    @Shadow
    public abstract NBTTagCompound writeToNBT(NBTTagCompound p_77955_1_);

    @Shadow
    public abstract void readFromNBT(NBTTagCompound p_77963_1_);

    @Unique
    private CapabilityDispatcher okcore$capabilities;
    @Unique
    private NBTTagCompound okcore$capNBT;

    /*
     * INTERNAL CAP INIT
     */
    @Inject(method = "func_150996_a", at = @At("RETURN"))
    private void okcore$forgeInit(Item item, CallbackInfo ci) {
        if (item == null) return;

        ItemStack stack = (ItemStack) (Object) this;
        ICapabilityProvider provider = null;

        if (item instanceof IItemCapability capItem) {
            provider = capItem.initCapabilities(stack, this.okcore$capNBT);
        }

        this.okcore$capabilities = OKEventFactory
            .gatherCapabilities((Class) ItemStack.class, (ICapabilityProvider) this, provider);
        if (this.okcore$capNBT != null && this.okcore$capabilities != null) {
            this.okcore$capabilities.deserializeNBT(this.okcore$capNBT);
        }
    }

    @Inject(method = "readFromNBT", at = @At("HEAD"))
    private void okcore$readFromNBT(NBTTagCompound tag, CallbackInfo ci) {
        this.okcore$capNBT = tag.hasKey("OKCaps") ? (NBTTagCompound) tag.getTag("OKCaps") : null;
    }

    @Inject(method = "writeToNBT", at = @At("RETURN"))
    private void okcore$writeToNBT(NBTTagCompound tag, CallbackInfoReturnable<NBTTagCompound> cir) {
        if (this.okcore$capabilities != null) {
            NBTTagCompound cnbt = this.okcore$capabilities.serializeNBT();
            if (cnbt != null && !cnbt.hasNoTags()) {
                tag.setTag("OKCaps", cnbt);
            }
        }
    }

    @Inject(method = "copy", at = @At("RETURN"))
    private void okcore$copyCaps(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = cir.getReturnValue();
        if (this.okcore$capabilities != null) {
            NBTTagCompound caps = this.okcore$capabilities.serializeNBT();
            if (!caps.hasNoTags()) {
                stack.setTagInfo("OKCaps", caps);
                ((MixinItemStackCap) (Object) stack).okcore$capNBT = caps;
                stack.func_150996_a(stack.getItem());
            }
        }
    }

    /*
     * CAPABILITY API
     */

    public <T> @NotNull LazyOptional<T> okcorecap$getCapability(@NotNull Capability<T> capability,
        @Nullable ForgeDirection facing) {
        return this.okcore$capabilities == null ? LazyOptional.empty()
            : this.okcore$capabilities.getCapability(capability, facing);
    }

    public NBTTagCompound okcorecap$serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return tag;
    }

    public void okcorecap$deserializeNBT(NBTTagCompound tag) {
        this.readFromNBT(tag);
    }

    public CapabilityDispatcher okcoreinternal$getCapabilities() {
        return okcore$capabilities;
    }
}
