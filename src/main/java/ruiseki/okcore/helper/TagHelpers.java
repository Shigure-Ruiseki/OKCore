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

import cpw.mods.fml.common.registry.GameData;
import ruiseki.okcore.datastructure.BlockStack;
import ruiseki.okcore.tag.Registries;
import ruiseki.okcore.tag.TagEntry;
import ruiseki.okcore.tag.TagKey;
import ruiseki.okcore.tag.TagManager;

public class TagHelpers {

    private static TagManager getManager() {
        return TagManager.getManager();
    }

    public static List<ItemStack> toItemStacks(TagKey<Item> tagKey) {
        if (tagKey == null) return Collections.emptyList();

        Set<TagEntry> entries = getManager().getEntries(tagKey);
        if (entries == null || entries.isEmpty()) return Collections.emptyList();

        List<ItemStack> resultStacks = new ArrayList<>();
        for (TagEntry entry : entries) {
            Item item = GameData.getItemRegistry()
                .getObject(
                    entry.id()
                        .toString());
            if (item == null) continue;

            int targetMeta = (entry.meta() == TagEntry.WILDCARD) ? 0 : entry.meta();
            try {
                resultStacks.add(new ItemStack(item, 1, targetMeta));
            } catch (Throwable ignored) {}
        }
        return resultStacks;
    }

    public static List<BlockStack> toBlockStacks(TagKey<Block> tagKey) {
        if (tagKey == null) return Collections.emptyList();

        Set<TagEntry> entries = getManager().getEntries(tagKey);
        if (entries == null || entries.isEmpty()) return Collections.emptyList();

        List<BlockStack> resultStacks = new ArrayList<>();
        for (TagEntry entry : entries) {
            Block item = GameData.getBlockRegistry()
                .getObject(
                    entry.id()
                        .toString());
            if (item == null) continue;

            int targetMeta = (entry.meta() == TagEntry.WILDCARD) ? 0 : entry.meta();
            try {
                resultStacks.add(new BlockStack(item, targetMeta));
            } catch (Throwable ignored) {}
        }
        return resultStacks;
    }

    public static Set<TagKey<Item>> toTags(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return Collections.emptySet();
        ResourceLocation id = Helpers.getLocation(stack.getItem());
        return getManager().getTags(Registries.ITEM, id, stack.getItemDamage());
    }

    public static Set<TagKey<Item>> getTags(Item item) {
        if (item == null) return Collections.emptySet();
        ResourceLocation id = Helpers.getLocation(item);
        return getManager().getTags(Registries.ITEM, id, TagEntry.WILDCARD);
    }

    public static Set<TagKey<Item>> getTags(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return Collections.emptySet();
        ResourceLocation id = Helpers.getLocation(stack);
        return getManager().getTags(Registries.ITEM, id, stack.getItemDamage());
    }

    public static Set<TagKey<Block>> getTags(BlockStack stack) {
        if (stack == null || stack.getBlock() == null) return Collections.emptySet();
        ResourceLocation id = Helpers.getLocation(stack.getBlock());
        return getManager().getTags(Registries.BLOCK, id, stack.getMeta());
    }

    public static Set<TagKey<Block>> getTags(Block block) {
        if (block == null) return Collections.emptySet();
        ResourceLocation id = Helpers.getLocation(block);
        return getManager().getTags(Registries.BLOCK, id, TagEntry.WILDCARD);
    }

    public static Set<TagKey<Entity>> getTags(Entity entity) {
        if (entity == null) return Collections.emptySet();
        String entityName = EntityList.getEntityString(entity);
        if (entityName == null) return Collections.emptySet();
        return getManager().getTags(Registries.ENTITY_TYPE, new ResourceLocation(entityName), TagEntry.WILDCARD);
    }

    public static Set<TagKey<Fluid>> getTags(Fluid fluid) {
        if (fluid == null) return Collections.emptySet();
        return getManager().getTags(Registries.FLUID, new ResourceLocation(fluid.getName()), TagEntry.WILDCARD);
    }

    public static Set<TagKey<Fluid>> getTags(FluidStack stack) {
        if (stack == null || stack.getFluid() == null) return Collections.emptySet();
        return getManager().getTags(
            Registries.FLUID,
            new ResourceLocation(
                stack.getFluid()
                    .getName()),
            TagEntry.WILDCARD);
    }

    public static boolean hasTag(ItemStack stack, TagKey<Item> tagKey) {
        if (stack == null || stack.getItem() == null || tagKey == null) return false;
        ResourceLocation id = Helpers.getLocation(stack.getItem());
        return getManager().hasTag(Registries.ITEM, id, stack.getItemDamage(), tagKey);
    }

    public static boolean hasTag(Item item, TagKey<Item> tagKey) {
        if (item == null || tagKey == null) return false;
        ResourceLocation id = Helpers.getLocation(item);
        return getManager().hasTag(Registries.ITEM, id, TagEntry.WILDCARD, tagKey);
    }

    public static boolean hasTag(BlockStack stack, TagKey<Block> tagKey) {
        if (stack == null || stack.getBlock() == null || tagKey == null) return false;
        ResourceLocation id = Helpers.getLocation(stack.getBlock());
        return getManager().hasTag(Registries.BLOCK, id, stack.getMeta(), tagKey);
    }

    public static boolean hasTag(Block block, TagKey<Block> tagKey) {
        if (block == null || tagKey == null) return false;
        ResourceLocation id = Helpers.getLocation(block);
        return getManager().hasTag(Registries.BLOCK, id, TagEntry.WILDCARD, tagKey);
    }

    public static boolean hasTag(Entity entity, TagKey<Entity> tagKey) {
        if (entity == null || tagKey == null) return false;
        String entityName = EntityList.getEntityString(entity);
        if (entityName == null) return false;
        return getManager().hasTag(Registries.ENTITY_TYPE, new ResourceLocation(entityName), TagEntry.WILDCARD, tagKey);
    }

    public static boolean hasTag(Fluid fluid, TagKey<Fluid> tagKey) {
        if (fluid == null || tagKey == null) return false;
        return getManager().hasTag(Registries.FLUID, new ResourceLocation(fluid.getName()), TagEntry.WILDCARD, tagKey);
    }

    public static boolean hasTag(FluidStack stack, TagKey<Fluid> tagKey) {
        if (stack == null || stack.getFluid() == null || tagKey == null) return false;
        return getManager().hasTag(
            Registries.FLUID,
            new ResourceLocation(
                stack.getFluid()
                    .getName()),
            TagEntry.WILDCARD,
            tagKey);
    }
}
