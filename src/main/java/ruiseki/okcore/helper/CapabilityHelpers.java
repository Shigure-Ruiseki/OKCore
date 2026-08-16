package ruiseki.okcore.helper;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityDispatcher;
import ruiseki.okcore.capabilities.ICapabilityInternal;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.datastructure.LazyOptional;

public class CapabilityHelpers {

    // ItemStack

    public static <T> LazyOptional<T> getCapability(ItemStack stack, @NotNull Capability<T> capability) {
        return getCapability(stack, capability, null);
    }

    @SuppressWarnings("ConstantConditions")
    public static <T> LazyOptional<T> getCapability(ItemStack stack, @NotNull Capability<T> capability,
        @Nullable ForgeDirection side) {
        if (stack == null) return LazyOptional.empty();
        Object obj = stack;
        if (obj instanceof ICapabilityProvider provider) {
            return provider.getCapability(capability, side);
        }
        return LazyOptional.empty();
    }

    @SuppressWarnings("ConstantConditions")
    public static CapabilityDispatcher getCapabilities(ItemStack stack) {
        if (stack == null) return null;
        Object obj = stack;
        if (obj instanceof ICapabilityInternal provider) {
            return provider.getCapabilities();
        }
        return null;
    }

    // TileEntity

    public static <C> LazyOptional<C> getCapability(TileEntity tile, @NotNull Capability<C> capability) {
        return getCapability(tile, capability, null);
    }

    public static <C> LazyOptional<C> getCapability(TileEntity tile, @NotNull Capability<C> capability,
        @Nullable ForgeDirection side) {
        if (tile == null) return LazyOptional.empty();
        if (tile instanceof ICapabilityProvider provider) {
            return provider.getCapability(capability, side);
        }
        return LazyOptional.empty();
    }

    public static <C> LazyOptional<C> getCapability(DimPos dimPos, @NotNull Capability<C> capability) {
        return getCapability(dimPos, capability, null);
    }

    public static <C> LazyOptional<C> getCapability(DimPos dimPos, @NotNull Capability<C> capability,
        @Nullable ForgeDirection side) {
        World world = dimPos.getWorld();
        return (world != null) ? getCapability(world, dimPos.getBlockPos(), capability, side) : LazyOptional.empty();
    }

    public static <C> LazyOptional<C> getCapability(IBlockAccess world, int x, int y, int z,
        @NotNull Capability<C> capability) {
        return getCapability(world, x, y, z, capability, null);
    }

    public static <C> LazyOptional<C> getCapability(IBlockAccess world, int x, int y, int z,
        @NotNull Capability<C> capability, @Nullable ForgeDirection side) {
        return (world != null) ? getCapability(world, new BlockPos(x, y, z), capability, side) : LazyOptional.empty();
    }

    public static <C> LazyOptional<C> getCapability(IBlockAccess world, BlockPos pos, Capability<C> capability) {
        return getCapability(world, pos, capability, null);
    }

    public static <C> LazyOptional<C> getCapability(IBlockAccess world, BlockPos pos, Capability<C> capability,
        @Nullable ForgeDirection side) {
        return getCapability(pos.getTileEntity(world), capability, side);
    }

    public static CapabilityDispatcher getCapabilities(TileEntity tile) {
        if (tile == null) return null;
        if (tile instanceof ICapabilityInternal provider) {
            return provider.getCapabilities();
        }
        return null;
    }

    // Chunk

    public static <T> LazyOptional<T> getCapability(Chunk chunk, @NotNull Capability<T> capability) {
        return getCapability(chunk, capability, null);
    }

    public static <T> LazyOptional<T> getCapability(Chunk chunk, @NotNull Capability<T> capability,
        @Nullable ForgeDirection side) {
        if (chunk == null) return LazyOptional.empty();
        if (chunk instanceof ICapabilityProvider provider) {
            return provider.getCapability(capability, side);
        }
        return LazyOptional.empty();
    }

    public static CapabilityDispatcher getCapabilities(Chunk chunk) {
        if (chunk == null) return null;
        if (chunk instanceof ICapabilityInternal provider) {
            return provider.getCapabilities();
        }
        return null;
    }

    // Entity

    public static <T> LazyOptional<T> getCapability(Entity entity, @NotNull Capability<T> capability) {
        return getCapability(entity, capability, null);
    }

    public static <T> LazyOptional<T> getCapability(Entity entity, @NotNull Capability<T> capability,
        @Nullable ForgeDirection side) {
        if (entity == null) return LazyOptional.empty();
        if (entity instanceof ICapabilityProvider provider) {
            return provider.getCapability(capability, side);
        }
        return LazyOptional.empty();
    }

    public static CapabilityDispatcher getCapabilities(Entity entity) {
        if (entity == null) return null;
        if (entity instanceof ICapabilityInternal provider) {
            return provider.getCapabilities();
        }
        return null;
    }
}
