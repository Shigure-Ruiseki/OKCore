package ruiseki.okcore.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.experimental.Delegate;
import ruiseki.okcore.block.property.BlockPropertyProviderComponent;
import ruiseki.okcore.block.property.IBlockPropertyProvider;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.BlockStateHelpers;
import ruiseki.okcore.inventory.container.TileInventoryContainerConfigurable;
import ruiseki.okcore.tileentity.TileEntityOK;

/**
 * Block with a tile entity and a custom GUI framework.
 */
public abstract class BlockTileGui extends BlockTile implements IBlockGui {

    @Delegate
    protected IBlockPropertyProvider propertyProvider = new BlockPropertyProviderComponent(this);

    public BlockTileGui(Material material, Class<? extends TileEntityOK> tileEntity) {
        super(material, tileEntity);
    }

    @Override
    public boolean isNormalCube() {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {
        if (super.onBlockActivated(world, x, y, z, player, side, subX, subY, subZ)) {
            return true;
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

    @Override
    protected void onPostBlockDestroyed(World world, int x, int y, int z) {
        super.onPostBlockDestroyed(world, x, y, z);

        if (world.isRemote) {
            tryCloseClientGui();
        }
    }

    /**
     * Safely closes open container on the client if the tile entity is invalidated.
     */
    @SideOnly(Side.CLIENT)
    public void tryCloseClientGui() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc != null && mc.thePlayer != null) {
            if (mc.thePlayer.openContainer instanceof TileInventoryContainerConfigurable<?>container) {
                if (container.getTile() == null || container.getTile()
                    .isInvalid()) {
                    mc.thePlayer.closeScreen();
                }
            }
        }
    }
}
