package ruiseki.okcore.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.common.registry.GameData;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.inventory.PlayerExtendedInventoryIterator;
import ruiseki.okcore.item.IItemSharedTag;
import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.item.handler.IItemHandler;
import ruiseki.okcore.item.weighted.WeightedStackBase;

/**
 * Contains helper methods for various itemstack specific things.
 *
 * @author rubensworks
 */
public class ItemHelpers {

    private static final Random RANDOM = new Random();

    public static final ItemStack EMPTY = null;

    // 1. STACK BASIC OPERATIONS & CHECKS

    public static boolean isEmpty(@Nullable ItemStack stack) {
        return stack == null || stack.getItem() == null || stack.stackSize <= 0;
    }

    @Nullable
    public static ItemStack copy(@Nullable ItemStack stack) {
        return isEmpty(stack) ? EMPTY : stack.copy();
    }

    @Nullable
    public static ItemStack copyWithSize(@Nullable ItemStack stack, int size) {
        if (isEmpty(stack)) return EMPTY;
        ItemStack copy = stack.copy();
        copy.stackSize = size;
        return copy;
    }

    public static int getItemStackSize(@Nullable ItemStack stack) {
        return isEmpty(stack) ? 0 : stack.getMaxStackSize();
    }

    public static int getStackMeta(@Nullable ItemStack stack) {
        return isEmpty(stack) ? 0 : stack.getItemDamage();
    }

    public static int getSpace(@Nullable ItemStack stack) {
        if (isEmpty(stack)) return 64;
        return stack.getMaxStackSize() - stack.stackSize;
    }

    public static void grow(@Nullable ItemStack stack, int amount) {
        if (isEmpty(stack)) return;
        stack.stackSize += amount;
    }

    public static void shrink(@Nullable ItemStack stack, int amount) {
        grow(stack, -amount);
    }

    @Nullable
    public static ItemStack split(@Nullable ItemStack stack, int amount) {
        if (isEmpty(stack)) return EMPTY;

        int i = Math.min(amount, stack.stackSize);
        ItemStack itemstack = stack.copy();
        itemstack.stackSize = i;
        shrink(stack, i);
        return itemstack;
    }

    @Nullable
    public static ItemStack merge(@Nullable ItemStack a, @Nullable ItemStack b) {
        if (isEmpty(a)) return b;
        if (isEmpty(b)) return a;
        a.stackSize += b.stackSize;
        return a;
    }

    // 2. EQUALITY & MERGE CHECKS

    public static boolean areStacksEqual(@Nullable ItemStack stack1, @Nullable ItemStack stack2) {
        return areItemsEqual(stack1, stack2, false);
    }

    public static boolean areItemsEqual(@Nullable ItemStack stack1, @Nullable ItemStack stack2) {
        return areItemsEqual(stack1, stack2, false);
    }

    public static boolean areItemsEqual(@Nullable ItemStack stack1, @Nullable ItemStack stack2, boolean ignoreNBT) {
        if (isEmpty(stack1)) return isEmpty(stack2);
        if (isEmpty(stack2)) return false;

        return stack1.getItem() == stack2.getItem() && doMetasMatch(getStackMeta(stack1), getStackMeta(stack2))
            && (ignoreNBT || Objects.equals(stack1.getTagCompound(), stack2.getTagCompound()));
    }

    public static boolean areItemsEqualIgnoreDurability(@Nullable ItemStack stack1, @Nullable ItemStack stack2) {
        if (isEmpty(stack1)) return isEmpty(stack2);
        if (isEmpty(stack2)) return false;

        if (stack1.getItem() != stack2.getItem()) {
            return false;
        }
        return Objects.equals(stack1.getTagCompound(), stack2.getTagCompound());
    }

    public static boolean areItemStacksIdentical(@Nullable ItemStack a, @Nullable ItemStack b) {
        if (isEmpty(a)) return isEmpty(b);
        if (isEmpty(b)) return false;
        return ItemStack.areItemStacksEqual(a, b) && a.stackSize == b.stackSize;
    }

    public static boolean canStack(@Nullable ItemStack a, @Nullable ItemStack b) {
        if (isEmpty(a) || isEmpty(b)) return false;

        return a.getItem() == b.getItem() && a.getItemDamage() == b.getItemDamage()
            && ItemStack.areItemStackTagsEqual(a, b);
    }

    public static boolean areItemsMergable(@Nullable ItemStack s1, @Nullable ItemStack s2) {
        if (isEmpty(s1) || isEmpty(s2) || !s1.isStackable() || !s2.isStackable()) {
            return false;
        }
        if (!s1.isItemEqual(s2)) {
            return false;
        }
        return areItemsEqual(s1, s2);
    }

    public static boolean doMetasMatch(int meta1, int meta2) {
        if (meta1 == OreDictionary.WILDCARD_VALUE || meta2 == OreDictionary.WILDCARD_VALUE) {
            return true;
        }
        return meta1 == meta2;
    }

    public static boolean areItemStackShareTagsEqual(@Nullable ItemStack stackA, @Nullable ItemStack stackB) {
        if (isEmpty(stackA)) return isEmpty(stackB);
        if (isEmpty(stackB)) return false;

        NBTTagCompound shareTagA = stackA.getItem() instanceof IItemSharedTag sharedTagA
            ? sharedTagA.getNBTShareTag(stackA)
            : stackA.getTagCompound();
        NBTTagCompound shareTagB = stackB.getItem() instanceof IItemSharedTag sharedTagB
            ? sharedTagB.getNBTShareTag(stackB)
            : stackB.getTagCompound();

        return Objects.equals(shareTagA, shareTagB);
    }

