package ruiseki.okcore.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.capabilities.IItemCapability;
import ruiseki.okcore.client.gui.tooltip.TooltipBuilder;
import ruiseki.okcore.fluid.capability.wrapper.FluidBucketWrapper;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Item food that can hold ExtendedConfigs
 *
 * @author rubensworks
 *
 */
public class ItemBucketBase extends ItemBucket implements IItemCapability, IItemSharedTag, IItemTooltip {

    private final TooltipBuilder tooltipBuilder = new TooltipBuilder();

    protected boolean canPickUp = true;

    /**
     * Make a new bucket instance.
     *
     * @param block The fluid blockState it can pick up.
     */
    public ItemBucketBase(Block block) {
        super(block);
        setContainerItem(Items.bucket);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {
        super.addInformation(stack, player, list, advanced);
        LangHelpers.addOptionalInfo(list, getUnlocalizedName());
        this.getTooltipBuilder()
            .build(stack, player, list, advanced);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new FluidBucketWrapper(stack);
    }

    @Override
    public TooltipBuilder getTooltipBuilder() {
        return tooltipBuilder;
    }
}
