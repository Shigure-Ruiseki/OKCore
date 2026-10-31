package ruiseki.okcore.inventory;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import baubles.api.BaublesApi;
import cpw.mods.fml.common.Optional;
import ruiseki.okcore.enums.Mods;

/**
 * Iterate over a player's inventory and any other attached inventory like baubles.
 *
 * @author rubensworks
 */
@Optional.Interface(modid = "Baubles", iface = "baubles.api.IBauble")
public class PlayerExtendedInventoryIterator implements Iterator<ItemStack> {

    private final PlayerInventoryIterator innerIt;
    private int maxBaublesSize;
    private int baublesIterator;
    private boolean lastFromBaubles;

    public PlayerExtendedInventoryIterator(EntityPlayer player) {
        innerIt = new PlayerInventoryIterator(player);

        if (Mods.Baubles.isModLoaded()) {
            setBaublesData();
        }
    }

    @Override
    public boolean hasNext() {
        return innerIt.hasNext() || baublesIterator < maxBaublesSize;
    }

    @Override
    public ItemStack next() {
        if (innerIt.hasNext()) {
            lastFromBaubles = false;
            return innerIt.next();
        }

        if (baublesIterator < maxBaublesSize) {
            lastFromBaubles = true;
            return getBaublesStack(baublesIterator++);
        }

        throw new NoSuchElementException();
    }

    @Optional.Method(modid = "Baubles")
    protected ItemStack getBaublesStack(int index) {
        return BaublesApi.getBaubles(innerIt.getPlayer())
            .getStackInSlot(index);
    }

    @Optional.Method(modid = "Baubles")
    protected void setBaublesStack(int index, ItemStack stack) {
        BaublesApi.getBaubles(innerIt.getPlayer())
            .setInventorySlotContents(index, stack);
    }

    @Optional.Method(modid = "Baubles")
    protected void setBaublesData() {
        maxBaublesSize = BaublesApi.getBaubles(innerIt.getPlayer())
            .getSizeInventory();
        baublesIterator = 0;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public void replace(ItemStack stack) {
        if (lastFromBaubles) {
            setBaublesStack(baublesIterator - 1, stack);
        } else {
            innerIt.replace(stack);
        }
    }
}
