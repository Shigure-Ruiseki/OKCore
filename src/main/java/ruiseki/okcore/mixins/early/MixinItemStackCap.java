package ruiseki.okcore.mixins.early;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

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

    @Inject(method = "<init>(Lnet/minecraft/item/Item;II)V", at = @At("RETURN"))
    private void okcore$initItem(Item item, int size, int damage, CallbackInfo ci) {
        this.initCaps();
    }

    /*
     * INTERNAL CAP INIT
     */

    private void initCaps() {
        ItemStack self = (ItemStack) (Object) this;

        if (self.getItem() == null) return;

        ICapabilityProvider parent = null;

        if (self.getItem() instanceof IItemCapability capItem) {
            parent = capItem.initCapabilities(self, this.capNBT);
        }

        this.capabilities = OKEventFactory.gatherCapabilities(self, parent);

        if (this.capNBT != null && this.capabilities != null) {
            this.capabilities.deserializeNBT(this.capNBT);
        }
    }

    /*
     * WRITE NBT
     */
    @Inject(method = "writeToNBT", at = @At("RETURN"))
    private void okcore$writeCaps(NBTTagCompound tag, CallbackInfoReturnable<NBTTagCompound> cir) {

        if (this.capabilities != null) {
            NBTTagCompound caps = this.capabilities.serializeNBT();
            if (!caps.hasNoTags()) {
                tag.setTag("OKCaps", caps);
            }
        }
    }

    /*
     * READ NBT
     */
    @Inject(method = "readFromNBT", at = @At("HEAD"))
    private void okcore$readCaps(NBTTagCompound tag, CallbackInfo ci) {
        if (tag.hasKey("OKCaps")) {
            this.capNBT = tag.getCompoundTag("OKCaps");
        }
    }

    @Inject(method = "readFromNBT", at = @At("RETURN"))
    private void okcore$applyCaps(NBTTagCompound tag, CallbackInfo ci) {
        if (this.capabilities == null) {
            this.initCaps();
        } else if (this.capNBT != null) {
            this.capabilities.deserializeNBT(this.capNBT);
        }

        this.capNBT = null;
    }

    /*
     * CAPABILITY API
     */

    public boolean okstackcap$hasCapability(Capability<?> capability, ForgeDirection side) {
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
