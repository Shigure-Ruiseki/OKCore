package ruiseki.okcore.helper;

import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityDispatcher;
import ruiseki.okcore.capabilities.ICapabilityInternal;
import ruiseki.okcore.capabilities.ICapabilityProvider;

public class ChunkHelpers {

    public static <T> T getCapability(Chunk chunk, Capability<T> capability, @Nullable ForgeDirection facing) {
        if (chunk == null) return null;
        try {
            ICapabilityProvider provider = (ICapabilityProvider) (Object) chunk;

            return provider.getCapability(capability, facing);

        } catch (ClassCastException ignored) {
            return null;
        }
    }

    public static boolean hasCapability(Chunk chunk, Capability<?> capability, @Nullable ForgeDirection facing) {
        if (chunk == null) return false;
        try {
            ICapabilityProvider provider = (ICapabilityProvider) (Object) chunk;

            return provider.hasCapability(capability, facing);

        } catch (ClassCastException ignored) {
            return false;
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
