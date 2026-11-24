
package ruiseki.okcore.mixins.early.blockstate;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.util.BlockSnapshot;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ruiseki.okcore.block.state.BlockStateStorage;
import ruiseki.okcore.block.state.IBlockSnapshot;
import ruiseki.okcore.block.state.IBlockState;
import ruiseki.okcore.block.state.IBlockStateNative;
import ruiseki.okcore.helper.EBSHelpers;

/**
 * Extends {@link BlockSnapshot} to preserve native extended block states
 * across snapshot creation and restoration.
 *
 * <p>
 * Unlike metadata, extended block states are stored separately from
 * vanilla block states. Therefore, they must be explicitly captured and
 * restored when a block snapshot is created or applied.
 * </p>
 *
 * @see IBlockSnapshot
 * @see IBlockStateNative
 * @see BlockStateStorage
 */
@Mixin(BlockSnapshot.class)
public abstract class MixinBlockSnapshot implements IBlockSnapshot {

    @Unique
    private @Nullable IBlockState okcore$capturedState;

    @Override
    public @Nullable IBlockState okcore$getCapturedState() {
        return this.okcore$capturedState;
    }

    @Override
    public void okcore$setCapturedState(@Nullable IBlockState state) {
        this.okcore$capturedState = state;
    }

    /**
     * Captures the extended block state when a snapshot is constructed.
     *
     * <p>
     * Only blocks implementing {@link IBlockStateNative} require
     * extended state capture. The state is read directly from the
     * corresponding {@link ExtendedBlockStorage}.
     * </p>
     *
     * <p>
     * The constructor accepting an additional update flag delegates
     * to this constructor, so injecting here covers both construction
     * paths.
     * </p>
     */
    @Inject(method = "<init>(Lnet/minecraft/world/World;IIILnet/minecraft/block/Block;I)V", at = @At("TAIL"))
    private void okcore$captureBlockState(World world, int x, int y, int z, Block block, int meta, CallbackInfo ci) {
        if (!(block instanceof IBlockStateNative)) return;

        ExtendedBlockStorage ebs = EBSHelpers.getEBS(world, x >> 4, y >> 4, z >> 4);
        if (ebs == null) return;

        BlockStateStorage storage = BlockStateStorage.getStorage(ebs, false);
        if (storage == null) return;

        this.okcore$capturedState = storage.getBlockState(x & 0xF, y & 0xF, z & 0xF);
    }

    /**
     * Restores the captured extended block state after {@link BlockSnapshot#restore}.
     *
     * <p>
     * The vanilla restoration process updates the block through the
     * normal world block-setting mechanism. The corresponding chunk
     * implementation may initialize its extended state to the default
     * value during that process. This injection overwrites that value
     * with the state originally captured by this snapshot.
     * </p>
     */
    @Inject(method = "restore(ZZ)Z", at = @At("TAIL"), remap = false)
    private void okcore$restoreBlockState(boolean force, boolean applyPhysics, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() || this.okcore$capturedState == null) return;

        BlockSnapshot self = (BlockSnapshot) (Object) this;
        ExtendedBlockStorage ebs = EBSHelpers.getEBS(self.world, self.x >> 4, self.y >> 4, self.z >> 4);
        if (ebs == null) return;

        BlockStateStorage storage = BlockStateStorage.getStorage(ebs, true);
        if (storage == null) return;

        storage.setBlockState(self.x & 0xF, self.y & 0xF, self.z & 0xF, this.okcore$capturedState);
    }

    /**
     * Restores the captured extended block state at a specified location.
     *
     * <p>
     * Unlike {@link #okcore$restoreBlockState}, this method restores
     * the snapshot into a potentially different world or position.
     * The captured state is therefore written to the destination
     * coordinates rather than the original snapshot coordinates.
     * </p>
     *
     */
    @Inject(method = "restoreToLocation(Lnet/minecraft/world/World;IIIZZ)Z", at = @At("TAIL"), remap = false)
    private void okcore$restoreBlockStateToLocation(World world, int x, int y, int z, boolean force,
        boolean applyPhysics, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() || this.okcore$capturedState == null) return;

        ExtendedBlockStorage ebs = EBSHelpers.getEBS(world, x >> 4, y >> 4, z >> 4);
        if (ebs == null) return;

        BlockStateStorage storage = BlockStateStorage.getStorage(ebs, true);
        if (storage == null) return;

        storage.setBlockState(x & 0xF, y & 0xF, z & 0xF, this.okcore$capturedState);
    }
}
