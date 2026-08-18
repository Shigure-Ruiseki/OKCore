package ruiseki.okcore.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.experimental.Delegate;
import ruiseki.okcore.block.property.BlockPropertyProviderComponent;
import ruiseki.okcore.block.property.IBlockPropertyProvider;
import ruiseki.okcore.inventory.IGuiContainerProvider;
import ruiseki.okcore.inventory.container.TileInventoryContainerConfigurable;
import ruiseki.okcore.tileentity.TileEntityOK;

/**
 * Block with a tile entity with a GUI that can hold ExtendedConfigs.
 * The container and GUI must be set inside the constructor of the extension.
 *
 * @author rubensworks
 *
 */
public abstract class BlockTileGui extends BlockTile implements IGuiContainerProvider {

    @Delegate
    protected IBlockPropertyProvider propertyProvider = new BlockPropertyProviderComponent(this);

    /**
     * Make a new blockState instance.
     *
     * @param material   Material of this blockState.
     * @param tileEntity The class of the tile entity this blockState holds.
     */
    public BlockTileGui(Material material, Class<? extends TileEntityOK> tileEntity) {
        super(material, tileEntity);
        this.hasGui = true;
    }

    @Override
    public boolean isNormalCube() {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {
        super.onBlockActivated(world, x, y, z, player, side, subX, subY, subZ);

        // Drop through if the player is sneaking
        if (player.isSneaking()) {
            return false;
        }

        if (!world.isRemote && hasGui()) {
            player.openGui(getModGui(), getGuiID(), world, x, y, z);
        }

        return true;
    }

    @Override
    protected void onPostBlockDestroyed(World world, int x, int y, int z) {
        super.onPostBlockDestroyed(world, x, y, z);

        // Close the GUI if it is open
        if (world.isRemote) {
            tryCloseClientGui(world);
        }
    }

    /**
     * Try to close the gui at client side.
     *
     * @param world The world.
     */
    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public void tryCloseClientGui(World world) {
        if (Minecraft
            .getMinecraft().thePlayer.openContainer instanceof TileInventoryContainerConfigurable<?>container) {
            if (container.getTile() == null || container.getTile()
                .isInvalid()) {
                Minecraft.getMinecraft().thePlayer.closeScreen();
            }
        }
    }
}
