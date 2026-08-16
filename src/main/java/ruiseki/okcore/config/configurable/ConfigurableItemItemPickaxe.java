package ruiseki.okcore.config.configurable;

import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.capabilities.IItemCapability;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.config.extendedconfig.ItemConfig;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.item.IItemSharedTag;

/**
 * Item that can hold ExtendedConfigs
 *
 * @author rubensworks
 *
 */
public class ConfigurableItemItemPickaxe extends ItemPickaxe
    implements IConfigurableItem, IItemCapability, IItemSharedTag {

    protected ItemConfig eConfig = null;

    /**
     * Make a new item instance.
     *
     * @param eConfig Config for this blockState.
     */
    public ConfigurableItemItemPickaxe(ExtendedConfig<ItemConfig> eConfig, float damageVsEntity,
        ToolMaterial toolMaterial, Set<Block> blocks) {
        super(toolMaterial);
        this.setConfig((ItemConfig) eConfig);
        this.setUnlocalizedName(eConfig.getUnlocalizedName());
        this.setTextureName(
            eConfig.getMod()
                .getModId() + ":"
                + eConfig.getNamedId());
    }

    private void setConfig(ItemConfig eConfig) {
        this.eConfig = eConfig;
    }

    @Override
    public ItemConfig getConfig() {
        return eConfig;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List<String> list, boolean flag) {
        super.addInformation(itemStack, entityPlayer, list, flag);
        LangHelpers.addOptionalInfo(list, getUnlocalizedName(itemStack));
    }

}
