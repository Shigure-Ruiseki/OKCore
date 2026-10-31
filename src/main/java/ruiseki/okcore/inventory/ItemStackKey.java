package ruiseki.okcore.inventory;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.oredict.OreDictionary;

import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import ruiseki.okcore.helper.ItemHelpers;

@EventBusSubscriber
public final class ItemStackKey {

    private static final Map<ItemStack, ItemStackKey> CACHE = new ConcurrentHashMap<>();

    private final long packedItemMeta;
    private final int nbtSignature;
    private final @Nullable NBTTagCompound nbt;

    private String cachedDisplayName = "";
    private int[] cachedOreIds;
    private String cachedModId;
    private final int cachedHashCode;

    private ItemStackKey(long packedItemMeta, @Nullable NBTTagCompound nbt) {
        this.packedItemMeta = packedItemMeta;
        this.nbt = nbt;
        this.nbtSignature = nbt != null ? calculateNBTHash(nbt) : 0;

        int result = Long.hashCode(packedItemMeta);
        result = 31 * result + nbtSignature;
        this.cachedHashCode = result;
    }

    private ItemStackKey(int itemId, int meta, @Nullable NBTTagCompound nbt) {
        this(packItemMeta(itemId, meta), nbt);
    }

    /**
     * Returns a cached key for the given stack.
     * The cache is cleared at the end of each server tick.
     */
    public static @Nullable ItemStackKey of(@Nullable ItemStack stack) {
        if (stack == null || stack.stackSize <= 0 || stack.getItem() == null) {
            return null;
        }

        return CACHE.computeIfAbsent(stack, ItemStackKey::create);
    }

    private static ItemStackKey create(ItemStack stack) {
        int itemId = Item.getIdFromItem(stack.getItem());
        int meta = stack.isItemStackDamageable() ? 0 : ItemHelpers.getStackMeta(stack);
        NBTTagCompound tag = stack.getTagCompound();
        return new ItemStackKey(itemId, meta, tag == null ? null : (NBTTagCompound) tag.copy());
    }

    /**
     * Clears cached keys at the end of each server tick.
     * Register this class on the Forge event bus.
     */
    @SubscribeEvent
    public static void clearCacheOnTickEnd(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            CACHE.clear();
        }
    }

    public static void clearCache() {
        CACHE.clear();
    }

    public static int getCacheSize() {
        return CACHE.size();
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

    public ItemStack getStack() {
        return getStack(1);
    }

    public ItemStack getStack(int amount) {
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
        if (cachedDisplayName.isEmpty()) {
            cachedDisplayName = getStack().getDisplayName()
                .toLowerCase(Locale.ROOT);
        }
        return cachedDisplayName;
    }

    public int[] getOreIds() {
        if (cachedOreIds == null) {
            cachedOreIds = OreDictionary.getOreIDs(getStack());
        }
        return cachedOreIds.clone();
    }

    public String getModId() {
        if (cachedModId == null) {
            Item item = getItem();
            String name = Item.itemRegistry.getNameForObject(item);
            if (name != null) {
                int idx = name.indexOf(':');
                cachedModId = (idx >= 0 ? name.substring(0, idx) : "minecraft").toLowerCase(Locale.ROOT);
            } else {
                cachedModId = "unknown";
            }
        }
        return cachedModId;
    }

    private static int calculateNBTHash(NBTTagCompound tag) {
        return tag.hashCode();
    }

    public void write(PacketBuffer buf) throws IOException {
        buf.writeVarIntToBuffer(getItemId());
        buf.writeVarIntToBuffer(getMeta());

        if (nbt != null) {
            buf.writeBoolean(true);
            buf.writeNBTTagCompoundToBuffer(nbt);
        } else {
            buf.writeBoolean(false);
        }
    }

    public static ItemStackKey read(PacketBuffer buf) throws IOException {
        int itemId = buf.readVarIntFromBuffer();
        int meta = buf.readVarIntFromBuffer();

        NBTTagCompound tag = null;
        if (buf.readBoolean()) {
            tag = buf.readNBTTagCompoundFromBuffer();
        }

        return new ItemStackKey(packItemMeta(itemId, meta), tag == null ? null : (NBTTagCompound) tag.copy());
    }

    public boolean matches(@Nullable ItemStack stack) {
        ItemStackKey other = of(stack);
        return equals(other);
    }

    public static boolean canItemStacksStack(@Nullable ItemStack a, @Nullable ItemStack b) {
        if (ItemHelpers.isEmpty(a) || ItemHelpers.isEmpty(b)) {
            return false;
        }

        return ItemHelpers.areItemsEqual(a, b) && Objects.equals(a.getTagCompound(), b.getTagCompound());
    }

    public boolean hashCodeNotEquals(@Nullable ItemStack otherStack) {
        return hashCode() != getHashCode(otherStack);
    }

    public static int getHashCode(@Nullable ItemStack stack) {
        if (ItemHelpers.isEmpty(stack)) {
            return 0;
        }

        int hash = stack.getItem()
            .hashCode();
        hash = 31 * hash + ItemHelpers.getStackMeta(stack);

        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null) {
            hash = 31 * hash + tag.hashCode();
        }

        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ItemStackKey other)) {
            return false;
        }

        if (packedItemMeta != other.packedItemMeta) {
            return false;
        }
        if (nbtSignature != other.nbtSignature) {
            return false;
        }

        return Objects.equals(nbt, other.nbt);
    }

    @Override
    public int hashCode() {
        return cachedHashCode;
    }

    @Override
    public String toString() {
        return "ItemStackKey{itemId=" + getItemId() + ", meta=" + getMeta() + ", nbtSignature=" + nbtSignature + "}";
    }
}
