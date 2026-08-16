package ruiseki.okcore.fluid;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidTank;

import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.base.Preconditions;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.capability.FluidHandlerItemCapacityConfig;
import ruiseki.okcore.fluid.capability.wrapper.BlockLiquidWrapper;
import ruiseki.okcore.fluid.capability.wrapper.BlockWrapper;
import ruiseki.okcore.fluid.capability.wrapper.FluidBlockWrapper;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidHandlerItem;
import ruiseki.okcore.fluid.handler.IFluidHandlerItemCapacity;
import ruiseki.okcore.fluid.handler.IFluidTankProperties;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.ItemHandlerHelpers;
import ruiseki.okcore.helper.ItemStackHelpers;
import ruiseki.okcore.helper.TileHelpers;
import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.item.handler.IItemHandler;

public class FluidHelpers {

    public static final int BUCKET_VOLUME = FluidContainerRegistry.BUCKET_VOLUME;

    /**
     * Get the fluid amount of the given stack in a safe manner.
     *
     * @param fluidStack The fluid stack
     * @return The fluid amount.
     */
    public static int getAmount(@Nullable FluidStack fluidStack) {
        return fluidStack != null ? fluidStack.amount : 0;
    }

    public static String getModId(Fluid fluid) {
        String fullName = FluidRegistry.masterFluidReference.inverse()
            .get(fluid);
        if (fullName != null && fullName.contains(":")) {
            return fullName.split(":")[0];
        }
        return null;
    }

    /**
     * Copy the given fluid stack
     *
     * @param fluidStack The fluid stack to copy.
     * @return A copy of the fluid stack.
     */
    public static FluidStack copy(@Nullable FluidStack fluidStack) {
        if (fluidStack == null) return null;
        return fluidStack.copy();
    }

    /**
     * If this destination can completely contain the given fluid in the given source.
     *
     * @param source      The source of the fluid that has to be moved.
     * @param destination The target of the fluid that has to be moved.
     * @return If the destination can completely contain the fluid of the source.
     */
    public static boolean canCompletelyFill(IFluidHandler source, IFluidHandler destination) {
        FluidStack drained = source.drain(Integer.MAX_VALUE, false);
        return drained != null && destination.fill(drained, false) == drained.amount;
    }

    /**
     * Get the fluid contained in a fluid handler.
     *
     * @param fluidHandler The fluid handler.
     * @return The fluid.
     */
    public static FluidStack getFluid(@Nullable IFluidHandler fluidHandler) {
        return fluidHandler != null ? fluidHandler.drain(Integer.MAX_VALUE, false) : null;
    }

    /**
     * Check if the fluid handler is not empty.
     *
     * @param fluidHandler The fluid handler.
     * @return If it is not empty.
     */
    public static boolean hasFluid(@Nullable IFluidHandler fluidHandler) {
        return getFluid(fluidHandler) != null;
    }

    /**
     * Get the capacity of a fluid handler.
     *
     * @param fluidHandler The fluid handler.
     * @return The capacity.
     */
    public static int getCapacity(@Nullable IFluidHandler fluidHandler) {
        if (fluidHandler != null) {
            for (IFluidTankProperties properties : fluidHandler.getTankProperties()) {
                return properties.getCapacity();
            }
        }
        return 0;
    }

    /**
     * @param itemStack The itemstack
     * @return The item capacity fluid handler.
     */
    public static @Nullable IFluidHandlerItemCapacity getFluidHandlerItemCapacity(ItemStack itemStack) {
        return CapabilityHelpers.getCapability(itemStack, FluidHandlerItemCapacityConfig.CAPABILITY)
            .getOrNull();
    }

    /**
     * Used to handle the common case of a player holding a fluid item and right-clicking on a fluid handler block.
     * First it tries to fill the item from the block,
     * if that action fails then it tries to drain the item into the block.
     * Automatically updates the item in the player's hand and stashes any extra items created.
     *
     * @param player The player doing the interaction between the item and fluid handler block.
     * @param world  The world that contains the fluid handler block.
     * @param pos    The position of the fluid handler block in the world.
     * @param side   The side of the block to interact with. May be null.
     * @return true if the interaction succeeded and updated the item held by the player, false otherwise.
     */
    public static boolean interactWithFluidHandler(@Nonnull EntityPlayer player, @Nonnull World world,
        @Nonnull BlockPos pos, @Nullable ForgeDirection side) {
        Preconditions.checkNotNull(world);
        Preconditions.checkNotNull(pos);
        return getFluidHandler(world, pos, side).map(handler -> interactWithFluidHandler(player, handler, side))
            .orElse(false);
    }

