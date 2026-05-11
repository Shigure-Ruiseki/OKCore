package cofh.api.transport;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public interface IItemDuct {

    ItemStack insertItem(ForgeDirection direction, ItemStack stack);

}
