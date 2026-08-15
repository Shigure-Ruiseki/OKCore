package ruiseki.okcore.config.configurable;

import java.util.List;

import net.minecraft.block.BlockLog;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import lombok.experimental.Delegate;
import ruiseki.okcore.block.property.BlockPropertyProviderComponent;
import ruiseki.okcore.block.property.IBlockPropertyProvider;
import ruiseki.okcore.config.extendedconfig.BlockConfig;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.helper.BlockHelpers;

public class ConfigurableBlockLog extends BlockLog implements IConfigurableBlock, IBlockPropertyProvider {

    @Delegate
    protected IBlockPropertyProvider propertyProvider = new BlockPropertyProviderComponent(this);

    protected BlockConfig eConfig = null;
    protected boolean hasGui = false;

    /**
     * Make a new block instance.
     *
     * @param eConfig Config for this block.
     */
    public ConfigurableBlockLog(ExtendedConfig<BlockConfig> eConfig) {
        this.setConfig((BlockConfig) eConfig);
        this.setBlockName(eConfig.getUnlocalizedName());
    }

    @Override
    public boolean hasGui() {
        return hasGui;
    }

    private void setConfig(BlockConfig eConfig) {
        this.eConfig = eConfig;
    }

    @Override
    public BlockConfig getConfig() {
        return eConfig;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
        if (!BlockHelpers.isValidCreativeTab(this, tab)) return;
        list.add(new ItemStack(itemIn, 1, 0));
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        this.field_150167_a = new IIcon[1];
        this.field_150166_b = new IIcon[1];
        this.field_150167_a = new IIcon[] { reg.registerIcon(
            eConfig.getMod()
                .getModId() + ":"
                + eConfig.getNamedId()) };
        this.field_150166_b = new IIcon[] { reg.registerIcon(
            eConfig.getMod()
                .getModId() + ":"
                + eConfig.getNamedId()
                + "_top") };
    }

    public static enum EnumAxis {

        X("x"),
        Y("y"),
        Z("z"),
        NONE("none");

        private final String name;

        private EnumAxis(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }
    }
}
