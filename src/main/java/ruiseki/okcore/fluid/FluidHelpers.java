package ruiseki.okcore.fluid;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import org.intellij.lang.annotations.MagicConstant;

import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.capability.FluidSink;
import ruiseki.okcore.fluid.capability.FluidSource;
import ruiseki.okcore.fluid.capability.IFluidSink;
import ruiseki.okcore.fluid.capability.IFluidSource;
import ruiseki.okcore.helper.CapabilityHelpers;

public class FluidHelpers {

    public static final int BUCKET_VOLUME = FluidContainerRegistry.BUCKET_VOLUME;

    private static int counter = 0;
    public static final int WRAP_HANDLER = 0b1 << counter++;
    public static final int FOR_INSERTS = 0b1 << counter++;
    public static final int FOR_EXTRACTS = 0b1 << counter++;
    public static final int DEFAULT = WRAP_HANDLER | FOR_INSERTS | FOR_EXTRACTS;

    public static IFluidSource getFluidSource(Object obj, ForgeDirection side) {
        return getFluidSource(obj, side, DEFAULT);
    }

    public static IFluidSource getFluidSource(Object obj, ForgeDirection side,
        @MagicConstant(flagsFromClass = FluidHelpers.class) int usage) {
        if ((usage & FOR_EXTRACTS) == 0) {
            return null;
        }

        if (obj instanceof IFluidSource source) {
            return source;
        }

        if (obj instanceof ICapabilityProvider capabilityProvider) {
            IFluidSource source = capabilityProvider.getCapability(CapabilityFluidHandler.FLUID_SOURCE_CAPABILITY, side)
                .resolveOrNull();
            if (source != null) {
                return source;
            }
        }

        if (obj instanceof IFluidHandler handler) {
            IFluidSource source = new FluidSource(handler, side);
            if (source != null) {
                return source;
            }
        }

        return null;
    }

    public static IFluidSink getFluidSink(Object obj, ForgeDirection side) {
        return getFluidSink(obj, side, DEFAULT);
    }

    public static IFluidSink getFluidSink(Object obj, ForgeDirection side,
        @MagicConstant(flagsFromClass = FluidHelpers.class) int usage) {
        if ((usage & FOR_INSERTS) == 0) {
            return null;
        }

        if (obj instanceof IFluidSink sink) {
            return sink;
        }

        if (obj instanceof ICapabilityProvider capabilityProvider) {
            IFluidSink sink = capabilityProvider.getCapability(CapabilityFluidHandler.FLUID_SINK_CAPABILITY, side)
                .resolveOrNull();
            if (sink != null) {
                return sink;
            }
        }

        if (obj instanceof IFluidHandler handler) {
            IFluidSink sink = new FluidSink(handler, side);
            if (sink != null) {
                return sink;
            }
        }

        return null;
    }

    public static LazyOptional<IFluidHandlerItem> getFluidHandler(ItemStack stack) {
        return CapabilityHelpers.getCapability(stack, CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
    }

    public static LazyOptional<IFluidHandler> getFluidHandler(TileEntity tile, ForgeDirection side) {
        return CapabilityHelpers.getCapability(tile, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
    }

    public static FluidActionResult tryPlaceFluid(World world, BlockPos pos, FluidStack fluidStack) {
        if (world == null || pos == null || fluidStack == null || fluidStack.getFluid() == null) {
            return new FluidActionResult(false, null);
        }

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        Block block = world.getBlock(x, y, z);
        boolean isReplaceable = block.isReplaceable(world, x, y, z);

        if (!world.isAirBlock(x, y, z) && !isReplaceable) {
            return new FluidActionResult(false, null);
        }

        Fluid fluid = fluidStack.getFluid();
        Block fluidBlock = fluid.getBlock();

        if (fluidBlock != null) {
            if (fluid == FluidRegistry.WATER) {
                world.setBlock(x, y, z, Blocks.flowing_water, 0, 3);
            } else if (fluid == FluidRegistry.LAVA) {
                world.setBlock(x, y, z, Blocks.flowing_lava, 0, 3);
            } else {
                world.setBlock(x, y, z, fluidBlock, 0, 3);
            }
            return new FluidActionResult(true, null);
        }

        return new FluidActionResult(false, null);
    }

    public static FluidActionResult tryPickUpFluid(ItemStack stack, World world, BlockPos pos, ForgeDirection facing) {
        if (stack == null || world == null || pos == null) {
            return new FluidActionResult(false, stack);
        }

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        Block block = world.getBlock(x, y, z);
        int metadata = world.getBlockMetadata(x, y, z);
        Fluid targetFluid = null;

        if (block == Blocks.water || block == Blocks.flowing_water) {
            if (metadata == 0) targetFluid = FluidRegistry.WATER;
        } else if (block == Blocks.lava || block == Blocks.flowing_lava) {
            if (metadata == 0) targetFluid = FluidRegistry.LAVA;
        } else if (block instanceof BlockFluidBase fluidBase) {
            if (fluidBase.getFilledPercentage(world, x, y, z) >= 1.0f) {
                targetFluid = fluidBase.getFluid();
            }
        }

        if (targetFluid != null) {
            FluidStack fluidStack = new FluidStack(targetFluid, BUCKET_VOLUME);
            ItemStack filledContainer = FluidContainerRegistry.fillFluidContainer(fluidStack, stack);

            if (filledContainer != null) {
                world.setBlockToAir(x, y, z);
                return new FluidActionResult(true, filledContainer);
            }
        }

        return new FluidActionResult(false, stack);
    }
}
