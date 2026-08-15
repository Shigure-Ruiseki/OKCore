package ruiseki.okcore.config.configurable;

import java.util.Random;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import lombok.experimental.Delegate;
import ruiseki.okcore.block.property.BlockPropertyProviderComponent;
import ruiseki.okcore.block.property.IBlockPropertyProvider;
import ruiseki.okcore.config.extendedconfig.BlockDoorConfig;

/**
 * Door block that can hold ExtendedConfigs.
 */
public class ConfigurableBlockDoor extends BlockDoor implements IConfigurableBlock {

    @Delegate
    protected IBlockPropertyProvider propertyProvider = new BlockPropertyProviderComponent(this);

    public Item item;

    protected BlockDoorConfig eConfig = null;
    protected boolean hasGui = false;

    /**
     * Make a new block instance.
     *
     * @param config   Config for this block.
     * @param material The door material.
     */
    public ConfigurableBlockDoor(BlockDoorConfig config, Material material) {
        super(material);
        setConfig(config);
        setBlockName(config.getUnlocalizedName());
        disableStats();
    }

    @Override
    public Item getItem(World world, int x, int y, int z) {
        return item != null ? item : super.getItem(world, x, y, z);
    }

    @Override
    public Item getItemDropped(int meta, Random rand, int fortune) {
        return (meta & 8) != 0 ? null : this.item;
    }

    @Override
    public ConfigurableBlockDoor setStepSound(SoundType sound) {
        super.setStepSound(sound);
        return this;
    }

    @Override
    public boolean hasGui() {
        return hasGui;
    }

    private void setConfig(BlockDoorConfig eConfig) {
        this.eConfig = eConfig;
    }

    @Override
    public BlockDoorConfig getConfig() {
        return eConfig;
    }
}
