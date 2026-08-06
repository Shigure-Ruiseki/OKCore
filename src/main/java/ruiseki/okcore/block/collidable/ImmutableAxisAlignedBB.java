package ruiseki.okcore.block.collidable;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

import ruiseki.okcore.datastructure.BlockPos;

public class ImmutableAxisAlignedBB extends AxisAlignedBB {

    public ImmutableAxisAlignedBB(double x1, double y1, double z1, double x2, double y2, double z2) {
        super(x1, y1, z1, x2, y2, z2);
    }

    public ImmutableAxisAlignedBB(BlockPos pos) {
        this(
            (double) pos.getX(),
            (double) pos.getY(),
            (double) pos.getZ(),
            (double) (pos.getX() + 1),
            (double) (pos.getY() + 1),
            (double) (pos.getZ() + 1));
    }

    public ImmutableAxisAlignedBB(BlockPos pos1, BlockPos pos2) {
        this(
            (double) pos1.getX(),
            (double) pos1.getY(),
            (double) pos1.getZ(),
            (double) pos2.getX(),
            (double) pos2.getY(),
            (double) pos2.getZ());
    }

    public ImmutableAxisAlignedBB(Vec3 min, Vec3 max) {
        this(min.xCoord, min.yCoord, min.zCoord, max.xCoord, max.yCoord, max.zCoord);
    }

    public static ImmutableAxisAlignedBB fromBounds(double x1, double y1, double z1, double x2, double y2, double z2) {
        return new ImmutableAxisAlignedBB(x1, y1, z1, x2, y2, z2);
    }

    @Override
    public AxisAlignedBB setBounds(double x1, double y1, double z1, double x2, double y2, double z2) {
        throw new UnsupportedOperationException("ImmutableAxisAlignedBB cannot be modified!");
    }

    @Override
    public void setBB(AxisAlignedBB other) {
        throw new UnsupportedOperationException("ImmutableAxisAlignedBB cannot be modified!");
    }

    // ==========================================
    // Updated & Added Transformations
    // ==========================================

    public ImmutableAxisAlignedBB setMaxY(double y2) {
        return new ImmutableAxisAlignedBB(this.minX, this.minY, this.minZ, this.maxX, y2, this.maxZ);
    }

    @Override
    public ImmutableAxisAlignedBB contract(double x, double y, double z) {
        double d0 = this.minX;
        double d1 = this.minY;
        double d2 = this.minZ;
        double d3 = this.maxX;
        double d4 = this.maxY;
        double d5 = this.maxZ;

        if (x < 0.0D) d0 -= x;
        else if (x > 0.0D) d3 -= x;

        if (y < 0.0D) d1 -= y;
        else if (y > 0.0D) d4 -= y;

        if (z < 0.0D) d2 -= z;
        else if (z > 0.0D) d5 -= z;

        return new ImmutableAxisAlignedBB(d0, d1, d2, d3, d4, d5);
    }

    @Override
    public ImmutableAxisAlignedBB expand(double x, double y, double z) {
        double d0 = this.minX;
        double d1 = this.minY;
        double d2 = this.minZ;
        double d3 = this.maxX;
        double d4 = this.maxY;
        double d5 = this.maxZ;

        if (x < 0.0D) d0 += x;
        else if (x > 0.0D) d3 += x;

        if (y < 0.0D) d1 += y;
        else if (y > 0.0D) d4 += y;

        if (z < 0.0D) d2 += z;
        else if (z > 0.0D) d5 += z;

        return new ImmutableAxisAlignedBB(d0, d1, d2, d3, d4, d5);
    }

    public ImmutableAxisAlignedBB grow(double x, double y, double z) {
        return new ImmutableAxisAlignedBB(
            this.minX - x,
            this.minY - y,
            this.minZ - z,
            this.maxX + x,
            this.maxY + y,
            this.maxZ + z);
    }

    public ImmutableAxisAlignedBB grow(double value) {
        return this.grow(value, value, value);
    }

    public ImmutableAxisAlignedBB shrink(double value) {
        return this.grow(-value);
    }

    public ImmutableAxisAlignedBB intersect(AxisAlignedBB other) {
        double d0 = Math.max(this.minX, other.minX);
        double d1 = Math.max(this.minY, other.minY);
        double d2 = Math.max(this.minZ, other.minZ);
        double d3 = Math.min(this.maxX, other.maxX);
        double d4 = Math.min(this.maxY, other.maxY);
        double d5 = Math.min(this.maxZ, other.maxZ);
        return new ImmutableAxisAlignedBB(d0, d1, d2, d3, d4, d5);
    }

    public ImmutableAxisAlignedBB union(AxisAlignedBB other) {
        double d0 = Math.min(this.minX, other.minX);
        double d1 = Math.min(this.minY, other.minY);
        double d2 = Math.min(this.minZ, other.minZ);
        double d3 = Math.max(this.maxX, other.maxX);
        double d4 = Math.max(this.maxY, other.maxY);
        double d5 = Math.max(this.maxZ, other.maxZ);
        return new ImmutableAxisAlignedBB(d0, d1, d2, d3, d4, d5);
    }

    @Override
    public ImmutableAxisAlignedBB offset(double x, double y, double z) {
        return new ImmutableAxisAlignedBB(
            this.minX + x,
            this.minY + y,
            this.minZ + z,
            this.maxX + x,
            this.maxY + y,
            this.maxZ + z);
    }

    public ImmutableAxisAlignedBB offset(BlockPos pos) {
        return new ImmutableAxisAlignedBB(
            this.minX + (double) pos.getX(),
            this.minY + (double) pos.getY(),
            this.minZ + (double) pos.getZ(),
            this.maxX + (double) pos.getX(),
            this.maxY + (double) pos.getY(),
            this.maxZ + (double) pos.getZ());
    }

    public ImmutableAxisAlignedBB offset(Vec3 vec) {
        return this.offset(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    // Legacy method back-compatibility
    @Override
    public ImmutableAxisAlignedBB func_111270_a(AxisAlignedBB other) {
        return this.union(other);
    }

    @Override
    public ImmutableAxisAlignedBB getOffsetBoundingBox(double x, double y, double z) {
        return this.offset(x, y, z);
    }

    public ImmutableAxisAlignedBB copy() {
        return new ImmutableAxisAlignedBB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }
}
