package ruiseki.okcore.mixins.early.blockstate;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S23PacketBlockChange;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import ruiseki.okcore.block.state.IBlockStateS23;

@Mixin(S23PacketBlockChange.class)
public class MixinS23Packet implements IBlockStateS23 {

    @Unique
    private NBTTagCompound okcore$state = null;

    @Override
    public void okcore$setState(NBTTagCompound state) {
        this.okcore$state = state;
    }

    @Override
    public NBTTagCompound okcore$getState() {
        return this.okcore$state;
    }
}
