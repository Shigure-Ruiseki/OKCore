package ruiseki.okcore.mixins.early.capabilities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityCache;
import ruiseki.okcore.capabilities.ICapabilitySerializable;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.item.capability.wrapper.ChestHandlerResolver;

@NotNullByDefault
@Mixin(TileEntityChest.class)
@Implements(@Interface(iface = ICapabilitySerializable.class, prefix = "okcorecap$"))
public abstract class MixinTileEntityChest extends MixinTileEntity {

    @Unique
    private final CapabilityCache okcore$cache = new CapabilityCache();

    @Inject(method = "<init>*", at = @At("RETURN"))
    private void okcore$init(CallbackInfo ci) {
        TileEntityChest self = (TileEntityChest) (Object) this;
        okcore$cache.addCapabilityResolver(new ChestHandlerResolver(self));
    }

    @Override
    public <T> LazyOptional<T> okcorecap$getCapability(Capability<T> capability, @Nullable ForgeDirection facing) {
        LazyOptional<T> result = okcore$cache.getCapability(capability, facing);
        if (result.isPresent()) {
            return result;
        }
        return super.okcorecap$getCapability(capability, facing);
    }

    @Inject(method = "invalidate", at = @At("RETURN"))
    private void okcore$invalidate(CallbackInfo ci) {
        okcore$cache.invalidateAll();
    }

    public NBTTagCompound okcorecap$serializeNBT() {
        return super.okcorecap$serializeNBT();
    }

    public void okcorecap$deserializeNBT(NBTTagCompound tag) {
        super.okcorecap$deserializeNBT(tag);
    }
}
