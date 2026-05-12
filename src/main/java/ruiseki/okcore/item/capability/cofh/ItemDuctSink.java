package ruiseki.okcore.item.capability.cofh;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.logging.log4j.Level;

import cofh.api.transport.IItemDuct;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.item.IImmutableItemStack;
import ruiseki.okcore.item.capability.IItemSink;

public class ItemDuctSink implements IItemSink {

    public final IItemDuct itemDuct;
    private final ForgeDirection side;

    public ItemDuctSink(IItemDuct itemDuct, ForgeDirection side) {
        this.itemDuct = itemDuct;
        this.side = side;
    }

    @Override
    public int store(IImmutableItemStack stack) {
        ItemStack rejected = itemDuct.insertItem(side, stack.toStack());

        if (rejected == null) return 0;

        if (!stack.matches(rejected)) {
            OKCore.okLog(
                Level.ERROR,
                "IItemDuct returned a rejected item that does not match what was inserted: deleting it to prevent dupes ({}x{})",
                rejected.stackSize,
                rejected.getDisplayName(),
                new Exception());

            return 0;
        }

        return rejected.stackSize;
    }
}
