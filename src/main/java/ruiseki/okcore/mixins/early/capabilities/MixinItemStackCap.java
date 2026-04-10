package ruiseki.okcore.mixins.early.capabilities;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityDispatcher;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.capabilities.ICapabilitySerializable;
import ruiseki.okcore.capabilities.IItemCapability;
import ruiseki.okcore.event.OKEventFactory;

@Mixin(ItemStack.class)
@Implements(@Interface(iface = ICapabilitySerializable.class, prefix = "okstackcap$"))
public abstract class MixinItemStackCap {

    @Shadow
    public abstract NBTTagCompound writeToNBT(NBTTagCompound p_77955_1_);

    @Shadow
    public abstract void readFromNBT(NBTTagCompound p_77963_1_);

    private CapabilityDispatcher capabilities;
    private NBTTagCompound capNBT;

    /*
     * INTERNAL CAP INIT
     */

    @Inject(method = "func_150996_a", at = @At("RETURN"))
    private void okcore$forgeInit(Item item, CallbackInfo ci) {
        if (item instanceof IItemCapability capItem) {
            ItemStack stack = (ItemStack) (Object) this;

            ICapabilityProvider provider = capItem.initCapabilities(stack, this.capNBT);
            this.capabilities = OKEventFactory.gatherCapabilities(stack, provider);
            if (this.capNBT != null && this.capabilities != null) this.capabilities.deserializeNBT(this.capNBT);
        }
    }

    @Inject(method = "readFromNBT", at = @At("HEAD"))
    private void okcore$readFromNBT(NBTTagCompound tag, CallbackInfo ci) {
        this.capNBT = tag.hasKey("OKCaps") ? (NBTTagCompound) tag.getTag("OKCaps") : null;
    }

    @Inject(method = "writeToNBT", at = @At("RETURN"))
    private void okcore$writeToNBT(NBTTagCompound tag, CallbackInfoReturnable<NBTTagCompound> cir) {
        if (this.capabilities != null) {
            NBTTagCompound cnbt = this.capabilities.serializeNBT();
            if (!cnbt.hasNoTags()) {
                tag.setTag("OKCaps", cnbt);
            }
        }
    }

    @Inject(method = "copy", at = @At("RETURN"))
    private void okcore$copyCaps(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = cir.getReturnValue();
        if (this.capabilities != null) {
            NBTTagCompound caps = this.capabilities.serializeNBT();
            if (!caps.hasNoTags()) {
                stack.setTagInfo("OKCaps", caps);
                ((MixinItemStackCap) (Object) stack).capNBT = caps;
                stack.func_150996_a(stack.getItem());
            }
        }
    }

    /*
     * CAPABILITY API
     */

    public boolean okstackcap$hasCapability(@NotNull Capability<?> capability, ForgeDirection side) {
        return this.capabilities != null && this.capabilities.hasCapability(capability, side);
    }

    public <T> T okstackcap$getCapability(Capability<T> capability, ForgeDirection side) {
        return this.capabilities == null ? null : this.capabilities.getCapability(capability, side);
    }

    public NBTTagCompound okstackcap$serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return tag;
    }

    public void okstackcap$deserializeNBT(NBTTagCompound tag) {
        this.readFromNBT(tag);
    }
}
