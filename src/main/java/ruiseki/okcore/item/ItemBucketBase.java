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
import ruiseki.okcore.fluid.capability.wrapper.FluidBucketWrapper;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Item food that can hold ExtendedConfigs
 *
 * @author rubensworks
 *
 */
public class ItemBucketBase extends ItemBucket implements IItemCapability, IItemSharedTag {

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
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List<String> list, boolean par4) {
        super.addInformation(itemStack, entityPlayer, list, par4);
        LangHelpers.addOptionalInfo(list, getUnlocalizedName());
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new FluidBucketWrapper(stack);
    }
}
