package ruiseki.okcore.block;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IBlockTooltipProvider {

    void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced);
}
