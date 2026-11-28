package ruiseki.okcore.block.state;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.falsepattern.chunk.api.DataManager;
import com.falsepattern.chunk.api.DataManager.BlockPacketDataManager;
import com.falsepattern.chunk.api.DataManager.CubicPacketDataManager;
import com.falsepattern.chunk.api.DataManager.PacketDataManager;
import com.falsepattern.chunk.api.DataManager.SubChunkDataManager;

import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.Reference;
import ruiseki.okcore.helper.EBSHelpers;
import ruiseki.okcore.network.ExtendedBuffer;

@SuppressWarnings({ "resource", "UnstableApiUsage" })
public class BSSDataManager
    implements DataManager, PacketDataManager, CubicPacketDataManager, BlockPacketDataManager, SubChunkDataManager {

    public static final int MAX_BYTES_PER_STATE = 1024;

    private static final ThreadLocal<ExtendedBuffer> STATE_BUFFERS = ThreadLocal
        .withInitial(() -> new ExtendedBuffer(Unpooled.buffer(MAX_BYTES_PER_STATE)));

    @Override
    public void writeBlockToPacket(Chunk chunk, int x, int y, int z, S23PacketBlockChange packet) {
        // Server world -> S23

        if (!(packet.field_148883_d instanceof IBlockStateNative nativeBlock)) return;
        if (!(packet instanceof IBlockStateS23 stateS23)) return;

        ExtendedBlockStorage ebs = EBSHelpers.getEBS(chunk, y >> 4);
        BlockStateStorage storage = BlockStateStorage.getStorage(ebs, false);

        if (storage == null) return;

        IBlockState state = storage.getBlockState(x & 0xF, y & 0xF, z & 0xF);
        stateS23.okcore$setState(
            state != null ? nativeBlock.getStateContainer()
                .serialize(state) : null);
    }

    @Override
    public void writeBlockPacketToBuffer(S23PacketBlockChange packet, PacketBuffer buffer) throws IOException {
        // S23 -> buffer

        if (!(packet.field_148883_d instanceof IBlockStateNative)) return;
        if (!(packet instanceof IBlockStateS23 stateS23)) return;

        buffer.writeNBTTagCompoundToBuffer(stateS23.okcore$getState());
    }

    @Override
    public void readBlockPacketFromBuffer(S23PacketBlockChange packet, PacketBuffer buffer) throws IOException {
        // Buffer -> S23

        if (!(packet.field_148883_d instanceof IBlockStateNative)) return;
        if (!(packet instanceof IBlockStateS23 stateS23)) return;

        stateS23.okcore$setState(buffer.readNBTTagCompoundFromBuffer());
    }

    @Override
    public void readBlockFromPacket(Chunk chunk, int x, int y, int z, S23PacketBlockChange packet) {
        // S23 -> client world

        if (!(packet instanceof IBlockStateS23 stateS23)) return;

        IBlockStateNative nativeBlock = packet.field_148883_d instanceof IBlockStateNative stateNative ? stateNative
            : null;

        ExtendedBlockStorage ebs = EBSHelpers.getEBS(chunk, y >> 4);
        BlockStateStorage storage = BlockStateStorage.getStorage(ebs, nativeBlock != null);

        if (nativeBlock == null) {
            if (storage != null && storage.hasState(x & 0xF, y & 0xF, z & 0xF)) {
                storage.setBlockState(x & 0xF, y & 0xF, z & 0xF, null);
                if (storage.isEmpty()) {
                    BlockStateStorage.removeStorage(ebs);
                }
            }
            return;
        }

        Objects.requireNonNull(storage, "Storage should not be null");

        IBlockState blockState = nativeBlock.getStateContainer()
            .deserialize(stateS23.okcore$getState());

        storage.setBlockState(x & 0xF, y & 0xF, z & 0xF, blockState);

        chunk.func_150807_a(
            x & 0xF,
            y,
            z & 0xF,
            nativeBlock.getBlockFromState(blockState),
            nativeBlock.getMetaFromState(blockState));

        chunk.worldObj.markBlockForUpdate((chunk.xPosition << 4) + x, y, (chunk.zPosition << 4) + z);
    }

    @Override
    public int maxPacketSizeCubic() {
        return MAX_BYTES_PER_STATE * 16 * 16 * 16;
    }

    @Override
    public void writeToBuffer(Chunk chunk, ExtendedBlockStorage ebs, ByteBuffer buffer) {
        BlockStateStorage storage = BlockStateStorage.getStorage(ebs, false);

        ExtendedBuffer packet = new ExtendedBuffer(Unpooled.wrappedBuffer(buffer));
        packet.clear();

        if (storage == null) {
            packet.writeVarIntToBuffer(0);
            buffer.position(buffer.position() + packet.writerIndex());
            return;
        }

        Short2ObjectOpenHashMap<IBlockState> states = storage.getBlockStates();
        packet.writeVarIntToBuffer(states.size());

        ExtendedBuffer stateBuffer = STATE_BUFFERS.get();
        states.forEach((loc, state) -> {
            if (state == null || !(state.getBlock() instanceof IBlockStateNative nativeBlock)) return;

            stateBuffer.clear();
            stateBuffer.writeShort(loc);
            NBTTagCompound stateNBT = nativeBlock.getStateContainer()
                .serialize(state);
            stateBuffer.writeNBTTagCompound(stateNBT);

            int stateLen = stateBuffer.writerIndex();

            if (stateLen > MAX_BYTES_PER_STATE) {
                OKCore.okLog(
                    Level.ERROR,
                    "Block state tried to sync too many bytes: reduce the number of bytes used, or increase the limit in the OKCore config. (Bytes used={}, State={})",
                    stateLen,
                    state);

                stateBuffer.clear();
                stateBuffer.writeShort(loc);
                stateBuffer.writeNBTTagCompound(null);

                stateLen = stateBuffer.writerIndex();
            }

            packet.writeVarIntToBuffer(stateLen);
            packet.writeBytes(stateBuffer, 0, stateLen);
        });

        buffer.position(buffer.position() + packet.writerIndex());
    }

    @Override
    public void readFromBuffer(Chunk chunk, ExtendedBlockStorage ebs, ByteBuffer buffer) {
        ExtendedBuffer packet = new ExtendedBuffer(Unpooled.wrappedBuffer(buffer));
        packet.writerIndex(buffer.remaining())
            .readerIndex(0);

        int stateCount = packet.readVarIntFromBuffer();

        BlockStateStorage storage = BlockStateStorage.getStorage(ebs, stateCount > 0);

        if (stateCount == 0 && storage != null) {
            // We have a storage, but we don't want to have any states, remove the storage to clear them
            BlockStateStorage.removeStorage(ebs);
            buffer.position(buffer.position() + packet.readerIndex());
            return;
        }

        if (storage == null) {
            // No storage and we aren't adding any new states, do nothing
            buffer.position(buffer.position() + packet.readerIndex());
            return;
        }

        ExtendedBuffer stateBuffer = STATE_BUFFERS.get();
        for (int i = 0; i < stateCount; i++) {
            stateBuffer.clear();

            int stateLen = packet.readVarIntFromBuffer();
            packet.readBytes(stateBuffer, 0, stateLen);
            stateBuffer.readerIndex(0)
                .writerIndex(stateLen);

            short loc = stateBuffer.readShort();
            NBTTagCompound stateNBT = stateBuffer.readNBTTagCompound();

            int rx = BlockStateStorage.unpackX(loc);
            int ry = BlockStateStorage.unpackY(loc);
            int rz = BlockStateStorage.unpackZ(loc);

            if (!(ebs.getBlockByExtId(rx, ry, rz) instanceof IBlockStateNative stateNative)) {
                continue;
            }

            try {
                IBlockState state = stateNative.getStateContainer()
                    .deserialize(stateNBT);
                storage.setBlockState(rx, ry, rz, state);
            } catch (Throwable t) {
                OKCore.okLog(
                    Level.ERROR,
                    "Failed to load BlockState (World={}, X={}, Y={}, Z={}, Block={})",
                    chunk.worldObj,
                    (chunk.xPosition << 4) + rx,
                    ebs.getYLocation() + ry,
                    (chunk.zPosition << 4) + rz,
                    stateNative,
                    t);
            }
        }

        buffer.position(buffer.position() + packet.readerIndex());
    }

    @Override
    public int maxPacketSize() {
        return MAX_BYTES_PER_STATE * 16 * 16 * 16 * 16;
    }

    @Override
    public void writeToBuffer(Chunk chunk, int subChunkMask, boolean forceUpdate, ByteBuffer buffer) {
        ExtendedBlockStorage[] ebses = chunk.getBlockStorageArray();
        for (int i = 0; i < ebses.length; i++) {
            if ((subChunkMask & 1 << i) == 0) continue;

            ExtendedBlockStorage ebs = ebses[i];

            writeToBuffer(chunk, ebs, buffer);
        }
    }

    @Override
    public void readFromBuffer(Chunk chunk, int subChunkMask, boolean forceUpdate, ByteBuffer buffer) {
        ExtendedBlockStorage[] ebses = chunk.getBlockStorageArray();

        for (int i = 0; i < ebses.length; i++) {
            if ((subChunkMask & 1 << i) == 0) continue;

            ExtendedBlockStorage ebs = ebses[i];

            readFromBuffer(chunk, ebs, buffer);
        }
    }

    @Override
    public void writeSubChunkToNBT(Chunk chunk, ExtendedBlockStorage ebs, NBTTagCompound nbt) {
        BlockStateStorage storage = BlockStateStorage.getStorage(ebs, false);
        if (storage == null) return;

        var states = storage.getBlockStates();
        if (states.isEmpty()) return;

        ExtendedBuffer stateBuffer = STATE_BUFFERS.get();
        NBTTagList list = new NBTTagList();

        states.forEach((loc, state) -> {
            if (state == null || !(state.getBlock() instanceof IBlockStateNative nativeBlock)) return;
            try {
                stateBuffer.clear();
                NBTTagCompound stateNBT = nativeBlock.getStateContainer()
                    .serialize(state);
                stateBuffer.writeNBTTagCompound(stateNBT);

                int len = stateBuffer.writerIndex();
                byte[] data = new byte[len];
                stateBuffer.getBytes(0, data);

                NBTTagCompound entry = new NBTTagCompound();
                entry.setShort("loc", loc);
                entry.setByteArray("data", data);

                list.appendTag(entry);
            } catch (RuntimeException e) {
                OKCore.okLog(
                    Level.ERROR,
                    "Failed to save BlockState (World={}, X={}, Y={}, Z={}, State={})",
                    chunk.worldObj,
                    (chunk.xPosition << 4) + BlockStateStorage.unpackX(loc),
                    ebs.getYLocation() + BlockStateStorage.unpackY(loc),
                    (chunk.zPosition << 4) + BlockStateStorage.unpackZ(loc),
                    state,
                    e);
            }
        });

        nbt.setTag("states", list);
    }

    @Override
    public void readSubChunkFromNBT(Chunk chunk, ExtendedBlockStorage ebs, NBTTagCompound nbt) {
        if (!nbt.hasKey("states")) return;

        NBTTagList list = nbt.getTagList("states", 10); // 10 = TAG_Compound
        if (list.tagCount() == 0) return;

        BlockStateStorage storage = BlockStateStorage.getStorage(ebs, true);
        if (storage == null) return;

        ExtendedBuffer stateBuffer = STATE_BUFFERS.get();

        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound entry = list.getCompoundTagAt(i);
            short loc = entry.getShort("loc");
            byte[] data = entry.getByteArray("data");

            stateBuffer.clear();
            stateBuffer.writeBytes(data);
            stateBuffer.readerIndex(0);

            NBTTagCompound stateNBT = stateBuffer.readNBTTagCompound();

            int rx = BlockStateStorage.unpackX(loc);
            int ry = BlockStateStorage.unpackY(loc);
            int rz = BlockStateStorage.unpackZ(loc);

            if (!(ebs.getBlockByExtId(rx, ry, rz) instanceof IBlockStateNative stateNative)) continue;

            try {
                IBlockState state = stateNative.getStateContainer()
                    .deserialize(stateNBT);
                storage.setBlockState(rx, ry, rz, state);
            } catch (Throwable t) {
                OKCore.okLog(
                    Level.ERROR,
                    "Failed to load BlockState (World={}, X={}, Y={}, Z={}, Block={})",
                    chunk.worldObj,
                    (chunk.xPosition << 4) + rx,
                    ebs.getYLocation() + ry,
                    (chunk.zPosition << 4) + rz,
                    stateNative,
                    t);
            }
        }
    }

    @Override
    public void cloneSubChunk(Chunk fromChunk, ExtendedBlockStorage from, ExtendedBlockStorage to) {
        if (!(to instanceof IBlockStateEBS toExt)) return;

        BlockStateStorage storage = BlockStateStorage.getStorage(from, false);

        toExt.okcore$setBlockStateStorage(storage == null ? null : storage.copy());
    }

    @Override
    public @NotNull String version() {
        return Reference.MOD_VERSION;
    }

    @Override
    public @Nullable String newInstallDescription() {
        return "OKCore native block state storage. This allows mods to emulate modern's BlockStates without metadata shims.";
    }

    @Override
    public @NotNull String uninstallMessage() {
        return "OKCore's native block state storage has been uninstalled. Any blocks that use this will be corrupted.";
    }

    @Override
    public @Nullable String versionChangeMessage(String priorVersion) {
        return null;
    }

    @Override
    public String domain() {
        return "blockstate";
    }

    @Override
    public String id() {
        return Reference.MOD_ID;
    }
}
