package ruiseki.okcore.block.state;

import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import ruiseki.okcore.datastructure.BlockPos;

public class BlockStateStorage {

    /**
     * Stores states by local position within an ExtendedBlockStorage section.
     */
    private final Short2ObjectOpenHashMap<IBlockState> blockStates = new Short2ObjectOpenHashMap<>();

    /**
     * Packs local section coordinates into a 12-bit index.
     *
     * Layout:
     * Y: bits 8-11
     * Z: bits 4-7
     * X: bits 0-3
     */
    public static short pack(int x, int y, int z) {
        x &= 0xF;
        y &= 0xF;
        z &= 0xF;

        return (short) (y << 8 | z << 4 | x);
    }

    public static int unpackX(int l) {
        return l & 0xF;
    }

    public static int unpackY(int l) {
        return l >> 8 & 0xF;
    }

    public static int unpackZ(int l) {
        return l >> 4 & 0xF;
    }

    public boolean hasState(int rx, int ry, int rz) {
        return blockStates.containsKey(pack(rx, ry, rz));
    }

    public @Nullable IBlockState getBlockState(BlockPos pos) {
        return this.getBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
    }

    public @Nullable IBlockState getBlockState(int rx, int ry, int rz) {
        return blockStates.get(pack(rx, ry, rz));
    }

    public void setBlockState(BlockPos pos, @Nullable IBlockState state) {
        this.setBlockState(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15, state);
    }

    public void setBlockState(int rx, int ry, int rz, @Nullable IBlockState state) {
        short packed = pack(rx, ry, rz);

        if (state == null) {
            blockStates.remove(packed);
        } else {
            blockStates.put(packed, state);
        }
    }

    public Short2ObjectOpenHashMap<IBlockState> getBlockStates() {
        return blockStates;
    }

    public boolean isEmpty() {
        return blockStates.isEmpty();
    }

    public int size() {
        return blockStates.size();
    }

    public void clear() {
        blockStates.clear();
    }

    public static @Nullable BlockStateStorage getStorage(ExtendedBlockStorage ebs, boolean createIfMissing) {
        if (ebs == null) return null;
        if (!(ebs instanceof IBlockStateEBS stateEBS)) return null;

        BlockStateStorage storage = stateEBS.okcore$getBlockStateStorage();

        if (createIfMissing && storage == null) {
            storage = new BlockStateStorage();
            stateEBS.okcore$setBlockStateStorage(storage);
            return storage;
        }

        return storage;
    }

    public static boolean removeStorage(ExtendedBlockStorage ebs) {
        if (ebs == null) return false;
        if (!(ebs instanceof IBlockStateEBS stateEBS)) return false;

        if (stateEBS.okcore$getBlockStateStorage() == null) {
            return false;
        }

        stateEBS.okcore$setBlockStateStorage(null);
        return true;
    }

    public BlockStateStorage copy() {
        BlockStateStorage copy = new BlockStateStorage();
        copy.blockStates.putAll(this.blockStates);
        return copy;
    }
}
