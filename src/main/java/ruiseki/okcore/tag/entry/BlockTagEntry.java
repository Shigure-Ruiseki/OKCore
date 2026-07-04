package ruiseki.okcore.tag.entry;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import cpw.mods.fml.common.registry.GameData;
import ruiseki.okcore.datastructure.BlockStack;

public class BlockTagEntry extends TagEntry<BlockStack> {

    public BlockTagEntry(@NotNull ResourceLocation id, int meta) {
        super(id, meta);
    }

    @Override
    public Class<BlockStack> getType() {
        return BlockStack.class;
    }

    @Override
    public BlockStack get() {
        Block block = (Block) GameData.getBlockRegistry()
            .getObject(this.id);
        if (block == null) return null;
        int blockMeta = this.meta == WILDCARD ? 0 : this.meta;
        return new BlockStack(block, blockMeta);
    }

    public static class Serializer implements ITagEntrySerializer<BlockStack, BlockTagEntry> {

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public String getKey() {
            return "block";
        }

        @Override
        public BlockTagEntry read(ResourceLocation id, int meta) {
            return new BlockTagEntry(id, meta);
        }
    }
}
