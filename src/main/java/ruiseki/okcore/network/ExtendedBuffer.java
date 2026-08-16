package ruiseki.okcore.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

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

    /**
     * Reads a collection of strings from the buffer.
     *
     * @param <C>        The collection type.
     * @param collection The target collection to populate.
     * @return The populated collection, or null if a null marker was written.
     */
    public <C extends Collection<String>> C readStringCollection(C collection) {
        int size = this.readInt();
        if (size < 0) {
            return null;
        }

        for (int i = 0; i < size; i++) {
            collection.add(this.readString());
        }

        return collection;
    }

    /**
     * Writes a collection of strings to the buffer.
     *
     * @param collection The collection to write.
     */
    public void writeStringCollection(Collection<String> collection) {
        if (collection == null) {
            this.writeInt(-1);
        } else {
            this.writeInt(collection.size());
            for (String string : collection) {
                this.writeString(string);
            }
        }
    }

    /**
     * Reads a List of strings from the buffer.
     */
    public List<String> readStringList() {
        return readStringCollection(new ArrayList<>());
    }

    /**
     * Reads a Set of strings from the buffer.
     */
    public Set<String> readStringSet() {
        return readStringCollection(new HashSet<>());
    }

    /**
     * Reads a BlockState from this buffer.
     *
     * @return The parsed BlockState, or null if null was written or reading failed.
     */
    public BlockState readBlockState() {
        if (!this.readBoolean()) {
            return null;
        }

        String stateStr = this.readString();
        if (stateStr.isEmpty()) {
            return null;
        }

        try {
            return BlockState.fromString(null, stateStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Writes the BlockState to this buffer as a String using its toString() format.
     * @param state The BlockState to write.
     */
    public void writeBlockState(BlockState state) {
        if (state == null) {
            this.writeBoolean(false);
        } else {
            this.writeBoolean(true);
            this.writeString(state.toString());
        }
    }

}