    public static boolean areItemStacksEqualUsingNBTShareTag(@Nullable ItemStack stackA, @Nullable ItemStack stackB) {
        if (isEmpty(stackA)) return isEmpty(stackB);
        return !isEmpty(stackB) && isItemStackEqualUsingNBTShareTag(stackA, stackB);
    }

    private static boolean isItemStackEqualUsingNBTShareTag(ItemStack self, ItemStack other) {
        return self.stackSize == other.stackSize && self.getItem() == other.getItem()
            && self.getItemDamage() == other.getItemDamage()
            && areItemStackShareTagsEqual(self, other);
    }

    public static int getItemStackHashCode(@Nullable ItemStack stack) {
        if (isEmpty(stack)) {
            return 0;
        }
        int result = 1;
        result = 37 * result + stack.stackSize;
        result = 37 * result + stack.getItem()
            .hashCode();
        result = 37 * result + stack.getItemDamage();

        return result;
    }

    // 3. WORLD & PLAYER INTERACTIONS

    public static void spawnItemStack(World world, BlockPos pos, @Nullable ItemStack itemStack) {
        spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
    }

    public static void spawnItemStack(World world, double x, double y, double z, @Nullable ItemStack itemStack) {
        if (world == null || isEmpty(itemStack)) return;

        float offsetX = RANDOM.nextFloat() * 0.8F + 0.1F;
        float offsetY = RANDOM.nextFloat() * 0.8F + 0.1F;
        float offsetZ = RANDOM.nextFloat() * 0.8F + 0.1F;

        while (!isEmpty(itemStack)) {
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

    public static void spawnItemStackToPlayer(World world, BlockPos pos, @Nullable ItemStack stack,
        EntityPlayer player) {
        if (world == null || player == null || world.isRemote || isEmpty(stack)) return;

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

    public static boolean hasPlayerItem(EntityPlayer player, Item item) {
        if (player == null || item == null) return false;
        for (PlayerExtendedInventoryIterator it = new PlayerExtendedInventoryIterator(player); it.hasNext();) {
            ItemStack itemStack = it.next();
            if (!isEmpty(itemStack) && itemStack.getItem() == item) {
                return true;
            }
        }
        return false;
    }

    // 4. CAPABILITY & TILE ENTITY HANDLERS

    public static LazyOptional<IItemHandler> getItemHandler(Object object, ForgeDirection side) {
        return object instanceof TileEntity tile ? getItemHandler(tile, side) : LazyOptional.empty();
    }

    public static LazyOptional<IItemHandler> getItemHandler(TileEntity tile, ForgeDirection side) {
        return CapabilityHelpers.getCapability(tile, CapabilityItemHandler.ITEM_HANDLER, side);
    }

    public static LazyOptional<IItemHandler> getItemHandler(World world, BlockPos pos, ForgeDirection side) {
        return getItemHandler(pos.getTileEntity(world), side);
    }

    public static LazyOptional<IItemHandler> getItemHandler(World world, int x, int y, int z, ForgeDirection side) {
        return getItemHandler(world, new BlockPos(x, y, z), side);
    }

    // 5. NBT & SERIALIZATION

    public static NBTTagCompound saveAllItems(NBTTagCompound tag, List<ItemStack> list) {
        return saveAllItems(tag, list, true);
    }

    public static NBTTagCompound saveAllItems(NBTTagCompound tag, List<ItemStack> list, boolean saveEmpty) {
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < list.size(); ++i) {
            ItemStack itemstack = list.get(i);

            if (!isEmpty(itemstack)) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte) i);
                itemstack.writeToNBT(nbttagcompound);
                nbttaglist.appendTag(nbttagcompound);
            }
        }

        if (nbttaglist.tagCount() > 0 || saveEmpty) {
            tag.setTag("Items", nbttaglist);
        }

        return tag;
    }

    public static void loadAllItems(NBTTagCompound tag, List<ItemStack> list) {
        NBTTagList nbttaglist = tag.getTagList("Items", 10);

        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;

            if (j >= 0 && j < list.size()) {
                list.set(j, ItemStack.loadItemStackFromNBT(nbttagcompound));
            }
        }
    }

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

    // 6. SUB-ITEMS & ORE DICTIONARY VARIANTS

    public static List<ItemStack> getSubItems(@Nullable ItemStack itemStack) {
        List<ItemStack> subItems = new ArrayList<>();
        if (isEmpty(itemStack)) return subItems;
        itemStack.getItem()
            .getSubItems(itemStack.getItem(), null, subItems);
        return subItems;
    }

    public static List<ItemStack> getVariants(@Nullable ItemStack itemStack) {
        return getSubItemsIfWildcardMeta(itemStack);
    }

    public static List<ItemStack> getSubItemsIfWildcardMeta(@Nullable ItemStack itemStack) {
        if (isEmpty(itemStack)) return Collections.emptyList();

        if (itemStack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
            return getSubItems(itemStack);
        } else {
            return Collections.singletonList(itemStack);
        }
    }

    public static boolean isValidCreativeTab(Item item, @Nullable CreativeTabs creativeTab) {
        if (item == null) return false;
        for (CreativeTabs itemTab : item.getCreativeTabs()) {
            if (itemTab == creativeTab) {
                return true;
            }
        }
        return creativeTab == null;
    }

    public static void copyWSList(List<WeightedStackBase> dest, List<WeightedStackBase> src) {
        if (src == null) return;
        if (dest == null) {
            dest = new ArrayList<>();
        }

        for (WeightedStackBase weightedStackBase : src) {
            dest.add(weightedStackBase.copy());
        }
    }
}
