package ruiseki.okcore.config.configurable;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import lombok.experimental.Delegate;
import ruiseki.okcore.block.property.BlockPropertyProviderComponent;
import ruiseki.okcore.block.property.IBlockPropertyProvider;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.inventory.IGuiContainerProvider;

/**
 * Block without a tile entity with a GUI that can hold ExtendedConfigs.
 * The container and GUI must be set inside the constructor of the extension.
 *
 * @author rubensworks
 *
 */
public abstract class ConfigurableBlockGui extends ConfigurableBlock
    implements IGuiContainerProvider, IBlockPropertyProvider {

    private int guiID;

    @Delegate
    protected IBlockPropertyProvider propertyProvider = new BlockPropertyProviderComponent(this);

    /**
     * Make a new block instance.
     *
     * @param eConfig  Config for this blockState.
     * @param material Material of this blockState.
     */
    @SuppressWarnings({ "rawtypes" })
    public ConfigurableBlockGui(ExtendedConfig eConfig, Material material) {
        super(eConfig, material);
        this.hasGui = true;
        if (hasGui()) {
            this.guiID = Helpers.getNewId(eConfig.getMod(), Helpers.IDType.GUI);
        }
    }

    @Override
    public int getGuiID() {
        return this.guiID;
    }

    @Override
    public ModBase getModGui() {
        return getConfig().getMod();
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
            player.openGui(getConfig().getMod(), getGuiID(), world, x, y, z);
        }

        return true;
    }
}
