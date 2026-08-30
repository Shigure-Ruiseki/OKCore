package ruiseki.okcore.helper;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.item.capability.wrapper.PlayerMainInvWrapper;
import ruiseki.okcore.item.handler.IItemHandler;

public class ItemHandlerHelpers {

    public ItemHandlerHelpers() {}

    @Nullable
    public static ItemStack insertItem(IItemHandler dest, @Nullable ItemStack stack, boolean simulate) {
        if (dest == null || ItemHelpers.isEmpty(stack)) {
            return stack;
        }

        for (int i = 0; i < dest.getSlots(); ++i) {
            stack = dest.insertItem(i, stack, simulate);
            if (ItemHelpers.isEmpty(stack)) {
                return null;
            }
        }
        return stack;
    }

    public static boolean canItemStacksStack(@Nullable ItemStack a, @Nullable ItemStack b) {
        if (ItemHelpers.isEmpty(a) || ItemHelpers.isEmpty(b)) {
            return false;
        }
        if (a.isItemEqual(b) && a.hasTagCompound() == b.hasTagCompound()) {
            return !a.hasTagCompound() || a.getTagCompound()
                .equals(b.getTagCompound());
        }
        return false;
    }

    public static boolean canItemStacksStackRelaxed(@Nullable ItemStack a, @Nullable ItemStack b) {
        if (ItemHelpers.isEmpty(a) || ItemHelpers.isEmpty(b)) {
            return false;
        }
        if (a.getItem() != b.getItem()) {
            return false;
        }
        if (!a.isStackable()) {
            return false;
        }
        if (a.getHasSubtypes() && a.getItemDamage() != b.getItemDamage()) {
            return false;
        }
        if (a.hasTagCompound() != b.hasTagCompound()) {
            return false;
        }

        return !a.hasTagCompound() || a.getTagCompound()
            .equals(b.getTagCompound());
    }

    @Nullable
    public static ItemStack copyStackWithSize(@Nullable ItemStack itemStack, int size) {
        if (ItemHelpers.isEmpty(itemStack) || size <= 0) {
            return null;
        }
        return ItemHelpers.copyWithSize(itemStack, size);
    }

    @Nullable
    public static ItemStack insertItemStacked(IItemHandler inventory, @Nullable ItemStack stack, boolean simulate) {
        if (inventory == null || ItemHelpers.isEmpty(stack)) {
            return stack;
        }

        if (!stack.isStackable()) {
            return insertItem(inventory, stack, simulate);
        }

        int sizeInventory = inventory.getSlots();

        for (int i = 0; i < sizeInventory; ++i) {
            ItemStack slot = inventory.getStackInSlot(i);
            if (canItemStacksStackRelaxed(slot, stack)) {
                stack = inventory.insertItem(i, stack, simulate);
                if (ItemHelpers.isEmpty(stack)) {
                    break;
                }
            }
        }

        if (!ItemHelpers.isEmpty(stack)) {
            for (int i = 0; i < sizeInventory; ++i) {
                if (ItemHelpers.isEmpty(inventory.getStackInSlot(i))) {
                    stack = inventory.insertItem(i, stack, simulate);
                    if (ItemHelpers.isEmpty(stack)) {
                        break;
                    }
                }
            }
        }

        return stack;
    }

    public static void giveItemToPlayer(EntityPlayer player, @Nullable ItemStack stack) {
        giveItemToPlayer(player, stack, -1);
    }

    public static void giveItemToPlayer(EntityPlayer player, @Nullable ItemStack stack, int preferredSlot) {
        if (player == null || ItemHelpers.isEmpty(stack)) {
            return;
        }

        IItemHandler inventory = new PlayerMainInvWrapper(player.inventory);
        World world = player.worldObj;
        ItemStack remainder = stack;

        if (preferredSlot >= 0 && preferredSlot < inventory.getSlots()) {
            remainder = inventory.insertItem(preferredSlot, stack, false);
        }

        if (!ItemHelpers.isEmpty(remainder)) {
            remainder = insertItemStacked(inventory, remainder, false);
        }

        if (ItemHelpers.isEmpty(remainder) || remainder.stackSize != stack.stackSize) {
            world.playSoundAtEntity(
                player,
                "random.pop",
                0.2F,
                ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }

        if (!ItemHelpers.isEmpty(remainder) && !world.isRemote) {
            EntityItem entityitem = new EntityItem(world, player.posX, player.posY + 0.5D, player.posZ, remainder);
            entityitem.delayBeforeCanPickup = 40;
            entityitem.motionX = 0.0D;
            entityitem.motionZ = 0.0D;
            world.spawnEntityInWorld(entityitem);
        }
    }

    public static int calcRedstoneFromInventory(@Nullable IItemHandler inv) {
        if (inv == null) {
            return 0;
        }

        int itemsFound = 0;
        float proportion = 0.0F;

        for (int j = 0; j < inv.getSlots(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if (!ItemHelpers.isEmpty(itemstack)) {
                proportion += (float) itemstack.stackSize
                    / (float) Math.min(inv.getSlotLimit(j), itemstack.getMaxStackSize());
                ++itemsFound;
            }
        }

        proportion /= (float) inv.getSlots();
        return MathHelper.floor_float(proportion * 14.0F) + (itemsFound > 0 ? 1 : 0);
    }
}
