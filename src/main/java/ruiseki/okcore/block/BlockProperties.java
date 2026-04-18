package ruiseki.okcore.block;

import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockProperties {

    Function<Integer, MapColor> mapColor = (p_284884_) -> MapColor.airColor;
    public Material material = Material.rock;
    public int lightEmission;
    public boolean canOcclude;
    public boolean hasCollision;
    public PushReaction pushReaction;
    public float slipperiness;
    public SoundType soundType;
    public ISpawnPredicate spawnPredicate;
    public float hardness;
    public float resistance;
    public boolean isRandomlyTicking;
    public boolean replaceable;
    public boolean requiresCorrectToolForDrops;
    public boolean isAir;
    public String harvestTool;
    public int harvestLevel;

    private BlockProperties() {
        this.soundType = Block.soundTypeStone;
        this.lightEmission = 0;
        this.slipperiness = 0.6F;
        this.canOcclude = true;
        this.hasCollision = true;
        this.pushReaction = PushReaction.NORMAL;
        this.spawnPredicate = (world, x, y, z, type) -> World.doesBlockHaveSolidTopSurface(world, x, y, z)
            && world.getLightBrightnessForSkyBlocks(x, y + 1, z, 0) < 14;
        this.hardness = 1.0F;
        this.resistance = 1.0F;
        this.isRandomlyTicking = false;
        this.replaceable = false;
        this.requiresCorrectToolForDrops = false;
        this.isAir = false;
        this.harvestTool = null;
        this.harvestLevel = -1;
    }

    public static BlockProperties of() {
        return new BlockProperties();
    }

    public static BlockProperties of(Material material) {
        BlockProperties properties = new BlockProperties();
        properties.material = material;
        return properties;
    }

    public BlockProperties mapColor(MapColor mapColor) {
        this.mapColor = (meta) -> mapColor;
        return this;
    }

    public BlockProperties mapColor(Function<Integer, MapColor> mapColor) {
        this.mapColor = mapColor;
        return this;
    }

    public BlockProperties noCollission() {
        this.hasCollision = false;
        this.canOcclude = false;
        return this;
    }

    public BlockProperties noOcclusion() {
        this.canOcclude = false;
        return this;
    }

    public BlockProperties friction(float friction) {
        this.slipperiness = friction;
        return this;
    }

    public BlockProperties slipperiness(float slipperiness) {
        this.slipperiness = slipperiness;
        return this;
    }

    public BlockProperties sound(SoundType soundType) {
        this.soundType = soundType;
        return this;
    }

    public BlockProperties lightLevel(int lightEmission) {
        this.lightEmission = lightEmission;
        return this;
    }

    public BlockProperties strength(float hardness, float resistance) {
        this.destroyTime(hardness)
            .explosionResistance(resistance);
        return this;
    }

    public BlockProperties instabreak() {
        return this.strength(0.0F);
    }

    public BlockProperties strength(float strength) {
        this.strength(strength, strength);
        return this;
    }

    public BlockProperties randomTicks() {
        this.isRandomlyTicking = true;
        return this;
    }

    public BlockProperties pushReaction(PushReaction pushReaction) {
        this.pushReaction = pushReaction;
        return this;
    }

    public BlockProperties isAir() {
        this.isAir = true;
        return this;
    }

    public BlockProperties isValidSpawn(ISpawnPredicate spawnPredicate) {
        this.spawnPredicate = spawnPredicate;
        return this;
    }

    public BlockProperties requiresCorrectToolForDrops() {
        this.requiresCorrectToolForDrops = true;
        return this;
    }

    public BlockProperties destroyTime(float hardness) {
        this.hardness = hardness;
        return this;
    }

    public BlockProperties explosionResistance(float resistance) {
        this.resistance = Math.max(0.0F, resistance);
        return this;
    }

    public BlockProperties replaceable() {
        this.replaceable = true;
        return this;
    }

    public BlockProperties harvestTool(String tool, int level) {
        this.harvestTool = tool;
        this.harvestLevel = level;
        return this;
    }

    public interface ISpawnPredicate {

        boolean canSpawn(IBlockAccess world, int x, int y, int z, EnumCreatureType type);
    }
}
