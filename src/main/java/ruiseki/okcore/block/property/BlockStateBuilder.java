package ruiseki.okcore.block.property;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;
import com.gtnewhorizon.gtnhlib.blockstate.registry.BlockPropertyRegistry;

import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.BlockStack;

public class BlockStateBuilder {

    private final Block block;
    private int meta = 0;
    private final Map<BlockProperty<?>, Object> propertyValues = new LinkedHashMap<>();

    public BlockStateBuilder() {
        this.block = null;
    }

    public BlockStateBuilder(Block block) {
        this.block = block;
    }

    public BlockStateBuilder(Block block, int meta) {
        this.block = block;
        this.meta = meta;
    }

    public BlockStateBuilder(BlockStack stack) {
        this.block = stack.getBlock();
        this.meta = stack.getMeta();
    }

    public BlockStateBuilder(IBlockAccess world, BlockPos pos) {
        this.block = pos.getBlock(world);
        this.meta = pos.getBlockMetadata(world);
    }

    public BlockStateBuilder(IBlockAccess world, int x, int y, int z) {
        this.block = world.getBlock(x, y, z);
        this.meta = world.getBlockMetadata(x, y, z);
    }

    public BlockStateBuilder(BlockState blockState) {
        this.block = blockState != null ? blockState.getBlock() : null;
        if (blockState != null) {
            blockState.forEachValue((name, state, property, value) -> {
                if (property != null) {
                    this.propertyValues.put(property, value);
                }
            });
        }
    }

    // Static Factory Methods

    public static BlockStateBuilder builder() {
        return new BlockStateBuilder();
    }

    public static BlockStateBuilder builder(Block block) {
        return new BlockStateBuilder(block);
    }

    public static BlockStateBuilder builder(BlockState blockState) {
        return new BlockStateBuilder(blockState);
    }

    // Builder Configuration
    public BlockStateBuilder withMeta(int meta) {
        this.meta = meta;
        return this;
    }

    // Chain Methods
    public <T> BlockStateBuilder withProperty(BlockProperty<T> property, T value) {
        if (property != null) {
            this.propertyValues.put(property, value);
        }
        return this;
    }

    // Build Methods
    public BlockState build() {
        if (this.block == null) {
            throw new IllegalStateException("Cannot build BlockState without a target Block instance!");
        }
        return build(this.block);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public BlockState build(Block targetBlock) {
        if (targetBlock == null) {
            throw new IllegalArgumentException("Target block cannot be null!");
        }

        BlockState state = BlockPropertyRegistry.getBlockState(targetBlock, this.meta);
        for (Map.Entry<BlockProperty<?>, Object> entry : this.propertyValues.entrySet()) {
            BlockProperty prop = entry.getKey();
            Object value = entry.getValue();

            if (prop != null) {
                if (value != null) {
                    state.setPropertyValue(prop, value);
                } else if (prop instanceof IProperty) {
                    Object defaultVal = ((IProperty<?>) prop).getDefaultValue();
                    if (defaultVal != null) {
                        state.setPropertyValue(prop, defaultVal);
                    }
                }
            }
        }

        return state;
    }

    public List<BlockProperty<?>> getProperties() {
        return new ArrayList<>(propertyValues.keySet());
    }
}
