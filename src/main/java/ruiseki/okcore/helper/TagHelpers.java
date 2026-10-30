package ruiseki.okcore.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import lombok.NonNull;
import ruiseki.okcore.datastructure.BlockStack;
import ruiseki.okcore.datastructure.IUniqueIndex;
import ruiseki.okcore.tag.Registries;
import ruiseki.okcore.tag.TagKey;
import ruiseki.okcore.tag.TagManager;

public class TagHelpers {

    private static TagManager getManager() {
        return TagManager.getManager();
    }

    public static List<ItemStack> toItemStacks(TagKey<Item> tagKey) {
        if (tagKey == null) return Collections.emptyList();
        List<ItemStack> result = new ArrayList<>();
        for (long packed : getManager().getPackedMembers(tagKey)) {
            Item item = getItemFromLong(packed);
            if (item == null) continue;
            int meta = getMetaFromLong(packed);
            result.add(new ItemStack(item, 1, meta == OreDictionary.WILDCARD_VALUE ? 0 : meta));
        }
        return result;
    }

    public static List<BlockStack> toBlockStacks(TagKey<Block> tagKey) {
        if (tagKey == null) return Collections.emptyList();
        List<BlockStack> result = new ArrayList<>();
        for (long packed : getManager().getPackedMembers(tagKey)) {
            Block block = getBlockFromLong(packed);
            if (block == null) continue;
            int meta = getMetaFromLong(packed);
            result.add(new BlockStack(block, meta == OreDictionary.WILDCARD_VALUE ? 0 : meta));
        }
        return result;
    }

    public static Set<TagKey<Item>> toTags(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return Collections.emptySet();
        return getManager().getTags(Registries.ITEM, pack(stack));
    }

    public static Set<TagKey<Item>> getTags(Item item) {
        if (item == null) return Collections.emptySet();
        return getManager().getTags(Registries.ITEM, pack(item, OreDictionary.WILDCARD_VALUE));
    }

    public static Set<TagKey<Item>> getTags(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return Collections.emptySet();
        return getManager().getTags(Registries.ITEM, pack(stack));
    }

    public static Set<TagKey<Block>> getTags(BlockStack stack) {
        if (stack == null || stack.getBlock() == null) return Collections.emptySet();
        return getManager().getTags(Registries.BLOCK, pack(stack.getBlock(), stack.getMeta()));
    }

    public static Set<TagKey<Block>> getTags(Block block) {
        if (block == null) return Collections.emptySet();
        return getManager().getTags(Registries.BLOCK, pack(block, OreDictionary.WILDCARD_VALUE));
    }

    public static Set<TagKey<Entity>> getTags(Entity entity) {
        if (entity == null) return Collections.emptySet();
        String entityName = EntityList.getEntityString(entity);
        if (entityName == null) return Collections.emptySet();
        return getManager()
            .getTags(Registries.ENTITY_TYPE, new ResourceLocation(entityName), OreDictionary.WILDCARD_VALUE);
    }

    public static Set<TagKey<Fluid>> getTags(Fluid fluid) {
        if (fluid == null) return Collections.emptySet();
        return getManager()
            .getTags(Registries.FLUID, new ResourceLocation(fluid.getName()), OreDictionary.WILDCARD_VALUE);
    }

    public static Set<TagKey<Fluid>> getTags(FluidStack stack) {
        if (stack == null || stack.getFluid() == null) return Collections.emptySet();
        return getManager().getTags(
            Registries.FLUID,
            new ResourceLocation(
                stack.getFluid()
                    .getName()),
            OreDictionary.WILDCARD_VALUE);
    }

    public static boolean hasTag(ItemStack stack, TagKey<Item> tagKey) {
        if (stack == null || stack.getItem() == null || tagKey == null) return false;
        return getManager().hasTag(Registries.ITEM, pack(stack), tagKey);
    }

    public static boolean hasTag(Item item, TagKey<Item> tagKey) {
        if (item == null || tagKey == null) return false;
        return getManager().hasTag(Registries.ITEM, pack(item, OreDictionary.WILDCARD_VALUE), tagKey);
    }

    public static boolean hasTag(BlockStack stack, TagKey<Block> tagKey) {
        if (stack == null || stack.getBlock() == null || tagKey == null) return false;
        return getManager().hasTag(Registries.BLOCK, pack(stack.getBlock(), stack.getMeta()), tagKey);
    }

    public static boolean hasTag(Block block, TagKey<Block> tagKey) {
        if (block == null || tagKey == null) return false;
        return getManager().hasTag(Registries.BLOCK, pack(block, OreDictionary.WILDCARD_VALUE), tagKey);
    }

    public static boolean hasTag(Entity entity, TagKey<Entity> tagKey) {
        if (entity == null || tagKey == null) return false;
        String entityName = EntityList.getEntityString(entity);
        if (entityName == null) return false;
        return getManager()
            .hasTag(Registries.ENTITY_TYPE, new ResourceLocation(entityName), OreDictionary.WILDCARD_VALUE, tagKey);
    }

