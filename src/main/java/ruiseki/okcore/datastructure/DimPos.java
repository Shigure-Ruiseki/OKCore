package ruiseki.okcore.datastructure;

import java.lang.ref.WeakReference;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Data;
import ruiseki.okcore.helper.MinecraftHelpers;

/**
 * A simple data class for a block position inside a world.
 *
 * @author rubensworks
 */
@Data
public class DimPos implements Comparable<DimPos> {

    private final int dimensionId;
    private final BlockPos blockPos;
    private @Nullable WeakReference<World> worldReference;

    private DimPos(int dimensionId, BlockPos blockPos, @Nullable World world) {
        this.dimensionId = dimensionId;
        this.blockPos = blockPos;
        this.worldReference = (world != null && world.provider.dimensionId == dimensionId) ? new WeakReference<>(world)
            : null;
    }

    public static DimPos of(int dimensionId, int x, int y, int z) {
        return new DimPos(dimensionId, new BlockPos(x, y, z), null);
    }

    public static DimPos of(int dimensionId, BlockPos pos) {
        return new DimPos(dimensionId, pos, null);
    }

    public static DimPos of(World world, int x, int y, int z) {
        return new DimPos(world.provider.dimensionId, new BlockPos(x, y, z), world);
    }

    public static DimPos of(World world, BlockPos pos) {
        return new DimPos(world.provider.dimensionId, pos, world);
    }

    @Nullable
    public World getWorld() {
        return getWorld(false);
    }

    @Nullable
    public World getWorld(boolean forceLoad) {
        if (worldReference != null) {
            World world = worldReference.get();
            if (world != null && world.provider.dimensionId == dimensionId) {
                return world;
            }
        }

        if (MinecraftHelpers.isClientSide()) {
            World clientWorld = getClientWorld();
            if (clientWorld != null) {
                this.worldReference = new WeakReference<>(clientWorld);
                return clientWorld;
            }
            return null;
        }

        World serverWorld = DimensionManager.getWorld(dimensionId);
        if (serverWorld != null) {
            this.worldReference = new WeakReference<>(serverWorld);
        } else {
            this.worldReference = null;
        }
        return serverWorld;
    }

    @SideOnly(Side.CLIENT)
    private @Nullable World getClientWorld() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && mc.theWorld != null && mc.theWorld.provider.dimensionId == dimensionId) {
            return mc.theWorld;
        }
        return null;
    }

    public boolean isLoaded() {
        World world = getWorld(false);
        return world != null && blockPos.isLoaded(world);
    }

    public DimPos withPosition(BlockPos pos) {
        return new DimPos(this.dimensionId, pos, this.worldReference == null ? null : this.worldReference.get());
    }

    @Override
    public int compareTo(DimPos o) {
        int dimCompare = Integer.compare(dimensionId, o.dimensionId);
        if (dimCompare != 0) return dimCompare;

        return MinecraftHelpers.compareBlockPos(blockPos, o.blockPos);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DimPos other)) return false;
        return this.dimensionId == other.dimensionId && this.blockPos.equals(other.blockPos);
    }

    @Override
    public int hashCode() {
        return 31 * dimensionId + blockPos.hashCode();
    }

    public int getX() {
        return blockPos.getX();
    }

    public int getY() {
        return blockPos.getY();
    }

    public int getZ() {
        return blockPos.getZ();
    }
}
