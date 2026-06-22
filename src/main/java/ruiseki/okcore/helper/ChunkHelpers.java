package ruiseki.okcore.helper;

import net.minecraft.world.chunk.Chunk;

import org.jetbrains.annotations.NotNull;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityDispatcher;
import ruiseki.okcore.capabilities.ICapabilityInternal;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.datastructure.LazyOptional;

public class ChunkHelpers {

    public static <T> LazyOptional<T> getCapability(Chunk chunk, @NotNull Capability<T> capability) {
        if (chunk == null) return null;
        try {
            ICapabilityProvider provider = (ICapabilityProvider) (Object) chunk;

            return provider.getCapability(capability);

        } catch (ClassCastException ignored) {
            return null;
        }
    }

    public static CapabilityDispatcher getCapabilities(Chunk chunk) {
        if (chunk == null) return null;
        try {
            ICapabilityInternal provider = (ICapabilityInternal) (Object) chunk;

            return provider.getCapabilities();

        } catch (ClassCastException ignored) {
            return null;
        }
    }
}
