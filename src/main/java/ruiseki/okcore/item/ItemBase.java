package ruiseki.okcore.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.capabilities.IItemCapability;
import ruiseki.okcore.client.gui.tooltip.TooltipBuilder;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Item that can hold ExtendedConfigs
 *
 * @author rubensworks
 *
 */
public class ItemBase extends Item implements IItemCapability, IItemSharedTag, IItemTooltip {

    private final TooltipBuilder tooltipBuilder = new TooltipBuilder();

    /**
     * Make a new item instance.
     *
     */
    public ItemBase() {}

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {
        super.addInformation(stack, player, list, advanced);
        LangHelpers.addOptionalInfo(list, getUnlocalizedName(stack));
        this.getTooltipBuilder()
            .build(stack, player, list, advanced);
    }

    @Override
    public TooltipBuilder getTooltipBuilder() {
        return this.tooltipBuilder;
    }
}
