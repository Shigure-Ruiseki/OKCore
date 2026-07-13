package ruiseki.okcore.helper;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.item.handler.IItemHandler;
import ruiseki.okcore.item.handler.IItemHandlerModifiable;

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
     * @param handler inventory to clear
     */
    public static void clearInventory(@Nullable IItemHandler handler) {
        if (handler == null) return;
        for (int i = 0; i < handler.getSlots(); i++) {
            handler.extractItem(i, Integer.MAX_VALUE, false);
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
     * @param handler   The inventory.
     * @param itemStack The item stack to read/write.
     * @param tagName   The tag name to read from.
     */
    public static void validateNBTStorage(IItemHandler handler, ItemStack itemStack, String tagName) {
        NBTTagCompound tag = itemStack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            itemStack.setTagCompound(tag);
        }
        if (!tag.hasKey(tagName)) {
            tag.setTag(tagName, new NBTTagList());
        }
        readFromNBT(handler, tag, tagName);
    }

    /**
     * Read an inventory from NBT.
     *
     * @param handler The inventory.
     * @param data    The tag to read from.
     * @param tagName The tag name to read from.
     */
    public static void readFromNBT(IItemHandler handler, NBTTagCompound data, String tagName) {
        NBTTagList nbttaglist = data.getTagList(tagName, MinecraftHelpers.NBTTag_Types.NBTTagCompound.getId());
        clearInventory(handler);

        for (int j = 0; j < nbttaglist.tagCount(); j++) {
            NBTTagCompound slot = nbttaglist.getCompoundTagAt(j);
            int index;
            if (slot.hasKey("index")) {
                index = slot.getInteger("index");
            } else {
                index = slot.getByte("Slot");
            }
            if (index >= 0 && index < handler.getSlots()) {
                if (handler instanceof IItemHandlerModifiable modifiable)
                    modifiable.setStackInSlot(index, ItemStack.loadItemStackFromNBT(slot));
            }
        }
    }

    /**
     * Write the given inventory to NBT.
     *
     * @param handler The inventory.
     * @param data    The tag to write to.
     * @param tagName The tag name to write into.
     */
    public static void writeToNBT(IItemHandler handler, NBTTagCompound data, String tagName) {
        NBTTagList slots = new NBTTagList();
        for (byte index = 0; index < handler.getSlots(); ++index) {
            ItemStack itemStack = handler.getStackInSlot(index);
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
     * Drop ALL ItemStacks from the inventory into the world
     */
    public static void dropItems(World world, IItemHandler handler, BlockPos pos) {
        dropItems(world, handler, pos, 0);
    }

    /**
     * Drop ItemStacks from a specific starting slot to the end of the inventory
     */
    public static void dropItems(World world, IItemHandler handler, BlockPos pos, int startingSlot) {
        if (handler == null) return;
        dropItems(world, handler, pos, startingSlot, handler.getSlots());
    }

    /**
     * Core Method: Drop ItemStacks from a specific slot range
     */
    public static void dropItems(World world, IItemHandler handler, BlockPos pos, int startingSlot, int maxSlot) {
        if (handler == null) return;

        int endSlot = Math.min(maxSlot, handler.getSlots());
        int startSlot = Math.max(0, startingSlot);

        for (int i = startSlot; i < endSlot; i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack != null && stack.stackSize > 0) {
                dropItems(world, stack.copy(), pos);
                handler.extractItem(i, stack.stackSize, false);
            }
        }
    }

    public static ItemStack insertStack(IItemHandler handler, ItemStack stack, boolean simulate) {
        if (handler == null || stack == null || stack.stackSize <= 0) {
            return stack;
        }

        ItemStack remainder = stack.copy();

        for (int i = 0; i < handler.getSlots(); i++) {
            remainder = handler.insertItem(i, remainder, simulate);

            if (remainder == null || remainder.stackSize <= 0) {
                return null;
            }
        }

        return remainder;
    }

    public static ItemStack insertIntoTile(TileEntity tile, ForgeDirection side, ItemStack stack, boolean simulate) {
        if (tile == null || stack == null) return stack;
        return CapabilityHelpers.getCapability(tile, CapabilityItemHandler.ITEM_HANDLER, side)
            .map(handler -> insertStack(handler, stack, simulate))
            .orElse(stack);
    }
}
