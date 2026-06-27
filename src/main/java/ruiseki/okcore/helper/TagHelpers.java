package ruiseki.okcore.helper;

import java.util.Collections;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.okcore.datastructure.BlockStack;
import ruiseki.okcore.tag.TagKey;
import ruiseki.okcore.tag.TagManager;
import ruiseki.okcore.tag.entry.BlockTagEntry;
import ruiseki.okcore.tag.entry.EntityTagEntry;
import ruiseki.okcore.tag.entry.FluidTagEntry;
import ruiseki.okcore.tag.entry.ItemTagEntry;

public class TagHelpers {

    private static TagManager getManager() {
        return TagManager.getManager();
    }

    public static Set<TagKey<ItemStack>> getTags(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return Collections.emptySet();
        return getManager().getTags(new ItemTagEntry(stack));
    }

    public static Set<TagKey<ItemStack>> getTags(Item item) {
        if (item == null) return Collections.emptySet();
        return getManager().getTags(new ItemTagEntry(item));
    }

    public static Set<TagKey<BlockStack>> getTags(BlockStack stack) {
        if (stack == null || stack.getBlock() == null) return Collections.emptySet();
        return getManager().getTags(new BlockTagEntry(stack));
    }

    public static Set<TagKey<BlockStack>> getTags(Block block) {
        if (block == null) return Collections.emptySet();
        return getManager().getTags(new BlockTagEntry(block));
    }

    public static Set<TagKey<Entity>> getTags(Entity entity) {
        if (entity == null) return Collections.emptySet();
        return getManager().getTags(new EntityTagEntry(entity));
    }

    public static Set<TagKey<Fluid>> getTags(Fluid fluid) {
        if (fluid == null) return Collections.emptySet();
        return getManager().getTags(new FluidTagEntry(fluid));
    }

    public static Set<TagKey<Fluid>> getTags(FluidStack stack) {
        if (stack == null || stack.getFluid() == null) return Collections.emptySet();
        return getManager().getTags(new FluidTagEntry(stack));
    }
}
