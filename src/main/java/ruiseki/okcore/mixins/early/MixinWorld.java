package ruiseki.okcore.mixins.early;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public abstract class MixinWorld {

    @Shadow
    public boolean isRemote;

    @Shadow
    public abstract void notifyBlocksOfNeighborChange(int x, int y, int z, Block blockIn);

    @Inject(method = "markAndNotifyBlock", at = @At("TAIL"), remap = false)
    private void addElseIfLogic(int x, int y, int z, Chunk chunk, Block oldBlock, Block newBlock, int flags,
        CallbackInfo ci) {
        if (!this.isRemote && (flags & 16) == 0) {
            this.notifyBlocksOfNeighborChange(x, y, z, newBlock);
        }
    }
}
