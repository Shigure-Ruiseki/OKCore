package ruiseki.okcore.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

import ruiseki.okcore.block.IBlock;
import ruiseki.okcore.block.IBlockRarityProvider;
import ruiseki.okcore.block.IBlockTooltipProvider;

public class ItemBlockOK extends ItemBlockWithMetadata {

    protected IBlockRarityProvider rarityProvider = null;
    protected IBlockTooltipProvider tooltipProvider = null;

    public ItemBlockOK(Block block) {
        super(block, block);

        if (block instanceof IBlock iBlock) {
            this.hasSubtypes = iBlock.isHasSubtypes();
            this.setHasSubtypes(iBlock.isHasSubtypes());
        }

        if (block instanceof IBlockRarityProvider rarity) this.rarityProvider = rarity;
        if (block instanceof IBlockTooltipProvider tooltip) this.tooltipProvider = tooltip;

    }

    @Override
    public int getMetadata(final int meta) {
        if (this.hasSubtypes) return meta;
        return 0;
    }

    @Override
    public EnumRarity getRarity(ItemStack itemStack) {
        if (rarityProvider != null) return rarityProvider.getRarity(itemStack);
        return super.getRarity(itemStack);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        super.addInformation(stack, player, list, flag);
        if (tooltipProvider != null) tooltipProvider.addInformation(stack, player, list, flag);
    }
}