    public static boolean hasTag(Fluid fluid, TagKey<Fluid> tagKey) {
        if (fluid == null || tagKey == null) return false;
        return getManager()
            .hasTag(Registries.FLUID, new ResourceLocation(fluid.getName()), OreDictionary.WILDCARD_VALUE, tagKey);
    }

    public static boolean hasTag(FluidStack stack, TagKey<Fluid> tagKey) {
        if (stack == null || stack.getFluid() == null || tagKey == null) return false;
        return getManager().hasTag(
            Registries.FLUID,
            new ResourceLocation(
                stack.getFluid()
                    .getName()),
            OreDictionary.WILDCARD_VALUE,
            tagKey);
    }

    public static long pack(Block block, int meta) {
        return pack((IUniqueIndex) block, meta);
    }

    public static long pack(Item item, int meta) {
        return pack((IUniqueIndex) item, meta);
    }

    public static long pack(@NonNull BlockStack stack) {
        return pack(stack.getBlock(), stack.getMeta());
    }

    public static long pack(@NonNull ItemStack stack) {
        return pack((IUniqueIndex) stack.getItem(), stack.getItemDamage());
    }

    private static long pack(IUniqueIndex obj, int meta) {
        return pack(obj.getUniqueID(), meta);
    }

    public static long pack(int obj, int meta) {
        return (((long) obj) & 0xFFFFFFFFL) | (long) meta << 32;
    }

    public static boolean matchesSoft(Block block, int meta, long compareTo) {
        return matchesSoft((IUniqueIndex) block, meta, compareTo);
    }

    public static boolean matches(Block block, int meta, long compareTo) {
        return matches((IUniqueIndex) block, meta, compareTo);
    }

    public static boolean matchesSoft(Item item, int meta, long compareTo) {
        return matchesSoft((IUniqueIndex) item, meta, compareTo);
    }

    public static boolean matches(Item item, int meta, long compareTo) {
        return matches((IUniqueIndex) item, meta, compareTo);
    }

    public static boolean matches(ItemStack stack, long compareTo) {
        return matches((IUniqueIndex) stack.getItem(), stack.getItemDamage(), compareTo);
    }

    public static boolean matches(Block block, long compareTo) {
        return matches(block, OreDictionary.WILDCARD_VALUE, compareTo);
    }

    public static boolean matches(Item item, long compareTo) {
        return matches(item, OreDictionary.WILDCARD_VALUE, compareTo);
    }

    private static boolean matchesSoft(IUniqueIndex obj, int meta, long compareTo) {
        return matchesSoft(pack(obj.getUniqueID(), meta), compareTo);
    }

    public static boolean matchesSoft(long compare1, long compare2) {
        if (getIDFromLong(compare1) == getIDFromLong(compare2)) {
            int meta1 = getMetaFromLong(compare1);
            int meta2 = getMetaFromLong(compare2);
            return meta1 == meta2 || meta1 == OreDictionary.WILDCARD_VALUE || meta2 == OreDictionary.WILDCARD_VALUE;
        }
        return false;
    }

    private static boolean matches(IUniqueIndex obj, int meta, long compareTo) {
        return pack(obj, meta) == compareTo;
    }

    private static boolean matchesObj(IUniqueIndex obj, long compareTo) {
        return obj.getUniqueID() == (int) compareTo;
    }

    public static boolean matchesMeta(int meta, long compareTo) {
        return meta == getMetaFromLong(compareTo);
    }

    public static int getMetaFromLong(long packed) {
        return (int) (packed >> 32);
    }

    public static int getIDFromLong(long packed) {
        return (int) packed;
    }

    @Nullable
    public static Block getBlockFromLong(long packed) {
        int index = getIDFromLong(packed);
        if (index < 0 || index >= BLOCK_INDICES.size()) {
            return null;
        }
        return BLOCK_INDICES.get(index);
    }

    @Nullable
    public static Item getItemFromLong(long packed) {
        int index = getIDFromLong(packed);
        if (index < 0 || index >= ITEM_INDICES.size()) {
            return null;
        }
        return ITEM_INDICES.get(index);
    }

    private static final List<Item> ITEM_INDICES = new ArrayList<>();
    private static final List<Block> BLOCK_INDICES = new ArrayList<>();

    @ApiStatus.Internal
    public static void registerItem(int id, Item item) {
        if (id != ITEM_INDICES.size()) {
            throw new IllegalStateException("Invalid item ID: " + id);
        }
        ITEM_INDICES.add(item);
    }

    @ApiStatus.Internal
    public static void registerBlock(int id, Block block) {
        if (id != BLOCK_INDICES.size()) {
            throw new IllegalStateException("Invalid block ID: " + id);
        }
        BLOCK_INDICES.add(block);
    }

}
