package ruiseki.okcore.mixins.early.blockstate;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S23PacketBlockChange;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import ruiseki.okcore.block.state.IBlockStateS23;

@Mixin(S23PacketBlockChange.class)
public class MixinS23Packet implements IBlockStateS23 {

    @Unique
    private NBTTagCompound okcore$blockState;

    @Override
    public NBTTagCompound okcore$getBlockState() {
        return this.okcore$blockState;
    }

    @Override
    public void okcore$setBlockState(NBTTagCompound state) {
        this.okcore$blockState = state;
    }
}
