package ruiseki.okcore.inventory;

import java.util.Objects;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.helper.ItemStackHelpers;

public final class ItemStackKey {

    private final long packedItemMeta;
    private final int nbtSignature;
    private final @Nullable NBTTagCompound nbt;

    private String cachedDisplayName;
    private int[] cachedOreIds;
    private String cachedModId;
    private int cachedHashCode;;

    private ItemStackKey(long packedItemMeta, @Nullable NBTTagCompound nbt) {
        this.packedItemMeta = packedItemMeta;
        this.nbt = nbt;
        this.nbtSignature = nbt != null ? calculateNBTHash(nbt) : 0;
    }

    public static @Nullable ItemStackKey of(@Nullable ItemStack stack) {
        if (stack == null || stack.stackSize <= 0 || stack.getItem() == null) return null;

        int itemId = Item.getIdFromItem(stack.getItem());
        int meta = stack.isItemStackDamageable() ? 0 : ItemStackHelpers.getStackMeta(stack);
        long packedItemMeta = packItemMeta(itemId, meta);

        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            return new ItemStackKey(packedItemMeta, null);
        }

        return new ItemStackKey(packedItemMeta, (NBTTagCompound) tag.copy());
    }

    public int getItemId() {
        return unpackItemId(packedItemMeta);
    }

    public int getMeta() {
        return unpackMeta(packedItemMeta);
    }

    public Item getItem() {
        return Item.getItemById(getItemId());
    }

    public @Nullable NBTTagCompound getTagCopy() {
        return nbt == null ? null : (NBTTagCompound) nbt.copy();
    }

    public ItemStack toStack(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }

        Item item = Item.getItemById(getItemId());
        if (item == null) {
            throw new IllegalStateException("No item found for id " + getItemId());
        }

        ItemStack stack = new ItemStack(item, amount, getMeta());
        if (nbt != null) {
            stack.setTagCompound((NBTTagCompound) nbt.copy());
        }
        return stack;
    }

    private static long packItemMeta(int itemId, int meta) {
        return ((long) itemId << 32) | (meta & 0xFFFF_FFFFL);
    }

    private static int unpackItemId(long packed) {
        return (int) (packed >>> 32);
    }

    private static int unpackMeta(long packed) {
        return (int) packed;
    }

    public String getDisplayName() {
        if (cachedDisplayName == null) {
            cachedDisplayName = toStack(1).getDisplayName()
                .toLowerCase();
        }
        return cachedDisplayName;
    }

    public int[] getOreIds() {
        if (cachedOreIds == null) {
            cachedOreIds = OreDictionary.getOreIDs(toStack(1));
        }
        return cachedOreIds;
    }

    public String getModId() {
        if (cachedModId == null) {
            Item item = getItem();
            String name = Item.itemRegistry.getNameForObject(item);
            if (name != null) {
                int idx = name.indexOf(':');
                cachedModId = (idx >= 0 ? name.substring(0, idx) : "minecraft").toLowerCase();
            } else {
                cachedModId = "unknown";
            }
        }
        return cachedModId;
    }

    private static int calculateNBTHash(NBTTagCompound tag) {
        return tag.toString()
            .hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ItemStackKey other)) return false;
        if (packedItemMeta != other.packedItemMeta) return false;
        if (nbtSignature != other.nbtSignature) return false;
        return Objects.equals(nbt, other.nbt);
    }

    @Override
    public int hashCode() {
        if (cachedHashCode == 0) {
            int result = Long.hashCode(packedItemMeta);
            result = 31 * result + nbtSignature;
            cachedHashCode = result;
        }
        return cachedHashCode;
    }

    @Override
    public String toString() {
        return "ItemStackKey{itemId=" + unpackItemId(
            packedItemMeta) + ", meta=" + getMeta() + ", nbtSignature=" + nbtSignature + "}";
    }
}
