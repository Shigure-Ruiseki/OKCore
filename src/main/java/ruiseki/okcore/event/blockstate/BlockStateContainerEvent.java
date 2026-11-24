package ruiseki.okcore.event.blockstate;

import net.minecraft.block.Block;

import cpw.mods.fml.common.eventhandler.Event;
import ruiseki.okcore.block.state.BlockStateContainer;

public abstract class BlockStateContainerEvent extends Event {

    private final Block block;
    private final BlockStateContainer.Builder builder;

    public BlockStateContainerEvent(Block block, BlockStateContainer.Builder builder) {
        this.block = block;
        this.builder = builder;
    }

    public Block getBlock() {
        return this.block;
    }

    public BlockStateContainer.Builder getBuilder() {
        return this.builder;
    }

    public static class Build extends BlockStateContainerEvent {

        public Build(Block block, BlockStateContainer.Builder builder) {
            super(block, builder);
        }
    }
}
