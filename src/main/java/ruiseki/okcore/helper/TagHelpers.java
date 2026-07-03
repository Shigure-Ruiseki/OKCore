package ruiseki.okcore.helper;

import java.util.Collections;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.okcore.datastructure.BlockStack;
import ruiseki.okcore.tag.TagKey;
import ruiseki.okcore.tag.TagManager;
import ruiseki.okcore.tag.entry.TagEntry;

public class TagHelpers {

    private static TagManager getManager() {
        return TagManager.getManager();
    }

    public static Set<TagKey<ItemStack>> getTags(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return Collections.emptySet();
        ResourceLocation id = Helpers.getLocation(stack.getItem());
        return getManager().getTags(ItemStack.class, id, stack.getItemDamage());
    }

    public static Set<TagKey<ItemStack>> getTags(Item item) {
        if (item == null) return Collections.emptySet();
        ResourceLocation id = Helpers.getLocation(item);
        return getManager().getTags(ItemStack.class, id, TagEntry.WILDCARD);
    }

    public static Set<TagKey<BlockStack>> getTags(BlockStack stack) {
        if (stack == null || stack.getBlock() == null) return Collections.emptySet();
        ResourceLocation id = Helpers.getLocation(stack.getBlock());
        return getManager().getTags(BlockStack.class, id, stack.getMeta());
    }

    public static Set<TagKey<BlockStack>> getTags(Block block) {
        if (block == null) return Collections.emptySet();
        ResourceLocation id = Helpers.getLocation(block);
        return getManager().getTags(BlockStack.class, id, TagEntry.WILDCARD);
    }

    public static Set<TagKey<Entity>> getTags(Entity entity) {
        if (entity == null) return Collections.emptySet();
        String entityName = EntityList.getEntityString(entity);
        if (entityName == null) return Collections.emptySet();
        return getManager().getTags(Entity.class, new ResourceLocation(entityName), 0);
    }

    public static Set<TagKey<Fluid>> getTags(Fluid fluid) {
        if (fluid == null) return Collections.emptySet();
        return getManager().getTags(Fluid.class, new ResourceLocation(fluid.getName()), TagEntry.WILDCARD);
    }

    public static Set<TagKey<Fluid>> getTags(FluidStack stack) {
        if (stack == null || stack.getFluid() == null) return Collections.emptySet();
        return getManager().getTags(
            Fluid.class,
            new ResourceLocation(
                stack.getFluid()
                    .getName()),
            0);
    }
}
