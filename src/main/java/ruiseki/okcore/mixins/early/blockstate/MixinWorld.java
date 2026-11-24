package ruiseki.okcore.mixins.early.blockstate;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import ruiseki.okcore.block.state.BlockStateStorage;
import ruiseki.okcore.block.state.IBlockState;
import ruiseki.okcore.block.state.IBlockStateAccess;
import ruiseki.okcore.block.state.IBlockStateIBA;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.BlockStateHelpers;
import ruiseki.okcore.helper.EBSHelpers;

@Mixin(World.class)
public class MixinWorld implements IBlockStateAccess, IBlockStateIBA {

    @Override
    public IBlockState okcore$getBlockState(BlockPos pos) {
        return BlockStateHelpers.getBlockState((IBlockAccess) this, pos);
    }

    @Override
    public @Nullable BlockStateStorage okcore$getBlockStateStorage(BlockPos pos) {
        return BlockStateStorage.getStorage(EBSHelpers.getEBS((World) (Object) this, pos), false);
    }
}
