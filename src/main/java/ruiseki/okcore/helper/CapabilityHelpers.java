package ruiseki.okcore.helper;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityDispatcher;
import ruiseki.okcore.capabilities.ICapabilityInternal;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.datastructure.LazyOptional;

public class CapabilityHelpers {

    public static <T> LazyOptional<T> getCapability(ItemStack stack, @NotNull Capability<T> capability) {
        if (stack == null) return LazyOptional.empty();
        try {
            ICapabilityProvider provider = (ICapabilityProvider) (Object) stack;

            return provider.getCapability(capability);

        } catch (ClassCastException ignored) {
            return LazyOptional.empty();
        }
    }

    public static CapabilityDispatcher getCapabilities(ItemStack stack) {
        if (stack == null) return null;
        try {
            ICapabilityInternal provider = (ICapabilityInternal) (Object) stack;

            return provider.getCapabilities();

        } catch (ClassCastException ignored) {
            return null;
        }
    }

    public static <C> LazyOptional<C> getCapability(TileEntity tile, @NotNull Capability<C> capability,
        @NotNull ForgeDirection side) {
        if (tile instanceof ICapabilityProvider provider) {
            return provider.getCapability(capability, side);
        }
        return LazyOptional.empty();
    }

    public static <C> LazyOptional<C> getCapability(DimPos dimPos, @NotNull Capability<C> capability,
        @NotNull ForgeDirection side) {
        World world = dimPos.getWorld();
        return (world != null) ? getCapability(world, dimPos.getBlockPos(), capability, side) : LazyOptional.empty();
    }

    public static <C> LazyOptional<C> getCapability(IBlockAccess world, BlockPos pos, Capability<C> capability,
        @NotNull ForgeDirection side) {
        return getCapability(pos.getTileEntity(world), capability, side);
    }

    public static CapabilityDispatcher getCapabilities(TileEntity tile) {
        if (tile == null) return null;
        try {
            ICapabilityInternal provider = (ICapabilityInternal) (Object) tile;

            return provider.getCapabilities();

        } catch (ClassCastException ignored) {
            return null;
        }
    }

    public static <T> LazyOptional<T> getCapability(Chunk chunk, @NotNull Capability<T> capability) {
        if (chunk == null) return null;
        try {
            ICapabilityProvider provider = (ICapabilityProvider) (Object) chunk;

            return provider.getCapability(capability);

        } catch (ClassCastException ignored) {
            return LazyOptional.empty();
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

    public static <T> LazyOptional<T> getCapability(Entity stack, @NotNull Capability<T> capability) {
        if (stack == null) return null;
        try {
            ICapabilityProvider provider = (ICapabilityProvider) (Object) stack;

            return provider.getCapability(capability);

        } catch (ClassCastException ignored) {
            return LazyOptional.empty();
        }
    }

    public static CapabilityDispatcher getCapabilities(Entity entity) {
        if (entity == null) return null;
        try {
            ICapabilityInternal provider = (ICapabilityInternal) (Object) entity;

            return provider.getCapabilities();

        } catch (ClassCastException ignored) {
            return null;
        }
    }
}
