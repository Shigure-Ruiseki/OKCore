package ruiseki.okcore.mixins.early.blockstate;

import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import ruiseki.okcore.block.state.BlockStateStorage;
import ruiseki.okcore.block.state.IBlockStateEBS;

@Mixin(ExtendedBlockStorage.class)
public class MixinExtendedBlockStorage implements IBlockStateEBS {

    @Unique
    private BlockStateStorage okcore$blockStates;

    @Override
    public BlockStateStorage okcore$getBlockStateStorage() {
        return this.okcore$blockStates;
    }

    @Override
    public void okcore$setBlockStateStorage(BlockStateStorage storage) {
        this.okcore$blockStates = storage;
    }
}
