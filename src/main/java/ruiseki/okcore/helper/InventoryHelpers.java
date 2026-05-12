package ruiseki.okcore.helper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.datastructure.BlockPos;

/**
 * Contains helper methods involving {@link IInventory}S.
 *
 * @author immortaleeb
 *
 */
public class InventoryHelpers {

    /**
     * Erase a complete inventory
     *
     * @param inventory inventory to clear
     */
    public static void clearInventory(IInventory inventory) {
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            inventory.setInventorySlotContents(i, null);
        }
    }

    /**
     * Try adding a new item stack originating from the given original stack to the same original stack.
     * The original item stack should not have it's stack-size decreased yet, this method does this.
     * Otherwise it will add the new stack to another inventory slot and in the worst case drop it on the floor.
     *
     * @param player        The player.
     * @param originalStack The original item stack from which the new item stack originated.
     * @param newStackPart  The new item stack.
     */
    public static void tryReAddToStack(EntityPlayer player, @Nullable ItemStack originalStack, ItemStack newStackPart) {
        if (!player.capabilities.isCreativeMode) {
            if (originalStack != null && --originalStack.copy().stackSize == 0) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, newStackPart);
            } else {
                if (originalStack != null) {
                    --originalStack.stackSize;
                }
                if (!player.inventory.addItemStackToInventory(newStackPart)) {
                    player.dropPlayerItemWithRandomChoice(newStackPart, false);
                }
            }
        }
    }

    /**
     * Validate the NBT storage of the given inventory in the given item.
     * Should be called in constructors of inventories.
     *
     * @param inventory The inventory.
     * @param itemStack The item stack to read/write.
     * @param tagName   The tag name to read from.
     */
    public static void validateNBTStorage(IInventory inventory, ItemStack itemStack, String tagName) {
        NBTTagCompound tag = itemStack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            itemStack.setTagCompound(tag);
        }
        if (!tag.hasKey(tagName)) {
            tag.setTag(tagName, new NBTTagList());
        }
        readFromNBT(inventory, tag, tagName);
    }

    /**
     * Read an inventory from NBT.
     *
     * @param inventory The inventory.
     * @param data      The tag to read from.
     * @param tagName   The tag name to read from.
     */
    public static void readFromNBT(IInventory inventory, NBTTagCompound data, String tagName) {
        NBTTagList nbttaglist = data.getTagList(tagName, MinecraftHelpers.NBTTag_Types.NBTTagCompound.getId());

        for (int j = 0; j < inventory.getSizeInventory(); j++) {
            inventory.setInventorySlotContents(j, null);
        }

        for (int j = 0; j < nbttaglist.tagCount(); j++) {
            NBTTagCompound slot = nbttaglist.getCompoundTagAt(j);
            int index;
            if (slot.hasKey("index")) {
                index = slot.getInteger("index");
            } else {
                index = slot.getByte("Slot");
            }
            if (index >= 0 && index < inventory.getSizeInventory()) {
                inventory.setInventorySlotContents(index, ItemStack.loadItemStackFromNBT(slot));
            }
        }
    }

    /**
     * Write the given inventory to NBT.
     *
     * @param inventory The inventory.
     * @param data      The tag to write to.
     * @param tagName   The tag name to write into.
     */
    public static void writeToNBT(IInventory inventory, NBTTagCompound data, String tagName) {
        NBTTagList slots = new NBTTagList();
        for (byte index = 0; index < inventory.getSizeInventory(); ++index) {
            ItemStack itemStack = inventory.getStackInSlot(index);
            if (itemStack != null && itemStack.stackSize > 0) {
                NBTTagCompound slot = new NBTTagCompound();
                slot.setInteger("index", index);
                slots.appendTag(slot);
                itemStack.writeToNBT(slot);
            }
        }
        data.setTag(tagName, slots);
    }

    /**
     * Get the item stack from the given index in the player inventory.
     *
     * @param player    The player.
     * @param itemIndex The index of the item in the inventory.
     * @return The item stack.
     */
    public static ItemStack getItemFromIndex(EntityPlayer player, int itemIndex) {
        return player.inventory.mainInventory[itemIndex];
    }

    /**
     * Drop an ItemStack into the world
     *
     * @param world     the world
     * @param inventory inventory with ItemStacks
     * @param blockPos  The position.
     */
    public static void dropItems(World world, IInventory inventory, BlockPos blockPos) {
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack itemStack = inventory.getStackInSlot(i);
            if (itemStack != null && itemStack.stackSize > 0) dropItems(
                world,
                inventory.getStackInSlot(i)
                    .copy(),
                blockPos);
        }
    }

    /**
     * Drop an ItemStack into the world
     *
     * @param world    the world
     * @param stack    ItemStack to drop
     * @param blockPos The position.
     */
    public static void dropItems(World world, ItemStack stack, BlockPos blockPos) {
        if (stack.stackSize > 0) {
            float offsetMultiply = 0.7F;
            double offsetX = (world.rand.nextFloat() * offsetMultiply) + (1.0F - offsetMultiply) * 0.5D;
            double offsetY = (world.rand.nextFloat() * offsetMultiply) + (1.0F - offsetMultiply) * 0.5D;
            double offsetZ = (world.rand.nextFloat() * offsetMultiply) + (1.0F - offsetMultiply) * 0.5D;
            EntityItem entityitem = new EntityItem(
                world,
                blockPos.getX() + offsetX,
                blockPos.getY() + offsetY,
                blockPos.getZ() + offsetZ,
                stack);
            entityitem.delayBeforeCanPickup = 10;

            world.spawnEntityInWorld(entityitem);
        }
    }

    /**
     * Core logic for inserting an ItemStack into a specific slot.
     *
     * @param inv      Target inventory
     * @param stack    Stack to insert (will be modified)
     * @param index    Slot index
     * @param side     Access side (for sided inventories)
     * @param simulate If true, inventory won't actually be modified
     * @return Remaining stack (null if fully inserted)
     */
    private static ItemStack insertStack(IInventory inv, ItemStack stack, int index, @Nullable ForgeDirection side,
        boolean simulate) {

        if (stack == null || stack.stackSize <= 0) return null;

        if (!canInsertItemInSlot(inv, stack, index, side)) return stack;

        ItemStack existing = inv.getStackInSlot(index);
        int limit = Math.min(stack.getMaxStackSize(), inv.getInventoryStackLimit());

        // Empty slot
        if (existing == null) {
            int toMove = Math.min(stack.stackSize, limit);

            if (!simulate) {
                ItemStack newStack = stack.copy();
                newStack.stackSize = toMove;
                inv.setInventorySlotContents(index, newStack);
                inv.markDirty();
            }

            stack.stackSize -= toMove;
            return stack.stackSize <= 0 ? null : stack;
        }

        // Not mergeable
        if (!ItemStackHelpers.areStackMergable(existing, stack)) return stack;

        int max = Math.min(existing.getMaxStackSize(), limit);
        int space = max - existing.stackSize;
        if (space <= 0) return stack;

        int toMove = Math.min(stack.stackSize, space);

        if (!simulate) {
            existing.stackSize += toMove;
            inv.markDirty();
        }

        stack.stackSize -= toMove;
        return stack.stackSize <= 0 ? null : stack;
    }

    /**
     * Attempts to insert an ItemStack into all available slots of the inventory.
     *
     * Works with both IInventory and ISidedInventory.
     *
     * @param inventory Target inventory
     * @param stack     Stack to insert
     * @param side      Access side (important for sided inventories)
     * @param simulate  If true, no changes will be applied
     * @return Remaining stack (null if fully inserted)
     */
    public static ItemStack addToInventory(IInventory inventory, ItemStack stack, @Nullable ForgeDirection side,
        boolean simulate) {

        if (stack == null || stack.stackSize <= 0) return null;

        if (inventory instanceof ISidedInventory sided && side != null) {
            int[] slots = sided.getAccessibleSlotsFromSide(side.ordinal());

            for (int slot : slots) {
                stack = insertStack(inventory, stack, slot, side, simulate);
                if (stack == null) return null;
            }
        } else {
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                stack = insertStack(inventory, stack, i, side, simulate);
                if (stack == null) return null;
            }
        }

        return stack;
    }

    /**
     * Checks whether a stack can be inserted into a slot.
     */
    private static boolean canInsertItemInSlot(IInventory inventory, ItemStack stack, int index,
        @Nullable ForgeDirection side) {
        if (!inventory.isItemValidForSlot(index, stack)) return false;
        if (inventory instanceof ISidedInventory sided) {
            return side == null || sided.canInsertItem(index, stack, side.ordinal());
        }

        return true;
    }

    /**
     * Try to add the given item to the given slot.
     *
     * @param inventory The inventory.
     * @param slot      The slot to add to.
     * @param itemStack The item to try to put in the production slot.
     * @return If the item could be added or joined in the production slot.
     */
    public static ItemStack addToSlot(IInventory inventory, int slot, ItemStack itemStack) {
        return addToSlot(inventory, slot, itemStack, false);
    }

    /**
     * Try inserting a stack into a specific slot.
     *
     * @param inventory Target inventory
     * @param slot      Slot index
     * @param stack     Stack to insert
     * @param simulate  If true, no actual change is made
     * @return True if fully inserted
     */
    public static ItemStack addToSlot(IInventory inventory, int slot, ItemStack stack, boolean simulate) {
        return insertStack(inventory, stack.copy(), slot, null, simulate);
    }

    /**
     * Gets the inventory adjacent to a given position in a specific direction.
     *
     * @param world The world
     * @param pos   The origin block position
     * @param side  The direction to check
     * @return The found inventory, or null if none exists
     */
    @Nullable
    public static IInventory getInventoryAtSide(World world, BlockPos pos, ForgeDirection side) {
        BlockPos targetPos = pos.offset(side);
        if (!targetPos.isLoaded(world)) return null;
        return getInventory(TileHelpers.getSafeTile(world, targetPos, TileEntity.class));
    }

    public static IInventory getInventory(TileEntity tile) {
        if (tile instanceof IInventory inventory) {
            if (tile instanceof TileEntityChest) {
                Block block = tile.getBlockType();
                if (block instanceof BlockChest blockChest) {
                    inventory = blockChest.func_149951_m(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
                }
            }
            return inventory;
        }
        return null;
    }
}
