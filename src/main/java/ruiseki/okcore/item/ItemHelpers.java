package ruiseki.okcore.item;

import java.util.Objects;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import org.intellij.lang.annotations.MagicConstant;

import com.gtnewhorizon.gtnhlib.hash.Fnv1a32;

import it.unimi.dsi.fastutil.Hash;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.datastructure.IImmutableItemMeta;
import ruiseki.okcore.helper.ItemStackHelpers;
import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.item.capability.IItemSink;
import ruiseki.okcore.item.capability.IItemSource;

public class ItemHelpers {

    private static int counter = 0;
    public static final int WRAP_INVENTORIES = 0b1 << counter++;
    public static final int FOR_INSERTS = 0b1 << counter++;
    public static final int FOR_EXTRACTS = 0b1 << counter++;
    public static final int DEFAULT = WRAP_INVENTORIES | FOR_INSERTS | FOR_EXTRACTS;

    public static IItemSource getItemSource(Object obj, ForgeDirection side) {
        return getItemSource(obj, side, DEFAULT);
    }

    public static IItemSource getItemSource(Object obj, ForgeDirection side,
        @MagicConstant(flagsFromClass = ItemStackHelpers.class) int usage) {
        if ((usage & FOR_EXTRACTS) == 0) return null;

        if (obj instanceof IItemSource source) {
            return source;
        }

        if (obj instanceof ICapabilityProvider provider) {
            IItemSource source = provider.getCapability(CapabilityItemHandler.ITEM_SOURCE_CAPABILITY, side)
                .getOrNull();

            if (source != null) return source;
        }

        return null;
    }

    public static IItemSink getItemSink(Object obj, ForgeDirection side) {
        return getItemSink(obj, side, DEFAULT);
    }

    public static IItemSink getItemSink(Object obj, ForgeDirection side,
        @MagicConstant(flagsFromClass = ItemStackHelpers.class) int usage) {
        if ((usage & FOR_INSERTS) == 0) return null;

        if (obj instanceof IItemSink sink) {
            return sink;
        }

        if (obj instanceof ICapabilityProvider provider) {
            IItemSink sink = provider.getCapability(CapabilityItemHandler.ITEM_SINK_CAPABILITY)
                .getOrNull();

            if (sink != null) return sink;
        }

        return null;
    }

    private static Item getGenericItem(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Item item) return item;
        if (obj instanceof ItemStack stack) return stack.getItem();
        // Includes ImmutableItemStack and ItemId
        if (obj instanceof IImmutableItemMeta im) return im.getItem();

        throw new IllegalArgumentException("Cannot extract item from object: " + obj);
    }

    private static int getGenericMeta(Object obj) {
        if (obj == null) return 0;
        if (obj instanceof ItemStack stack) return ItemStackHelpers.getStackMeta(stack);
        // Includes ImmutableItemStack and ItemId
        if (obj instanceof IImmutableItemMeta im) return im.getItemMeta();

        throw new IllegalArgumentException("Cannot extract item metadata from object: " + obj);
    }

    private static NBTTagCompound getGenericTag(Object obj) {
        if (obj == null) return null;
        if (obj instanceof ItemStack stack) return stack.getTagCompound();
        // Includes ItemId
        if (obj instanceof IImmutableItemStack stack) return stack.getTag();

        throw new IllegalArgumentException("Cannot extract item metadata from object: " + obj);
    }

    public static final Hash.Strategy<Object> GENERIC_ITEM_META_STRATEGY = new Hash.Strategy<>() {

        @Override
        public int hashCode(Object o) {
            int hash = Fnv1a32.initialState();

            if (o != null) {
                hash = Fnv1a32.hashStep(hash, Objects.hashCode(getGenericItem(o)));
                hash = Fnv1a32.hashStep(hash, getGenericMeta(o));
            }

            return hash;
        }

        @Override
        public boolean equals(Object a, Object b) {
            if (a == b) return true;
            if (a == null || b == null) return false;

            if (getGenericItem(a) != getGenericItem(b)) return false;
            return getGenericMeta(a) == getGenericMeta(b);
        }
    };

    public static final Hash.Strategy<Object> GENERIC_ITEM_META_NBT_STRATEGY = new Hash.Strategy<>() {

        @Override
        public int hashCode(Object o) {
            int hash = Fnv1a32.initialState();

            if (o != null) {
                hash = Fnv1a32.hashStep(hash, Objects.hashCode(getGenericItem(o)));
                hash = Fnv1a32.hashStep(hash, getGenericMeta(o));
                hash = Fnv1a32.hashStep(hash, Objects.hashCode(getGenericTag(o)));
            }

            return hash;
        }

        @Override
        public boolean equals(Object a, Object b) {
            if (a == b) return true;
            if (a == null || b == null) return false;

            if (getGenericItem(a) != getGenericItem(b)) return false;
            if (getGenericMeta(a) == getGenericMeta(b)) return false;
            return Objects.equals(getGenericTag(a), getGenericTag(b));
        }
    };
}
