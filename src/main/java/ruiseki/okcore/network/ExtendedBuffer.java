package ruiseki.okcore.network;

import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import ruiseki.okcore.datastructure.BlockStack;

/**
 * An extended packet buffer.
 *
 * @author rubensworks
 */
public class ExtendedBuffer extends PacketBuffer {

    public ExtendedBuffer(ByteBuf wrapped) {
        super(wrapped);
    }

    public String readString() {
        return ByteBufUtils.readUTF8String(this);
    }

    public void writeString(String string) {
        ByteBufUtils.writeUTF8String(this, string);
    }

    /**
     * Reads an FluidStack from this buffer
     */
    public FluidStack readFluidStack() throws IOException {
        NBTTagCompound tagCompound = this.readNBTTagCompoundFromBuffer();
        return FluidStack.loadFluidStackFromNBT(tagCompound);
    }

    /**
     * Writes the FluidStack's
     */
    public void writeFluidStack(FluidStack stack) throws IOException {
        this.writeNBTTagCompoundToBuffer(stack.writeToNBT(new NBTTagCompound()));
    }

    /**
     * Reads an BlockStack from this buffer
     */
    public BlockStack readBlockStack() {
        BlockStack blockStack = null;
        short id = this.readShort();

        if (id >= 0) {
            short meta = this.readShort();
            blockStack = new BlockStack(Block.getBlockById(id), meta);
        }

        return blockStack;
    }

    /**
     * Writes the BlockStack's ID (short), meta (short)
     */
    public void writeBlockStack(BlockStack stack) {
        if (stack == null) {
            this.writeShort(-1);
        } else {
            this.writeShort(Block.getIdFromBlock(stack.getBlock()));
            this.writeShort(stack.getMeta());
        }
    }

    /**
     * Reads an ResourceLocation from this buffer
     */
    public ResourceLocation readResourceLocation() {
        if (!this.readBoolean()) {
            return null;
        }

        String domain = this.readString();
        String path = this.readString();

        if (!domain.isEmpty() && !path.isEmpty()) {
            return new ResourceLocation(domain, path);
        }

        return null;
    }

    /**
     * Writes the ResourceLocation's Domain (String), Path (String)
     */
    public void writeResourceLocation(ResourceLocation location) {
        if (location == null) {
            this.writeBoolean(false);
        } else {
            this.writeBoolean(true);
            this.writeString(location.getResourceDomain());
            this.writeString(location.getResourcePath());
        }
    }
}
