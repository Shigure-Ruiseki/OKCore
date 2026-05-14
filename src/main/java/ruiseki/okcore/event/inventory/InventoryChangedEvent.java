package ruiseki.okcore.event.inventory;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.common.eventhandler.Event;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import ruiseki.okcore.inventory.ItemStackKey;

public abstract class InventoryChangedEvent extends Event {

    public final EntityPlayer player;
    public final Object2IntMap<ItemStackKey> changes;

    protected InventoryChangedEvent(EntityPlayer player, Object2IntMap<ItemStackKey> changes) {
        this.player = player;
        this.changes = Object2IntMaps.unmodifiable(new Object2IntOpenHashMap<>(changes));
    }

    /**
     * Posted when items have entered player-owned inventory scope.
     */
    public static final class Entered extends InventoryChangedEvent {

        public Entered(EntityPlayer player, Object2IntMap<ItemStackKey> entered) {
            super(player, entered);
        }
    }

    /**
     * Posted when items have left player-owned inventory scope.
     */
    public static final class Left extends InventoryChangedEvent {

        public Left(EntityPlayer player, Object2IntMap<ItemStackKey> left) {
            super(player, left);
        }
    }
}
