package ruiseki.okcore.inventory;

import java.util.Objects;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.gtnhlib.concurrent.ThreadsafeCache;

import ruiseki.okcore.helper.ItemStackHelpers;

public final class ItemStackKey {

    private static final ThreadsafeCache<ItemStack, ItemStackKey> CACHE = new ThreadsafeCache<>(
        2048,
        key -> createInternal((ItemStack) key),
        false);

    private final long packedItemMeta;
    private final int nbtSignature;
    private final @Nullable NBTTagCompound nbt;

    private String cachedDisplayName;
    private int[] cachedOreIds;
    private String cachedModId;

    private ItemStackKey(long packedItemMeta, int nbtSignature, @Nullable NBTTagCompound nbt) {
        this.packedItemMeta = packedItemMeta;
        this.nbtSignature = nbtSignature;
        this.nbt = nbt;
    }

    public static @Nullable ItemStackKey of(@Nullable ItemStack stack) {
        if (stack == null || stack.stackSize <= 0 || stack.getItem() == null) return null;
        return CACHE.get(stack);
    }

    private static ItemStackKey createInternal(ItemStack stack) {
        int itemId = Item.getIdFromItem(stack.getItem());
        int meta = stack.isItemStackDamageable() ? 0 : ItemStackHelpers.getStackMeta(stack);
        long packedItemMeta = packItemMeta(itemId, meta);

        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            return new ItemStackKey(packedItemMeta, 0, null);
        }

        NBTTagCompound tagCopy = (NBTTagCompound) tag.copy();
        return new ItemStackKey(packedItemMeta, tagCopy.hashCode(), tagCopy);
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
        int result = Long.hashCode(packedItemMeta);
        result = 31 * result + nbtSignature;
        return result;
    }

    @Override
    public String toString() {
        return "InventoryKey{itemId=" + getItemId() + ", meta=" + getMeta() + ", nbtSignature=" + nbtSignature + "}";
    }
}