    /**
     * Used to handle the common case of a player holding a fluid item and right-clicking on a fluid handler.
     * First it tries to fill the item from the handler,
     * if that action fails then it tries to drain the item into the handler.
     * Automatically updates the item in the player's hand and stashes any extra items created.
     *
     * @param player  The player doing the interaction between the item and fluid handler.
     * @param handler The fluid handler.
     * @return true if the interaction succeeded and updated the item held by the player, false otherwise.
     */
    public static boolean interactWithFluidHandler(@Nonnull EntityPlayer player, @Nonnull IFluidHandler handler,
        ForgeDirection side) {
        Preconditions.checkNotNull(player);
        Preconditions.checkNotNull(handler);

        ItemStack heldItem = player.getHeldItem();
        if (heldItem == null) {
            return false;
        }

        return CapabilityHelpers.getCapability(player, CapabilityItemHandler.ITEM_HANDLER)
            .map(playerInventory -> {
                FluidActionResult actionResult = tryFillContainerAndStow(
                    heldItem,
                    handler,
                    playerInventory,
                    Integer.MAX_VALUE,
                    player,
                    side,
                    true);

                if (!actionResult.isSuccess()) {
                    actionResult = tryEmptyContainerAndStow(
                        heldItem,
                        handler,
                        playerInventory,
                        Integer.MAX_VALUE,
                        player,
                        side,
                        true);
                }

                if (actionResult.isSuccess()) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, actionResult.getResult());
                    return true;
                }

                return false;
            })
            .orElse(false);
    }

    /**
     * Takes an Fluid Container Item and tries to fill it from the given tank.
     * If the player is in creative mode, the container will not be modified on success, and no additional items
     * created.
     * If the input itemstack has a stacksize > 1 it will stow the filled container in the given inventory.
     * If the inventory does not accept it, it will be given to the player or dropped at the players feet.
     * If player is null in this case, the action will be aborted.
     *
     * @param container   The Fluid Container ItemStack to fill.
     *                    Will not be modified directly, if modifications are necessary a modified copy is returned in
     *                    the result.
     * @param fluidSource The fluid source to fill from
     * @param inventory   An inventory where any additionally created item (filled container if multiple empty are
     *                    present) are put
     * @param maxAmount   Maximum amount of fluid to take from the tank.
     * @param player      The player that gets the items the inventory can't take.
     *                    Can be null, only used if the inventory cannot take the filled stack.
     * @param doFill      true if the container should actually be filled, false if it should be simulated.
     * @return a {@link FluidActionResult} holding the result and the resulting container. The resulting container is
     *         empty on failure.
     */
    @Nonnull
    public static FluidActionResult tryFillContainerAndStow(@Nonnull ItemStack container, IFluidHandler fluidSource,
        IItemHandler inventory, int maxAmount, @Nullable EntityPlayer player, ForgeDirection side, boolean doFill) {
        if (player != null && player.capabilities.isCreativeMode) {
            FluidActionResult filledReal = tryFillContainer(container, fluidSource, maxAmount, player, side, doFill);
            if (filledReal.isSuccess()) {
                return new FluidActionResult(container); // creative mode: item does not change
            }
        } else if (container.stackSize == 1) // don't need to stow anything, just fill the container stack
        {
            FluidActionResult filledReal = tryFillContainer(container, fluidSource, maxAmount, player, side, doFill);
            if (filledReal.isSuccess()) {
                return filledReal;
            }
        } else {
            FluidActionResult filledSimulated = tryFillContainer(
                container,
                fluidSource,
                maxAmount,
                player,
                side,
                false);
            if (filledSimulated.isSuccess()) {
                ItemStack remainder = ItemHandlerHelpers
                    .insertItemStacked(inventory, filledSimulated.getResult(), true);
                if (remainder == null || player != null) {
                    FluidActionResult filledReal = tryFillContainer(
                        container,
                        fluidSource,
                        maxAmount,
                        player,
                        side,
                        doFill);
                    remainder = ItemHandlerHelpers.insertItemStacked(inventory, filledReal.getResult(), !doFill);

                    // give it to the player or drop it at their feet
                    if (remainder != null && player != null && doFill) {
                        ItemHandlerHelpers.giveItemToPlayer(player, remainder);
                    }

                    ItemStack containerCopy = container.copy();
                    ItemStackHelpers.shrink(containerCopy, 1);
                    return new FluidActionResult(containerCopy);
                }
            }
        }

        return FluidActionResult.FAILURE;
    }

    /**
     * Takes an Fluid Container Item, tries to empty it into the fluid handler, and stows it in the given inventory.
     * If the player is in creative mode, the container will not be modified on success, and no additional items
     * created.
     * If the input itemstack has a stacksize > 1 it will stow the emptied container in the given inventory.
     * If the inventory does not accept the emptied container, it will be given to the player or dropped at the players
     * feet.
     * If player is null in this case, the action will be aborted.
     *
     * @param container        The filled Fluid Container Itemstack to empty.
     *                         Will not be modified directly, if modifications are necessary a modified copy is returned
     *                         in the result.
     * @param fluidDestination The fluid destination to fill from the fluid container.
     * @param inventory        An inventory where any additionally created item (filled container if multiple empty are
     *                         present) are put
     * @param maxAmount        Maximum amount of fluid to take from the tank.
     * @param player           The player that gets the items the inventory can't take. Can be null, only used if the
     *                         inventory cannot take the filled stack.
     * @param doDrain          true if the container should actually be drained, false if it should be simulated.
     * @return a {@link FluidActionResult} holding the result and the resulting container. The resulting container is
     *         empty on failure.
     */
    @Nonnull
    public static FluidActionResult tryEmptyContainerAndStow(@Nonnull ItemStack container,
        IFluidHandler fluidDestination, IItemHandler inventory, int maxAmount, @Nullable EntityPlayer player,
        ForgeDirection side, boolean doDrain) {
        if (player != null && player.capabilities.isCreativeMode) {
            FluidActionResult emptiedReal = tryEmptyContainer(
                container,
                fluidDestination,
                maxAmount,
                player,
                side,
                doDrain);
            if (emptiedReal.isSuccess()) {
                return new FluidActionResult(container); // creative mode: item does not change
            }
        } else if (container.stackSize == 1) // don't need to stow anything, just fill and edit the container stack
        {
            FluidActionResult emptiedReal = tryEmptyContainer(
                container,
                fluidDestination,
                maxAmount,
                player,
                side,
                doDrain);
            if (emptiedReal.isSuccess()) {
                return emptiedReal;
            }
        } else {
            FluidActionResult emptiedSimulated = tryEmptyContainer(
                container,
                fluidDestination,
                maxAmount,
                player,
                side,
                false);
            if (emptiedSimulated.isSuccess()) {
                ItemStack remainder = ItemHandlerHelpers
                    .insertItemStacked(inventory, emptiedSimulated.getResult(), true);
                if (remainder == null || player != null) {
                    FluidActionResult emptiedReal = tryEmptyContainer(
                        container,
                        fluidDestination,
                        maxAmount,
                        player,
                        side,
                        doDrain);
                    remainder = ItemHandlerHelpers.insertItemStacked(inventory, emptiedReal.getResult(), !doDrain);

                    // give it to the player or drop it at their feet
                    if (remainder != null && player != null && doDrain) {
                        ItemHandlerHelpers.giveItemToPlayer(player, remainder);
                    }

                    ItemStack containerCopy = container.copy();
                    ItemStackHelpers.shrink(containerCopy, 1);
                    return new FluidActionResult(containerCopy);
                }
            }
        }

        return FluidActionResult.FAILURE;
    }

    /**
     * Takes a filled container and tries to empty it into the given tank.
     *
     * @param container        The filled container. Will not be modified.
     *                         Separate handling must be done to reduce the stack size, stow containers, etc, on
     *                         success.
     *                         See
     *                         {@link #tryEmptyContainerAndStow(ItemStack, IFluidHandler, IItemHandler, int, EntityPlayer, ForgeDirection, boolean)}.
     * @param fluidDestination The fluid handler to be filled by the container.
     * @param maxAmount        The largest amount of fluid that should be transferred.
     * @param player           Player for making the bucket drained sound. Pass null for no noise.
     * @param side             The side of the fluid destination to interact with.
     * @param doDrain          true if the container should actually be drained, false if it should be simulated.
     * @return a {@link FluidActionResult} holding the empty container if the fluid handler was filled.
     */
    @Nonnull
    public static FluidActionResult tryEmptyContainer(@Nonnull ItemStack container, IFluidHandler fluidDestination,
        int maxAmount, @Nullable EntityPlayer player, ForgeDirection side, boolean doDrain) {
        ItemStack containerCopy = ItemHandlerHelpers.copyStackWithSize(container, 1); // do not modify the input
        if (containerCopy == null) return FluidActionResult.FAILURE;

        return getFluidHandler(containerCopy).map(containerFluidHandler -> {
            if (doDrain) {
                FluidStack transfer = tryFluidTransfer(fluidDestination, containerFluidHandler, maxAmount, side, true);
                if (transfer != null) {
                    if (player != null && player.worldObj != null) {
                        String soundName = transfer.getFluid()
                            .getName()
                            .contains("lava") ? "liquid.lavapop" : "liquid.swim";
                        player.worldObj.playSoundAtEntity(player, soundName, 1.0F, 1.0F);
                    }
                    ItemStack resultContainer = containerFluidHandler.getContainer();
                    return new FluidActionResult(resultContainer);
                }
            } else {
                FluidStack simulatedTransfer = tryFluidTransfer(
                    fluidDestination,
                    containerFluidHandler,
                    maxAmount,
                    side,
                    false);
                if (simulatedTransfer != null) {
                    containerFluidHandler.drain(simulatedTransfer, true);
                    ItemStack resultContainer = containerFluidHandler.getContainer();
                    return new FluidActionResult(resultContainer);
                }
            }
            return FluidActionResult.FAILURE;
        })
            .orElse(FluidActionResult.FAILURE);
    }

    /**
     * Fill a container from the given fluidSource.
     *
     * @param container   The container to be filled. Will not be modified.
     *                    Separate handling must be done to reduce the stack size, stow containers, etc, on success.
     * @param fluidSource The fluid handler to be drained.
     * @param maxAmount   The largest amount of fluid that should be transferred.
     * @param player      The player to make the filling noise. Pass null for no noise.
     * @param doFill      true if the container should actually be filled, false if it should be simulated.
     * @return a {@link FluidActionResult} holding the filled container if successful.
     */
    @Nonnull
    public static FluidActionResult tryFillContainer(@Nonnull ItemStack container, IFluidHandler fluidSource,
        int maxAmount, @Nullable EntityPlayer player, ForgeDirection side, boolean doFill) {
        ItemStack containerCopy = ItemHandlerHelpers.copyStackWithSize(container, 1); // do not modify the input

        if (containerCopy == null) return FluidActionResult.FAILURE;
        return getFluidHandler(containerCopy).map(containerFluidHandler -> {
            FluidStack simulatedTransfer = tryFluidTransfer(containerFluidHandler, fluidSource, maxAmount, side, false);

            if (simulatedTransfer != null) {
                if (doFill) {
                    tryFluidTransfer(containerFluidHandler, fluidSource, maxAmount, side, true);

                    if (player != null && player.worldObj != null) {
                        String soundName = simulatedTransfer.getFluid()
                            .getName()
                            .contains("lava") ? "liquid.lavapop" : "liquid.swim";
                        player.worldObj.playSoundAtEntity(player, soundName, 1.0F, 1.0F);
                    }
                } else {
                    containerFluidHandler.fill(simulatedTransfer, true);
                }

                ItemStack resultContainer = containerFluidHandler.getContainer();
                return new FluidActionResult(resultContainer);
            }

            return FluidActionResult.FAILURE;
        })
            .orElse(FluidActionResult.FAILURE);
    }

    /**
     * Fill a destination fluid handler from a source fluid handler with a max amount.
     * To specify a fluid to transfer instead of max amount, use
     * {@link #tryFluidTransfer(IFluidHandler, IFluidHandler, FluidStack, ForgeDirection, boolean)}
     * To transfer as much as possible, use {@link Integer#MAX_VALUE} for maxAmount.
     *
     * @param fluidDestination The fluid handler to be filled.
     * @param fluidSource      The fluid handler to be drained.
     * @param maxAmount        The largest amount of fluid that should be transferred.
     * @param doTransfer       True if the transfer should actually be done, false if it should be simulated.
     * @return the fluidStack that was transferred from the source to the destination. null on failure.
     */
    @Nullable
    public static FluidStack tryFluidTransfer(IFluidHandler fluidDestination, IFluidHandler fluidSource, int maxAmount,
        ForgeDirection side, boolean doTransfer) {
        FluidStack drainable = fluidSource.drain(side, maxAmount, false);
        if (drainable != null && drainable.amount > 0) {
            return tryFluidTransfer_Internal(fluidDestination, fluidSource, drainable, side, doTransfer);
        }
        return null;
    }

    /**
     * Fill a destination fluid handler from a source fluid handler using a specific fluid.
     * To specify a max amount to transfer instead of specific fluid, use
     *
     * @param fluidDestination The fluid handler to be filled.
     * @param fluidSource      The fluid handler to be drained.
     * @param resource         The fluid that should be transferred. Amount represents the maximum amount to transfer.
     * @param doTransfer       True if the transfer should actually be done, false if it should be simulated.
     * @return the fluidStack that was transferred from the source to the destination. null on failure.
     */
    @Nullable
    public static FluidStack tryFluidTransfer(IFluidHandler fluidDestination, IFluidHandler fluidSource,
        FluidStack resource, ForgeDirection side, boolean doTransfer) {
        FluidStack drainable = fluidSource.drain(side, resource, false);
        if (drainable != null && drainable.amount > 0 && resource.isFluidEqual(drainable)) {
            return tryFluidTransfer_Internal(fluidDestination, fluidSource, drainable, side, doTransfer);
        }
        return null;
    }

    /**
     * Internal method for filling a destination fluid handler from a source fluid handler using a specific fluid.
     * Assumes that "drainable" can be drained from "fluidSource".
     */
    @Nullable
    private static FluidStack tryFluidTransfer_Internal(IFluidHandler fluidDestination, IFluidHandler fluidSource,
        FluidStack drainable, ForgeDirection side, boolean doTransfer) {
        int fillableAmount = fluidDestination.fill(side, drainable, false);
        if (fillableAmount > 0) {
            if (doTransfer) {
                FluidStack drained = fluidSource.drain(side, fillableAmount, true);
                if (drained != null) {
                    drained.amount = fluidDestination.fill(side, drained, true);
                    return drained;
                }
            } else {
                drainable.amount = fillableAmount;
                return drainable;
            }
        }
        return null;
    }

    public static LazyOptional<IFluidHandlerItem> getFluidHandler(@NotNull ItemStack stack) {
        return CapabilityHelpers.getCapability(stack, CapabilityFluidHandler.FLUID_HANDLER_ITEM);
    }

    @Nullable
    public static FluidStack getFluidContained(@NotNull ItemStack container) {
        container = ItemHandlerHelpers.copyStackWithSize(container, 1);
        if (container == null) return null;
        LazyOptional<IFluidHandlerItem> cap = getFluidHandler(container);
        if (cap.isPresent()) {
            IFluidHandlerItem handler = cap.getOrNull();
            if (handler != null) {
                return handler.drain(Integer.MAX_VALUE, false);
            }
        }

        return null;
    }

    public static LazyOptional<IFluidHandler> getFluidHandler(Object object, ForgeDirection side) {
        return object instanceof TileEntity tile ? getFluidHandler(tile, side) : LazyOptional.empty();
    }

    public static LazyOptional<IFluidHandler> getFluidHandler(TileEntity tile, ForgeDirection side) {
        return CapabilityHelpers.getCapability(tile, CapabilityFluidHandler.FLUID_HANDLER, side);
    }

    public static LazyOptional<IFluidHandler> getFluidHandler(World world, BlockPos pos, ForgeDirection side) {
        Block block = pos.getBlock(world);
        if (block != null && block.hasTileEntity(pos.getBlockMetadata(world))) {
            return TileHelpers.getTileEntity(world, pos, TileEntity.class)
                .map(tile -> getFluidHandler(tile, side))
                .orElse(LazyOptional.empty());
        }
        if (block instanceof IFluidBlock) {
            return LazyOptional.of(() -> new FluidBlockWrapper((IFluidBlock) block, world, pos));
        } else if (block instanceof BlockLiquid) {
            return LazyOptional.of(() -> new BlockLiquidWrapper((BlockLiquid) block, world, pos));
        }

        return LazyOptional.empty();
    }

    public static LazyOptional<IFluidHandler> getFluidHandler(World world, int x, int y, int z, ForgeDirection side) {
        return getFluidHandler(world, new BlockPos(x, y, z), side);
    }

    /**
     * Attempts to pick up a fluid in the world and put it in an empty container item.
     *
     * @param emptyContainer The empty container to fill.
     *                       Will not be modified directly, if modifications are necessary a modified copy is returned
     *                       in the result.
     * @param playerIn       The player filling the container. Optional.
     * @param worldIn        The world the fluid is in.
     * @param pos            The position of the fluid in the world.
     * @param side           The side of the fluid that is being drained.
     * @return a {@link FluidActionResult} holding the result and the resulting container.
     */
    @Nonnull
    public static FluidActionResult tryPickUpFluid(ItemStack emptyContainer, @Nullable EntityPlayer playerIn,
        World worldIn, BlockPos pos, ForgeDirection side) {
        if (emptyContainer == null || worldIn == null || pos == null) {
            return FluidActionResult.FAILURE;
        }

        Block block = pos.getBlock(worldIn);

        if (block instanceof IFluidBlock || block instanceof BlockLiquid) {
            return getFluidHandler(worldIn, pos, side)
                .map(handler -> tryFillContainer(emptyContainer, handler, Integer.MAX_VALUE, playerIn, side, true))
                .orElse(FluidActionResult.FAILURE);
        }
        return FluidActionResult.FAILURE;
    }

    /**
     * @param player    Player who places the fluid. May be null for blocks like dispensers.
     * @param world     World to place the fluid in
     * @param pos       The position in the world to place the fluid block
     * @param container The fluid container holding the fluidStack to place
     * @param resource  The fluidStack to place
     * @return the container's ItemStack with the remaining amount of fluid if the placement was successful, null
     *         otherwise
     */
    @Nonnull
    public static FluidActionResult tryPlaceFluid(@Nullable EntityPlayer player, World world, BlockPos pos,
        @Nonnull ItemStack container, FluidStack resource, ForgeDirection side) {
        ItemStack containerCopy = ItemHandlerHelpers.copyStackWithSize(container, 1);
        if (containerCopy == null) return FluidActionResult.FAILURE;
        return getFluidHandler(containerCopy).map(handler -> {
            if (!tryPlaceFluid(player, world, pos, handler, resource, side)) return FluidActionResult.FAILURE;
            return new FluidActionResult(handler.getContainer());
        })
            .orElse(FluidActionResult.FAILURE);
    }

    /**
     * Tries to place a fluid resource into the world as a block and drains the fluidSource.
     * Makes a fluid emptying or vaporization sound when successful.
     * Honors the amount of fluid contained by the used container.
     * Checks if water-like fluids should vaporize like in the nether.
     *
     * @param player      Player who places the fluid. May be null for blocks like dispensers.
     * @param world       World to place the fluid in
     * @param pos         The position in the world to place the fluid block
     * @param fluidSource The fluid source holding the fluidStack to place
     * @param resource    The fluidStack to place.
     * @return true if the placement was successful, false otherwise
     */
    public static boolean tryPlaceFluid(@Nullable EntityPlayer player, World world, BlockPos pos,
        IFluidHandler fluidSource, FluidStack resource, ForgeDirection side) {
        if (world == null || resource == null || pos == null) {
            return false;
        }

        Fluid fluid = resource.getFluid();
        if (fluid == null || fluid.getBlock() == null) {
            return false;
        }

        if (fluidSource.drain(side, resource, false) == null) {
            return false;
        }

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        Block destBlock = world.getBlock(x, y, z);
        Material destMaterial = destBlock.getMaterial();
        boolean isDestNonSolid = !destMaterial.isSolid();
        boolean isDestReplaceable = destBlock.isReplaceable(world, x, y, z);

        if (!world.isAirBlock(x, y, z) && !isDestNonSolid && !isDestReplaceable) {
            return false;
        }

        if (world.provider.isHellWorld && destMaterial == Material.water) {
            FluidStack result = fluidSource.drain(side, resource, true);
            if (result != null) {
                world.playSoundEffect(
                    (double) x + 0.5D,
                    (double) y + 0.5D,
                    (double) z + 0.5D,
                    "random.fizz",
                    0.5F,
                    2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
                for (int l = 0; l < 8; ++l) {
                    world.spawnParticle(
                        "largesmoke",
                        (double) x + Math.random(),
                        (double) y + Math.random(),
                        (double) z + Math.random(),
                        0.0D,
                        0.0D,
                        0.0D);
                }
                return true;
            }
        } else {
            IFluidHandler handler = getFluidBlockHandler(fluid, world, pos);
            FluidStack result = tryFluidTransfer(handler, fluidSource, resource, side, true);

            if (result != null) {
                String soundName = fluid.getName()
                    .contains("lava") ? "liquid.lavapop" : "liquid.swim";

                if (player != null) {
                    world.playSoundAtEntity(player, soundName, 1.0F, 1.0F);
                } else {
                    world.playSoundEffect(
                        (double) x + 0.5D,
                        (double) y + 0.5D,
                        (double) z + 0.5D,
                        soundName,
                        1.0F,
                        1.0F);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Internal method for getting a fluid block handler for placing a fluid.
     */
    private static IFluidHandler getFluidBlockHandler(Fluid fluid, World world, BlockPos pos) {
        Block block = fluid.getBlock();
        if (block instanceof IFluidBlock) {
            return new FluidBlockWrapper((IFluidBlock) block, world, pos);
        } else if (block instanceof BlockLiquid) {
            return new BlockLiquidWrapper((BlockLiquid) block, world, pos);
        } else {
            return new BlockWrapper(block, world, pos);
        }
    }

    /**
     * Destroys a block when a fluid is placed in the same position.
     * Modeled after {@link ItemBucket#tryPlaceContainedLiquid(World, int, int, int)}
     *
     * This is a helper method for implementing fluid placement.
     *
     * @param world the world that the fluid will be placed in
     * @param pos   the location that the fluid will be placed
     */
    public static void destroyBlockOnFluidPlacement(World world, BlockPos pos) {
        if (!world.isRemote) {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();

            Block destBlock = world.getBlock(x, y, z);
            int meta = world.getBlockMetadata(x, y, z);
            Material destMaterial = destBlock.getMaterial();

            boolean isDestNonSolid = !destMaterial.isSolid();
            boolean isDestReplaceable = destBlock.isReplaceable(world, x, y, z);

            if ((isDestNonSolid || isDestReplaceable) && !destMaterial.isLiquid()) {
                destBlock.dropBlockAsItem(world, x, y, z, meta, 0);
                world.setBlockToAir(x, y, z);
            }
        }
    }

    /**
     * @param fluidStack contents used to fill the bucket.
     *                   FluidStack is used instead of Fluid to preserve fluid NBT, the amount is ignored.
     * @return a filled vanilla bucket or filled universal/modded bucket.
     *         Returns null if none of the enabled buckets can hold the fluid.
     */
    @Nullable
    public static ItemStack getFilledBucket(FluidStack fluidStack) {
        Fluid fluid = fluidStack.getFluid();
        if (fluid == null) {
            return null;
        }

        if (fluidStack.tag == null) {
            if (fluid == FluidRegistry.WATER) {
                return new ItemStack(Items.water_bucket);
            } else if (fluid == FluidRegistry.LAVA) {
                return new ItemStack(Items.lava_bucket);
            } else if (fluid.getName()
                .equals("milk")) {
                    return new ItemStack(Items.milk_bucket);
                }
        }

        ItemStack emptyBucket = new ItemStack(Items.bucket);
        FluidStack bucketVolumeStack = new FluidStack(fluidStack, FluidContainerRegistry.BUCKET_VOLUME);
        return FluidContainerRegistry.fillFluidContainer(bucketVolumeStack, emptyBucket);
    }

    /**
     * Picks up fluid fills a container with it.
     */
    public static FluidActionResult fillContainer(World world, BlockPos pos, ItemStack stackIn, ForgeDirection facing) {
        return tryPickUpFluid(stackIn, null, world, pos, facing);
    }

    /**
     * Drains a filled container and places the fluid.
     * RETURN new item stack that has been drained after placing in world if it works null otherwise
     */
    public static ItemStack dumpContainer(World world, BlockPos pos, ItemStack stackIn, ForgeDirection facing) {
        ItemStack dispensedStack = stackIn.copy();
        return getFluidHandler(dispensedStack).map(handler -> {
            FluidStack fluidStack = handler.drain(BUCKET_VOLUME, false);
            if (fluidStack != null// && fluidStack.amount >= Fluid.BUCKET_VOLUME
            ) {
                FluidActionResult placementResult = tryPlaceFluid(
                    null,
                    world,
                    pos,
                    dispensedStack,
                    fluidStack.copy(),
                    facing);
                if (placementResult.isSuccess()) {
                    return placementResult.result;
                }
            }
            return stackIn;
        })
            .orElse(stackIn);
    }

    public static ItemStack drainOneBucket(ItemStack d) {
        return getFluidHandler(d).map(handler -> {
            handler.drain(BUCKET_VOLUME, true);
            return handler.getContainer();
        })
            .orElse(d);
    }

    public static boolean stackHasFluidHandler(ItemStack stackIn) {
        return getFluidHandler(stackIn).isPresent();
    }

    public static boolean hasFluidHandler(TileEntity tile, ForgeDirection side) {
        return tile != null && CapabilityHelpers.getCapability(tile, CapabilityFluidHandler.FLUID_HANDLER, side)
            .isPresent();
    }

    public static boolean isEmptyOfFluid(ItemStack returnMe) {
        FluidStack fs = getFluidContained(returnMe);
        return fs == null || fs.amount == 0;
    }

    public static Fluid getFluidType(ItemStack returnMe) {
        FluidStack f = getFluidContained(returnMe);
        return (f == null) ? null : f.getFluid();
    }

    public static boolean tryFillTankFromPosition(World world, BlockPos posSide, ForgeDirection sideOpp,
        IFluidTank tankTo, final int amount) {
        return tryFillTankFromPosition(world, posSide, sideOpp, tankTo, amount, false, null);
    }

    public static boolean isStackInvalid(FluidStack stackToTest, boolean isWhitelist, List<FluidStack> filterList) {
        if (filterList == null) {
            return true;
        }
        boolean hasMatch = false;
        for (FluidStack filt : filterList) {
            if (stackToTest.getFluid() == filt.getFluid()) {
                hasMatch = true;
                break;
            }
        }
        if (hasMatch) {
            // fluid matches something in my list . so whitelist means ok
            return isWhitelist;
        }
        // here is the opposite: i did NOT match the list
        return !isWhitelist;
    }

    /**
     * Look for a fluid handler with gien position and direction try to extract from that pos and fill the tank
     *
     */
    public static boolean tryFillTankFromPosition(World world, BlockPos posSide, ForgeDirection sideOpp,
        IFluidTank tankTo, final int amount, boolean isWhitelist, List<FluidStack> allowedToMove) {
        try {
            return getFluidHandler(world, posSide, sideOpp).map(handler -> {
                // its not my facing dir
                // SO: pull fluid from that into myself
                FluidStack wasDrained = handler.drain(sideOpp, amount, false);
                if (wasDrained == null) {
                    return false;
                }
                if (!isStackInvalid(wasDrained, isWhitelist, allowedToMove)) {
                    return false;
                }
                int filled = tankTo.fill(wasDrained, false);
                if (wasDrained.amount > 0 && filled > 0) {
                    int realAmt = Math.min(filled, wasDrained.amount);
                    wasDrained = handler.drain(sideOpp, realAmt, true);
                    if (wasDrained == null) {
                        return false;
                    }
                    return tankTo.fill(wasDrained, true) > 0;
                }
                return false;
            })
                .orElse(false);
        } catch (Exception e) {
            OKCore.okLog(Level.ERROR, "External fluid block had an issue when we tried to drain", e);
            return false;
        }
    }

    public static boolean tryFillPositionFromTank(World world, BlockPos posSide, ForgeDirection sideOpp,
        IFluidTank tankFrom, int amount) {
        try {
            return getFluidHandler(world, posSide, sideOpp).map(handler -> {
                // its not my facing dir
                // SO: pull fluid from that into myself
                FluidStack wasDrained = tankFrom.drain(amount, false);
                if (wasDrained == null) {
                    return false;
                }
                int filled = handler.fill(sideOpp, wasDrained, false);
                if (wasDrained.amount > 0 && filled > 0) {
                    int realAmt = Math.min(filled, wasDrained.amount);
                    wasDrained = tankFrom.drain(realAmt, true);
                    if (wasDrained == null) {
                        return false;
                    }
                    return handler.fill(sideOpp, wasDrained, true) > 0;
                }
                return false;
            })
                .orElse(false);
        } catch (Exception e) {
            OKCore.okLog(Level.ERROR, "A fluid tank had an issue when we tried to fill", e);
            return false;
        }
    }
}
