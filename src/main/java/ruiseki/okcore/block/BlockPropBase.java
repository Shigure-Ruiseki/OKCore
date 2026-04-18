package ruiseki.okcore.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPropBase extends Block {

    protected final BlockProperties properties;

    public BlockPropBase(BlockProperties properties) {
        super(properties.material);
        this.properties = properties;

        this.setHardness(properties.hardness);
        this.setResistance(properties.resistance);
        this.setStepSound(properties.soundType);
        this.setLightLevel(properties.lightEmission / 15.0F);
        this.slipperiness = properties.slipperiness;
        this.setTickRandomly(properties.isRandomlyTicking);
        if (properties.harvestTool != null) {
            this.setHarvestLevel(properties.harvestTool, properties.harvestLevel);
        }
    }

    @Override
    public MapColor getMapColor(int meta) {
        return properties.mapColor.apply(meta);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        if (!this.properties.hasCollision) return null;
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public boolean isOpaqueCube() {
        return this.properties.canOcclude;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return this.properties.canOcclude;
    }

    @Override
    public boolean isNormalCube() {
        return this.properties.canOcclude;
    }

    @Override
    public boolean isAir(IBlockAccess world, int x, int y, int z) {
        return this.properties.isAir;
    }

    @Override
    public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
        return this.properties.replaceable;
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        if (properties.spawnPredicate != null) return properties.spawnPredicate.canSpawn(world, x, y, z, type);
        return super.canCreatureSpawn(type, world, x, y, z);
    }

    @Override
    public int getMobilityFlag() {
        return switch (properties.pushReaction) {
            case DESTROY -> 1;
            case BLOCK -> 2;
            default -> 0;
        };
    }
}
