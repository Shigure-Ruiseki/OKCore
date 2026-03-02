package ruiseki.okcore.energy.capability.ok;

import net.minecraft.item.ItemStack;

import ruiseki.okcore.energy.IOKEnergyItem;
import ruiseki.okcore.energy.capability.IEnergyIO;

public class OKEnergyItem implements IEnergyIO {

    private final IOKEnergyItem handler;
    private final ItemStack itemStack;

    public OKEnergyItem(IOKEnergyItem handler, ItemStack itemStack) {
        this.handler = handler;
        this.itemStack = itemStack;

    }

    @Override
    public int extract(int amount, boolean simulate) {
        return handler.extractEnergy(itemStack, amount, simulate);
    }

    @Override
    public int insert(int amount, boolean simulate) {
        return handler.receiveEnergy(itemStack, amount, simulate);
    }

    @Override
    public boolean canConnect() {
        return true;
    }

}
