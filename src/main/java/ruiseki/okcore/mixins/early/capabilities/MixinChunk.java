package ruiseki.okcore.mixins.early.capabilities;

import net.minecraft.world.chunk.Chunk;
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
import ruiseki.okcore.event.OKEventFactory;

@Mixin(Chunk.class)
@Implements({ @Interface(iface = ICapabilityProvider.class, prefix = "okcorecap$"),
    @Interface(iface = ICapabilityInternal.class, prefix = "okcoreinternal$") })
public abstract class MixinChunk {

    @Unique
    private CapabilityDispatcher okcore$capabilities;

    @Inject(method = "<init>(Lnet/minecraft/world/World;II)V", at = @At("RETURN"))
    private void okcore$initCaps(CallbackInfo ci) {
        this.okcore$capabilities = OKEventFactory.gatherCapabilities((Chunk) (Object) this);
    }

    public boolean okcorecap$hasCapability(@NotNull Capability<?> capability, @Nullable ForgeDirection facing) {
        return this.okcore$capabilities != null && this.okcore$capabilities.hasCapability(capability, facing);
    }

    @Nullable
    public <T> T okcorecap$getCapability(@NotNull Capability<T> capability, @Nullable ForgeDirection facing) {
        return this.okcore$capabilities == null ? null : this.okcore$capabilities.getCapability(capability, facing);
    }

    public CapabilityDispatcher okcoreinternal$getCapabilities() {
        return okcore$capabilities;
    }
}
