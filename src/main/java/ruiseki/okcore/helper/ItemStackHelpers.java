package ruiseki.okcore.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.registry.GameData;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.inventory.PlayerExtendedInventoryIterator;
import ruiseki.okcore.item.IItemSharedTag;
import ruiseki.okcore.item.weighted.WeightedStackBase;

/**
 * Contains helper methods for various itemstack specific things.
 *
 * @author rubensworks
 */
public final class ItemStackHelpers {

    private static final Random RANDOM = new Random();

    /**
     * Get the tag compound from an item safely.
     * If it does not exist yet, it will create and save a new tag compound.
     *
     * @param itemStack The item to get the tag compound from.
     * @return The tag compound.
     */
    public static NBTTagCompound getSafeTagCompound(ItemStack itemStack) {
        if (!itemStack.hasTagCompound()) {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        return itemStack.getTagCompound();
    }

    /**
     * Spawn an itemstack into the world.
     *
     * @param world     The world
     * @param pos       The position
     * @param itemStack the item stack
     */
    public static void spawnItemStack(World world, BlockPos pos, ItemStack itemStack) {
        spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
    }

    /**
     * Spawn an itemstack into the world.
     *
     * @param world     The world
     * @param x         X
     * @param y         Y
     * @param z         Z
     * @param itemStack the item stack
     */
    public static void spawnItemStack(World world, double x, double y, double z, ItemStack itemStack) {
        float offsetX = RANDOM.nextFloat() * 0.8F + 0.1F;
        float offsetY = RANDOM.nextFloat() * 0.8F + 0.1F;
        float offsetZ = RANDOM.nextFloat() * 0.8F + 0.1F;

        while (itemStack.stackSize > 0) {
            int i = RANDOM.nextInt(21) + 10;

            if (i > itemStack.stackSize) {
                i = itemStack.stackSize;
            }

            itemStack.stackSize -= i;
            EntityItem entityitem = new EntityItem(
                world,
                x + (double) offsetX,
                y + (double) offsetY,
                z + (double) offsetZ,
                new ItemStack(itemStack.getItem(), i, itemStack.getItemDamage()));

            if (itemStack.hasTagCompound()) {
                entityitem.getEntityItem()
                    .setTagCompound(
                        (NBTTagCompound) itemStack.getTagCompound()
                            .copy());
            }

            float motion = 0.05F;
            entityitem.motionX = RANDOM.nextGaussian() * (double) motion;
            entityitem.motionY = RANDOM.nextGaussian() * (double) motion + 0.2D;
            entityitem.motionZ = RANDOM.nextGaussian() * (double) motion;
            world.spawnEntityInWorld(entityitem);
        }
    }

    /**
     * Spawn an entity in the direction of a player without setting a pickup delay.
     *
     * @param world  The world
     * @param pos    The position to spawn an
     * @param stack  The stack to spawn
     * @param player The player to direct the motion to
     */
    public static void spawnItemStackToPlayer(World world, BlockPos pos, ItemStack stack, EntityPlayer player) {
        if (!world.isRemote) {
            float f = 0.5F;

            double xo = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double yo = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double zo = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(
                world,
                (double) pos.getX() + xo,
                (double) pos.getY() + yo,
                (double) pos.getZ() + zo,
                stack);

            double d0 = 8.0D;
            double d1 = (player.posX - entityitem.posX) / d0;
            double d2 = (player.posY + (double) player.getEyeHeight() - entityitem.posY) / d0;
            double d3 = (player.posZ - entityitem.posZ) / d0;

            entityitem.motionX += d1;
            entityitem.motionY += d2;
            entityitem.motionZ += d3;

            entityitem.delayBeforeCanPickup = 0;
            world.spawnEntityInWorld(entityitem);
        }
    }

    /**
     * Check if the given player has at least one of the given item.
     *
     * @param player The player.
     * @param item   The item to search in the inventory.
     * @return If the player has the item.
     */
    public static boolean hasPlayerItem(EntityPlayer player, Item item) {
        for (PlayerExtendedInventoryIterator it = new PlayerExtendedInventoryIterator(player); it.hasNext();) {
            ItemStack itemStack = it.next();
            if (itemStack != null && itemStack.getItem() == item) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get a list of variants from the given stack if its damage value is the wildcard value,
     * otherwise the list will only contain the given itemstack.
     *
     * @param itemStack The itemstack
     * @return The list of variants.
     */
    public static List<ItemStack> getVariants(ItemStack itemStack) {
        List<ItemStack> output = Lists.newLinkedList();
        if (itemStack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
            itemStack.getItem()
                .getSubItems(itemStack.getItem(), null, output);
        } else {
            output.add(itemStack);
        }
        return output;
    }

    /**
     * Parse a string to an itemstack.
     * Expects the format "domain:itemname:amount:meta"
     * The domain and itemname are mandatory, the rest is optional.
     *
     * @param itemStackString The string to parse.
     * @return The itemstack.
     * @throws IllegalArgumentException If the string was incorrectly formatted.
     */
    public static ItemStack parseItemStack(String itemStackString) {
        String[] split = itemStackString.split(":");
        String itemName = split[0] + ":" + split[1];
        Item item = GameData.getItemRegistry()
            .getObject(itemName);
        if (item == null) {
            throw new IllegalArgumentException("Invalid ItemStack item: " + itemName);
        }
        int amount = 1;
        int meta = 0;
        if (split.length > 2) {
            try {
                amount = Integer.parseInt(split[2]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid ItemStack amount: " + split[2]);
            }
            if (split.length > 3) {
                try {
                    meta = Integer.parseInt(split[3]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid ItemStack meta: " + split[3]);
                }
            }
        }
        return new ItemStack(item, amount, meta);
    }

    /**
     * If the given itemstacks are completely identical, including their stack size.
     *
     * @param a The first itemstack.
     * @param b The second itemstack.
     * @return If they are completely equal.
     */
    public static boolean areItemStacksIdentical(ItemStack a, ItemStack b) {
        return ItemStack.areItemStacksEqual(a, b)
            && ((a == null && b == null) || (a != null && a.stackSize == b.stackSize));
    }

    public static <T> T getCapability(ItemStack stack, Capability<T> capability, @Nullable ForgeDirection facing) {
        if (stack == null) return null;
        try {
            ICapabilityProvider provider = (ICapabilityProvider) (Object) stack;

            return provider.getCapability(capability, facing);

        } catch (ClassCastException ignored) {
            return null;
        }
    }

    public static boolean hasCapability(ItemStack stack, Capability<?> capability, @Nullable ForgeDirection facing) {
        if (stack == null) return false;
        try {
            ICapabilityProvider provider = (ICapabilityProvider) (Object) stack;

            return provider.hasCapability(capability, facing);

        } catch (ClassCastException ignored) {
            return false;
        }
    }

    public static void copyWSList(List<WeightedStackBase> dest, List<WeightedStackBase> src) {
        if (dest == null) {
            dest = new ArrayList<>();
        }

        for (WeightedStackBase weightedStackBase : src) {
            dest.add(weightedStackBase.copy());
        }

    }

    public static int getStackMeta(ItemStack stack) {
        return stack.getItemDamage();
    }

    public static boolean isStackEmpty(ItemStack stack) {
        return stack == null || stack.getItem() == null || stack.stackSize <= 0;
    }

    public static boolean isStackInvalid(ItemStack stack) {
        return stack == null || stack.getItem() == null || stack.stackSize < 0;
    }

    public static boolean areStacksEqual(ItemStack stack1, ItemStack stack2) {
        return areStacksEqual(stack1, stack2, false);
    }

    public static boolean areStacksEqual(ItemStack stack1, ItemStack stack2, boolean ignoreNBT) {
        return stack1 != null && stack2 != null
            && stack1.getItem() == stack2.getItem()
            && doStackMetasMatch(getStackMeta(stack1), getStackMeta(stack2))
            && (ignoreNBT || Objects.equals(stack1.getTagCompound(), stack2.getTagCompound()));
    }

    public static boolean doStackMetasMatch(int meta1, int meta2) {
        if (meta1 == OreDictionary.WILDCARD_VALUE) return true;
        if (meta2 == OreDictionary.WILDCARD_VALUE) return true;

        return meta1 == meta2;
    }

    public static boolean areStackMergable(ItemStack s1, ItemStack s2) {
        if (s1 == null || s2 == null || !s1.isStackable() || !s2.isStackable()) {
            return false;
        }
        if (!s1.isItemEqual(s2)) {
            return false;
        }
        return areStacksEqual(s1, s2);
    }

    public static boolean areItemsEqualIgnoreDurability(ItemStack stack1, ItemStack stack2) {
        if (stack1 == null || stack2 == null) {
            return false;
        }
        if (stack1.getItem() != stack2.getItem()) {
            return false;
        }
        return Objects.equals(stack1.getTagCompound(), stack2.getTagCompound());
    }

    public static boolean areItemStacksEqualUsingNBTShareTag(ItemStack stackA, ItemStack stackB) {
        if (stackA == null) return stackB == null;
        else return stackB != null && isItemStackEqualUsingNBTShareTag(stackA, stackB);
    }

    private static boolean isItemStackEqualUsingNBTShareTag(ItemStack self, ItemStack other) {
        return self.stackSize == other.stackSize && self.getItem() == other.getItem()
            && self.getItemDamage() == other.getItemDamage()
            && areItemStackShareTagsEqual(self, other);
    }

    public static boolean areItemStackShareTagsEqual(ItemStack stackA, ItemStack stackB) {
        NBTTagCompound shareTagA = stackA.getItem() instanceof IItemSharedTag sharedTagA
            ? sharedTagA.getNBTShareTag(stackA)
            : stackA.getTagCompound();
        NBTTagCompound shareTagB = stackB.getItem() instanceof IItemSharedTag sharedTagB
            ? sharedTagB.getNBTShareTag(stackB)
            : stackB.getTagCompound();
        if (shareTagA == null) return shareTagB == null;
        else return shareTagB != null && shareTagA.equals(shareTagB);
    }

    public static ItemStack merge(ItemStack a, ItemStack b) {
        if (a == null) return b;
        a.stackSize += b.stackSize;
        return a;
    }

    public static void grow(ItemStack stack, int amount) {
        if (stack == null) return;
        stack.stackSize += amount;
    }

    public static void shrink(ItemStack stack, int amount) {
        if (stack == null) return;
        stack.stackSize -= amount;

        if (stack.stackSize <= 0) {
            stack.stackSize = 0;
        }
    }

    public static ItemStack split(ItemStack stack, int amount) {
        if (stack == null) return null;

        int removed = Math.min(amount, stack.stackSize);
        ItemStack result = stack.copy();
        result.stackSize = removed;

        stack.stackSize -= removed;

        if (stack.stackSize <= 0) {
            stack.stackSize = 0;
        }

        return result;
    }

    public static ItemStack copyWithSize(ItemStack stack, int size) {
        if (stack == null) return null;

        ItemStack copy = stack.copy();
        copy.stackSize = size;
        return copy;
    }

    public static boolean canStack(ItemStack a, ItemStack b) {
        if (a == null || b == null) return false;

        return a.getItem() == b.getItem() && a.getItemDamage() == b.getItemDamage()
            && ItemStack.areItemStackTagsEqual(a, b);
    }

    public static int getSpace(ItemStack stack) {
        if (stack == null) return 64;
        return stack.getMaxStackSize() - stack.stackSize;
    }
}
