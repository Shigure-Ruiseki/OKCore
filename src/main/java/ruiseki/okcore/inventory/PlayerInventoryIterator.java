package ruiseki.okcore.inventory;

import net.minecraft.entity.player.EntityPlayer;

import ruiseki.okcore.item.capability.wrapper.InvWrapper;

/**
 * Iterate over a player's inventory.
 * 
 * @author rubensworks
 *
 */
public class PlayerInventoryIterator extends InventoryIterator {

    public PlayerInventoryIterator(EntityPlayer player) {
        super(new InvWrapper(player.inventory));
    }
}
