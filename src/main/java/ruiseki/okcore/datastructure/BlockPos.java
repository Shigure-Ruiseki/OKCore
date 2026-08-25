package ruiseki.okcore.datastructure;

import java.util.Iterator;

import javax.annotation.concurrent.Immutable;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;

import com.google.common.base.Strings;
import com.google.common.collect.AbstractIterator;
import com.gtnewhorizon.gtnhlib.blockpos.IBlockPos;

@Immutable
public class BlockPos extends Vector3i implements Comparable<BlockPos>, IBlockPos {

    public static final BlockPos ORIGIN = new BlockPos();

    private static final int NUM_X_BITS = 26;
    private static final int NUM_Z_BITS = NUM_X_BITS;
    private static final int NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;
    private static final int Y_SHIFT = NUM_Z_BITS;
    private static final int X_SHIFT = Y_SHIFT + NUM_Y_BITS;
    private static final long X_MASK = (1L << NUM_X_BITS) - 1L;
    private static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;
    private static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;

    public BlockPos() {
        super(0, 0, 0);
    }

    public BlockPos(int x, int y, int z) {
        super(x, y, z);
    }

    public BlockPos(double x, double y, double z) {
        super(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
    }

    public BlockPos(ChunkPosition chunkPosition) {
        super(chunkPosition.chunkPosX, chunkPosition.chunkPosY, chunkPosition.chunkPosZ);
    }

    public BlockPos(ChunkCoordinates chunkPosition) {
        super(chunkPosition.posX, chunkPosition.posY, chunkPosition.posZ);
    }

    public BlockPos(MovingObjectPosition position) {
        super(position.blockX, position.blockY, position.blockZ);
    }

    public BlockPos(TileEntity tile) {
        this(tile.xCoord, tile.yCoord, tile.zCoord);
    }

    public BlockPos(Entity entity) {
        this(entity.posX, entity.posY, entity.posZ);
    }

    public BlockPos(BlockPos pos) {
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public BlockPos(String x, String y, String z) {
        super(
            Strings.isNullOrEmpty(x) ? 0 : Integer.parseInt(x),
            Strings.isNullOrEmpty(y) ? 0 : Integer.parseInt(y),
            Strings.isNullOrEmpty(z) ? 0 : Integer.parseInt(z));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    /**
     * Add the given coordinates to the coordinates of this BlockPos
     */
    public BlockPos add(double x, double y, double z) {
        return x == 0.0D && y == 0.0D && z == 0.0D ? this
            : new BlockPos((double) this.getX() + x, (double) this.getY() + y, (double) this.getZ() + z);
    }

    /**
     * Add the given coordinates to the coordinates of this BlockPos
     */
    public BlockPos add(int x, int y, int z) {
        return x == 0 && y == 0 && z == 0 ? this : new BlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
    }

    /**
     * Add the given Vector to this BlockPos
     */
    public BlockPos add(Vector3i vec) {
        return this.add(vec.x(), vec.y(), vec.z());
    }

    /**
     * Subtract the given Vector from this BlockPos
     */
    public BlockPos subtract(Vector3i vec) {
        return this.add(-vec.x(), -vec.y(), -vec.z());
    }

    /**
     * Offset this BlockPos 1 block up
     */
    public BlockPos up() {
        return this.up(1);
    }

    /**
     * Offset this BlockPos n blocks up
     */
    public BlockPos up(int n) {
        return this.offset(ForgeDirection.UP, n);
    }

    /**
     * Offset this BlockPos 1 block down
     */
    public BlockPos down() {
        return this.down(1);
    }

    /**
     * Offset this BlockPos n blocks down
     */
    public BlockPos down(int n) {
        return this.offset(ForgeDirection.DOWN, n);
    }

    /**
     * Offset this BlockPos 1 block in northern direction
     */
    public BlockPos north() {
        return this.north(1);
    }

    /**
     * Offset this BlockPos n blocks in northern direction
     */
    public BlockPos north(int n) {
        return this.offset(ForgeDirection.NORTH, n);
    }

    /**
     * Offset this BlockPos 1 block in southern direction
     */
    public BlockPos south() {
        return this.south(1);
    }

    /**
     * Offset this BlockPos n blocks in southern direction
     */
    public BlockPos south(int n) {
        return this.offset(ForgeDirection.SOUTH, n);
    }

    /**
     * Offset this BlockPos 1 block in western direction
     */
    public BlockPos west() {
        return this.west(1);
    }

    /**
     * Offset this BlockPos n blocks in western direction
     */
    public BlockPos west(int n) {
        return this.offset(ForgeDirection.WEST, n);
    }

    /**
     * Offset this BlockPos 1 block in eastern direction
     */
    public BlockPos east() {
        return this.east(1);
    }

    /**
     * Offset this BlockPos n blocks in eastern direction
     */
    public BlockPos east(int n) {
        return this.offset(ForgeDirection.EAST, n);
    }

    /**
     * Offset this BlockPos 1 block in the given direction
     */
    public BlockPos offset(ForgeDirection facing) {
        return this.offset(facing, 1);
    }

    /**
     * Offsets this BlockPos n blocks in the given direction
     */
    public BlockPos offset(ForgeDirection facing, int n) {
        return n == 0 ? this
            : new BlockPos(
                this.getX() + facing.offsetX * n,
                this.getY() + facing.offsetY * n,
                this.getZ() + facing.offsetZ * n);
    }

    public BlockPos offset(int x, int y, int z) {
        return new BlockPos(this.x + x, this.y + y, this.z + z);
    }

    /**
     * Calculate the cross product of this and the given Vector
     */
    public BlockPos crossProduct(Vector3i vec) {
        return new BlockPos(
            this.getY() * vec.z() - this.getZ() * vec.y(),
            this.getZ() * vec.x() - this.getX() * vec.z(),
            this.getX() * vec.y() - this.getY() * vec.x());
    }

    /**
     * Serialize this BlockPos into a long value
     */
    public long toLong() {
        return ((long) this.getX() & X_MASK) << X_SHIFT | ((long) this.getY() & Y_MASK) << Y_SHIFT
            | ((long) this.getZ() & Z_MASK) << 0;
    }

    /**
     * Create a BlockPos from a serialized long value (created by toLong)
     */
    public static BlockPos fromLong(long serialized) {
        int i = (int) (serialized << 64 - X_SHIFT - NUM_X_BITS >> 64 - NUM_X_BITS);
        int j = (int) (serialized << 64 - Y_SHIFT - NUM_Y_BITS >> 64 - NUM_Y_BITS);
        int k = (int) (serialized << 64 - NUM_Z_BITS >> 64 - NUM_Z_BITS);
        return new BlockPos(i, j, k);
    }

    public static Iterable<BlockPos> getAllInBox(BlockPos from, BlockPos to) {
        return getAllInBox(
            Math.min(from.getX(), to.getX()),
            Math.min(from.getY(), to.getY()),
            Math.min(from.getZ(), to.getZ()),
            Math.max(from.getX(), to.getX()),
            Math.max(from.getY(), to.getY()),
            Math.max(from.getZ(), to.getZ()));
    }

    public static Iterable<BlockPos> getAllInBox(final int x1, final int y1, final int z1, final int x2, final int y2,
        final int z2) {
        return new Iterable<BlockPos>() {

            public Iterator<BlockPos> iterator() {
                return new AbstractIterator<BlockPos>() {

                    private boolean first = true;
                    private int lastPosX;
                    private int lastPosY;
                    private int lastPosZ;

                    protected BlockPos computeNext() {
                        if (this.first) {
                            this.first = false;
                            this.lastPosX = x1;
                            this.lastPosY = y1;
                            this.lastPosZ = z1;
                            return new BlockPos(x1, y1, z1);
                        } else if (this.lastPosX == x2 && this.lastPosY == y2 && this.lastPosZ == z2) {
                            return (BlockPos) this.endOfData();
                        } else {
                            if (this.lastPosX < x2) {
                                ++this.lastPosX;
                            } else if (this.lastPosY < y2) {
                                this.lastPosX = x1;
                                ++this.lastPosY;
                            } else if (this.lastPosZ < z2) {
                                this.lastPosX = x1;
                                this.lastPosY = y1;
                                ++this.lastPosZ;
                            }

                            return new BlockPos(this.lastPosX, this.lastPosY, this.lastPosZ);
                        }
                    }
                };
            }
        };
    }

    /**
     * Calculate squared distance to the given coordinates
     */
    public double distanceSq(double toX, double toY, double toZ) {
        double d0 = (double) this.getX() - toX;
        double d1 = (double) this.getY() - toY;
        double d2 = (double) this.getZ() - toZ;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    /**
     * Compute square of distance from point x, y, z to center of this Block
     */
    public double distanceSqToCenter(double xIn, double yIn, double zIn) {
        double d0 = (double) this.getX() + 0.5D - xIn;
        double d1 = (double) this.getY() + 0.5D - yIn;
        double d2 = (double) this.getZ() + 0.5D - zIn;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    /**
     * Calculate squared distance to the given Vector
     */
    public double distanceSq(Vector3i to) {
        return this.distanceSq((double) to.x, (double) to.y, (double) to.z());
    }

    public BiomeGenBase getBiomeGen(World world) {
        return world.getBiomeGenForCoords(getX(), getZ());
    }

    public void markBlockForUpdate(World world) {
        world.markBlockForUpdate(getX(), getY(), getZ());
    }

    public TileEntity getTileEntity(IBlockAccess world) {
        return world.getTileEntity(getX(), getY(), getZ());
    }

    public Block getBlock(IBlockAccess world) {
        return world.getBlock(getX(), getY(), getZ());
    }

    public int getBlockMetadata(IBlockAccess world) {
        return world.getBlockMetadata(getX(), getY(), getZ());
    }

    public boolean equals(int x, int y, int z) {
        return x == getX() && y == getY() && z == getZ();
    }

    public boolean equals(TileEntity tile) {
        return tile.xCoord == getX() && tile.yCoord == getY() && tile.zCoord == getZ();
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof BlockPos other)) return false;
        if (!other.canEqual(this)) return false;
        return getX() == other.getX() && getY() == other.getY() && getZ() == other.getZ();
    }

    protected boolean canEqual(final Object other) {
        return other instanceof BlockPos;
    }

    public static int compareBlockPos(BlockPos pos1, BlockPos pos2) {
        int compX = Integer.compare(pos1.getX(), pos2.getX());
        if (compX == 0) {
            int compY = Integer.compare(pos1.getY(), pos2.getY());
            if (compY == 0) {
                return Integer.compare(pos1.getZ(), pos2.getZ());
            }
            return compY;
        }
        return compX;
    }

    public boolean isLoaded(World world) {
        if (world == null) return false;

        int x = getX();
        int y = getY();
        int z = getZ();

        return world.blockExists(x, y, z);
    }

    public boolean isAirBlock(World world) {
        if (world == null) return false;

        int x = getX();
        int y = getY();
        int z = getZ();

        if (!isLoaded(world)) return false;

        return world.isAirBlock(x, y, z);
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + getX();
        result = result * PRIME + getY();
        result = result * PRIME + getZ();
        return result;
    }

    public BlockPos withX(final int x) {
        return getX() == x ? this : new BlockPos(x, getY(), getZ());
    }

    public BlockPos withY(final int y) {
        return getY() == y ? this : new BlockPos(getX(), y, getZ());
    }

    public BlockPos withZ(final int z) {
        return getZ() == z ? this : new BlockPos(getX(), getY(), z);
    }

    public BlockPos readFromNBT(NBTTagCompound compound) {
        return new BlockPos(compound.getInteger("X"), compound.getInteger("Y"), compound.getInteger("Z"));
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setInteger("X", getX());
        tag.setInteger("Y", getY());
        tag.setInteger("Z", getZ());
        return tag;
    }

    public long distSqr(BlockPos other) {
        long dx = this.getX() - other.getX();
        long dy = this.getY() - other.getY();
        long dz = this.getZ() - other.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    @Override
    public long asLong() {
        return toLong();
    }

    @Override
    public IBlockPos copy() {
        return new BlockPos(this.x, this.y, this.z);
    }

    @Override
    public int compareTo(@NotNull BlockPos o) {
        return compareBlockPos(this, o);
    }
}
