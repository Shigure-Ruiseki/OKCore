package ruiseki.okcore.mixins.early;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ruiseki.okcore.capabilities.AttachCapabilitiesEvent;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityDispatcher;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.capabilities.ICapabilitySerializable;
import ruiseki.okcore.capabilities.IItemCapability;

@Mixin(ItemStack.class)
public abstract class MixinItemStackCap implements ICapabilitySerializable {

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract NBTTagCompound writeToNBT(NBTTagCompound tag);

    @Shadow
    public abstract void readFromNBT(NBTTagCompound tag);

    private CapabilityDispatcher capabilities;
    private NBTTagCompound capNBT;

    @Inject(method = "<init>(Lnet/minecraft/item/Item;II)V", at = @At("RETURN"))
    private void okcore$init(Item item, int size, int damage, CallbackInfo ci) {
        this.initCaps(null);
    }

    /*
     * INTERNAL CAP INIT
     */
    private void initCaps(@Nullable NBTTagCompound nbt) {
        ItemStack self = (ItemStack) (Object) this;
        if (self.getItem() == null) {
            return;
        }

        ICapabilityProvider parent = null;

        // Item provided cap
        if (self.getItem() instanceof IItemCapability capItem) {
            parent = capItem.initCapabilities(self, nbt);
        }

        // Event caps
        AttachCapabilitiesEvent<ItemStack> event = new AttachCapabilitiesEvent<>(ItemStack.class, self);

        MinecraftForge.EVENT_BUS.post(event);

        if (parent != null || !event.getCapabilities()
            .isEmpty()) {
            this.capabilities = new CapabilityDispatcher(event.getCapabilities(), parent);

            if (nbt != null) {
                this.capabilities.deserializeNBT(nbt);
            }
        }
    }

    /*
     * WRITE NBT
     */
    @Inject(method = "writeToNBT", at = @At("RETURN"))
    private void okcore$writeCaps(NBTTagCompound tag, CallbackInfoReturnable<NBTTagCompound> cir) {
        if (this.capabilities != null) {
            tag.setTag("ForgeCaps", this.capabilities.serializeNBT());
        }
    }

    /*
     * READ NBT
     */
    @Inject(method = "readFromNBT", at = @At("HEAD"))
    private void okcore$readCaps(NBTTagCompound tag, CallbackInfo ci) {
        if (tag.hasKey("ForgeCaps")) {
            this.capNBT = tag.getCompoundTag("ForgeCaps");
        }
    }

    @Inject(method = "readFromNBT", at = @At("RETURN"))
    private void okcore$applyCaps(NBTTagCompound tag, CallbackInfo ci) {
        if (this.capabilities == null) {
            this.initCaps(this.capNBT);
        } else if (this.capNBT != null) {
            this.capabilities.deserializeNBT(this.capNBT);
        }

        this.capNBT = null;
    }

    /*
     * CAPABILITY API
     */
    @Override
    public boolean hasCapability(Capability<?> capability, ForgeDirection side) {
        return this.capabilities != null && this.capabilities.hasCapability(capability, side);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, ForgeDirection side) {
        return this.capabilities == null ? null : this.capabilities.getCapability(capability, side);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        this.readFromNBT(tag);
    }
}
