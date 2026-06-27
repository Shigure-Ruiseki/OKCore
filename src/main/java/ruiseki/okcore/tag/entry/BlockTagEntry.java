package ruiseki.okcore.tag.entry;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.datastructure.BlockStack;
import ruiseki.okcore.tag.Registries;
import ruiseki.okcore.tag.ResourceKey;

@TagData
public class BlockTagEntry extends TagEntry<BlockStack> {

    public BlockTagEntry() {
        super(null, 0);
    }

    public BlockTagEntry(Block block) {
        super((block != null) ? new ResourceLocation(Block.blockRegistry.getNameForObject(block)) : null, 0);
    }

    public BlockTagEntry(BlockStack stack) {
        super(
            (stack != null && stack.getBlock() != null)
                ? new ResourceLocation(Block.blockRegistry.getNameForObject(stack.getBlock()))
                : null,
            (stack != null) ? stack.getMeta() : 0);
    }

    public BlockTagEntry(ResourceLocation id, int meta) {
        super(id, meta);
    }

    @Override
    public Class<BlockStack> getType() {
        return BlockStack.class;
    }

    @Override
    public String getKey() {
        return "block";
    }

    @Override
    public ResourceKey<?> getRegistryKey() {
        return Registries.BLOCK;
    }

    @Override
    public TagEntry<BlockStack> create(ResourceLocation id, int meta) {
        return new BlockTagEntry(id, meta);
    }

    @Override
    public BlockStack to() {
        if (this.id == null) return null;
        Block block = (Block) Block.blockRegistry.getObject(this.id.toString());
        if (block == null) return null;
        int finalMeta = (this.meta == WILDCARD) ? 0 : this.meta;
        return new BlockStack(block, finalMeta);
    }
}
