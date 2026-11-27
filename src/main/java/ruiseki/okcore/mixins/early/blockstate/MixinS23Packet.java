package ruiseki.okcore.mixins.early.blockstate;

import net.minecraft.network.play.server.S23PacketBlockChange;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import ruiseki.okcore.block.state.IBlockStateS23;

@Mixin(S23PacketBlockChange.class)
public class MixinS23Packet implements IBlockStateS23 {

    @Unique
    private int okcore$stateId = -1;

    @Override
    public void okcore$setStateId(int stateId) {
        this.okcore$stateId = stateId;
    }

    @Override
    public int okcore$getStateId() {
        return this.okcore$stateId;
    }
}
