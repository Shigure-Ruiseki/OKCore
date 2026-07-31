package ruiseki.okcore.config.configurable;

import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockSapling;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.TerrainGen;

import lombok.experimental.Delegate;
import ruiseki.okcore.block.property.BlockPropertyProviderComponent;
import ruiseki.okcore.block.property.IBlockPropertyProvider;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.world.gen.WorldGeneratorTree;

public class ConfigurableBlockSapling extends BlockSapling implements IConfigurableBlock, IBlockPropertyProvider {

    @Delegate
    protected IBlockPropertyProvider propertyProvider = new BlockPropertyProviderComponent(this);

    @SuppressWarnings("rawtypes")
    protected ExtendedConfig eConfig = null;
    protected boolean hasGui = false;

    private WorldGeneratorTree treeGenerator;

    /**
     * Make a new blockState instance.
     *
     * @param eConfig       Config for this blockState.
     * @param material      Material of this blockState.
     * @param treeGenerator The world generator of the tree.
     */
    @SuppressWarnings({ "rawtypes" })
    public ConfigurableBlockSapling(ExtendedConfig eConfig, Material material, WorldGeneratorTree treeGenerator) {
        this.setConfig(eConfig);
        this.setBlockName(eConfig.getUnlocalizedName());
        this.setBlockTextureName(
            eConfig.getMod()
                .getModId() + ":"
                + eConfig.getNamedId());
        this.treeGenerator = treeGenerator;
        setStepSound(soundTypeGrass);
    }

    @Override
    public boolean hasGui() {
        return hasGui;
    }

    @SuppressWarnings("rawtypes")
    private void setConfig(ExtendedConfig eConfig) {
        this.eConfig = eConfig;
    }

    @Override
    public ExtendedConfig<?> getConfig() {
        return eConfig;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, 0));
    }

    @Override
    public void func_149879_c(World world, int x, int y, int z, Random rand) {
        if (!TerrainGen.saplingGrowTree(world, rand, x, y, z)) return;
        if (world.isRemote) {
            return;
        }

        world.setBlockToAir(x, y, z);

        if (!treeGenerator.growTree(world, rand, x, y, z)) {
            world.setBlock(x, y, z, this, 0, 4);
        }
    }

    @Override
    public int damageDropped(int meta) {
        return 0;
    }
}
