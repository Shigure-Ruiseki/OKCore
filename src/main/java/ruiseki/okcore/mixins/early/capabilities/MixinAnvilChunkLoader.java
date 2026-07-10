package ruiseki.okcore.mixins.early.capabilities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;

import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.helper.CapabilityHelpers;

@Mixin(AnvilChunkLoader.class)
public abstract class MixinAnvilChunkLoader {

    @Inject(method = "writeChunkToNBT", at = @At("RETURN"))
    private void okcore$writeChunkCaps(Chunk chunk, World world, NBTTagCompound compound, CallbackInfo ci) {
        if (CapabilityHelpers.getCapabilities(chunk) != null) {
            try {
                compound.setTag(
                    "OKCaps",
                    CapabilityHelpers.getCapabilities(chunk)
                        .serializeNBT());
            } catch (Exception exception) {
                OKCore.okLog(
                    Level.ERROR,
                    "A capability provider has thrown an exception trying to write state. It will not persist. Report this to the mod author",
                    exception);
            }
        }
    }

    @Inject(method = "readChunkFromNBT", at = @At("RETURN"))
    private void okcore$readChunkCaps(World world, NBTTagCompound compound,
        @NotNull CallbackInfoReturnable<Chunk> cir) {
        Chunk chunk = cir.getReturnValue();
        if (chunk != null && compound.hasKey("OKCaps")) {
            if (CapabilityHelpers.getCapabilities(chunk) != null) {
                CapabilityHelpers.getCapabilities(chunk)
                    .deserializeNBT(compound.getCompoundTag("OKCaps"));
            }
        }
    }
}
