package ruiseki.okcore.json.block;

import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.registry.GameData;
import ruiseki.okcore.datastructure.BlockStack;
import ruiseki.okcore.json.AbstractJsonMaterial;

public class BlockMaterial extends AbstractJsonMaterial {

    private Block block;
    private int meta = 0;

    @Override
    public void read(JsonObject json) {
        String blockName = getString(json, "block", "minecraft:air");
        this.block = GameData.getBlockRegistry()
            .getObject(blockName);
        this.meta = getInt(json, "meta", 0);
        captureUnknownProperties(json, "block", "meta");
    }

    public Block getBlock() {
        return block;
    }

    public int getMeta() {
        return meta;
    }

    public void fromWorld(World world, int x, int y, int z) {
        if (world == null) {
            this.block = null;
            this.meta = 0;
            return;
        }

        this.block = world.getBlock(x, y, z);
        this.meta = world.getBlockMetadata(x, y, z);
        this.unknownProperties.clear();
    }

    public BlockStack toStack() {
        if (this.block == null || this.block == Blocks.air) return BlockStack.empty();
        return new BlockStack(this.block, this.meta);
    }

    public void fromStack(BlockStack stack) {
        this.block = stack.getBlock();
        this.meta = stack.getMeta();
    }

    @Override
    public void write(JsonObject json) {
        if (this.block != null) json.addProperty(
            "block",
            GameData.getBlockRegistry()
                .getNameForObject(block));
        json.addProperty("meta", this.meta);
        writeUnknownProperties(json);
    }

    @Override
    public boolean validate() {
        if (block == null) {
            logValidationError("BlockMaterial block cannot be empty!");
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockMaterial that = (BlockMaterial) o;

        if (meta != that.meta) return false;
        return Objects.equals(block, that.block);
    }

    @Override
    public int hashCode() {
        int result = block != null ? block.hashCode() : 0;
        result = 31 * result + meta;
        return result;
    }

    @Override
    public String toString() {
        return "BlockMaterial[Block=" + block + ":" + meta + "]";
    }
}
