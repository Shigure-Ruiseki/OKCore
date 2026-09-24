package ruiseki.okcore.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import lombok.experimental.Delegate;
import ruiseki.okcore.block.property.BlockPropertyProviderComponent;
import ruiseki.okcore.block.property.IBlockPropertyProvider;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.BlockStateHelpers;

/**
 * Block without a tile entity with a GUI that can hold ExtendedConfigs.
 * The container and GUI must be set inside the constructor of the extension.
 *
 * @author rubensworks
 *
 */
public abstract class BlockGui extends BlockBase implements IBlockGui {

    @Delegate
    protected IBlockPropertyProvider propertyProvider = new BlockPropertyProviderComponent(this);

    /**
     * Make a new block instance.
     *
     * @param material Material of this blockState.
     */
    public BlockGui(Material material) {
        super(material);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {
        super.onBlockActivated(world, x, y, z, player, side, subX, subY, subZ);

        // Drop through if the player is sneaking
        if (player.isSneaking()) {
            return false;
        }

        BlockPos pos = new BlockPos(x, y, z);
        Vec3 hitVec = Vec3.createVectorHelper(x + subX, y + subY, z + subZ);
        MovingObjectPosition rayTraceResult = new MovingObjectPosition(x, y, z, side, hitVec);

        return IBlockGui.onBlockActivatedHook(
            this,
            this::getGuiProvider,
            BlockStateHelpers.getState(world, pos),
            world,
            pos,
            player,
            rayTraceResult);
    }
}
