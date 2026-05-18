package ruiseki.okcore.json.block;

import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.registry.GameData;
import ruiseki.okcore.datastructure.BlockStack;
import ruiseki.okcore.json.AbstractJsonMaterial;

public class BlockMaterial extends AbstractJsonMaterial {

    private Block block;
    private String name;
    private int meta = 0;

    @Override
    public void read(JsonObject json) {
        this.name = getString(json, "block", null);
        this.block = null;

        this.meta = getInt(json, "meta", 0);
        captureUnknownProperties(json, "block", "meta");
    }

    public Block getBlock() {
        if (this.block == null && this.name != null) {
            this.block = GameData.getBlockRegistry()
                .getObject(this.name);
        }
        return block;
    }

    public int getMeta() {
        return meta;
    }

    public void fromWorld(World world, int x, int y, int z) {
        if (world == null) {
            this.block = null;
            this.name = "minecraft:air";
            this.meta = 0;
            return;
        }

        this.block = world.getBlock(x, y, z);
        this.name = GameData.getBlockRegistry()
            .getNameForObject(this.block);
        this.meta = world.getBlockMetadata(x, y, z);
        this.unknownProperties.clear();
    }

    public BlockStack toStack() {
        Block currentBlock = getBlock();
        if (currentBlock == null) return BlockStack.empty();
        return new BlockStack(currentBlock, this.meta);
    }

    public void fromStack(BlockStack stack) {
        if (stack == null) return;
        this.block = stack.getBlock();
        this.name = GameData.getBlockRegistry()
            .getNameForObject(this.block);
        this.meta = stack.getMeta();
    }

    @Override
    public void write(JsonObject json) {
        Block currentBlock = getBlock();
        if (currentBlock != null) {
            json.addProperty(
                "block",
                GameData.getBlockRegistry()
                    .getNameForObject(currentBlock));
        } else if (this.name != null) {
            json.addProperty("block", this.name);
        }

        json.addProperty("meta", this.meta);
        writeUnknownProperties(json);
    }

    @Override
    public boolean validate() {
        if (this.name == null) {
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
        return Objects.equals(getBlock(), that.getBlock());
    }

    @Override
    public int hashCode() {
        int result = getBlock() != null ? getBlock().hashCode() : 0;
        result = 31 * result + meta;
        return result;
    }

    @Override
    public String toString() {
        return "BlockMaterial[Block=" + (getBlock() != null ? block : name) + ":" + meta + "]";
    }
}
