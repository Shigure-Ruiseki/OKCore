
package ruiseki.okcore.event.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import ruiseki.okcore.inventory.ClickType;

/**
 * Fired when a player interacts with a slot in a container.
 *
 * <p>This event is fired on both the client and server during
 * inventory click processing, provided the corresponding
 * client and server hooks are installed.</p>
 *
 * <p>The event has two phases:</p>
 * <ul>
 *     <li>{@link Pre} - Before the original
 *     {@link Container#slotClick(int, int, int, EntityPlayer)}
 *     operation is executed.</li>
 *     <li>{@link Post} - After the slot click operation has
 *     completed or has been replaced by a canceled Pre event.</li>
 * </ul>
 *
 * <p>Handlers may inspect or modify inventory state during
 * the Pre phase. If the event is not canceled, the original
 * slot click operation executes using the modified state.</p>
 *
 * <p>The Post phase occurs before server-side transaction
 * validation and inventory synchronization have completed.</p>
 */
public abstract class SlotClickEvent extends Event {

    protected final Container container;
    protected final EntityPlayer player;
    protected final int slotId;
    protected final int mouseButton;
    protected final ClickType mode;

    protected SlotClickEvent(Container container, EntityPlayer player,
                             int slotId, int mouseButton, ClickType mode) {
        this.container = container;
        this.player = player;
        this.slotId = slotId;
        this.mouseButton = mouseButton;
        this.mode = mode;
    }

    /**
     * Fired before the original container slot click operation.
     *
     * <p>Handlers may modify the slot, cursor stack, or other
     * inventory state without canceling this event. The original
     * operation will then execute using the updated state.</p>
     *
     * <p>Canceling this event prevents the original slot click
     * operation from executing. The value provided by
     * {@link #setResultStack(ItemStack)} is returned instead.</p>
     *
     * <p>Setting a result stack does not automatically cancel
     * the event. If the event is not canceled, the configured
     * result stack is ignored.</p>
     *
     * <p>Canceling the event does not cancel the entire click
     * packet or bypass transaction validation.</p>
     */
    @Cancelable
    public static class Pre extends SlotClickEvent {

        private ItemStack resultStack;

        public Pre(Container container, EntityPlayer player,
                   int slotId, int mouseButton, ClickType mode) {
            super(container, player, slotId, mouseButton, mode);
        }

        /**
         * Sets the result returned when this event is canceled.
         *
         * <p>This method does not cancel the event automatically.
         * Call {@link #setCanceled(boolean)} with {@code true}
         * to skip the original slot click operation.</p>
         *
         * <p>If the event is not canceled, this value is ignored
         * and the original operation determines the result.</p>
         *
         * @param resultStack the replacement result, or {@code null}
         */
        public void setResultStack(ItemStack resultStack) {
            this.resultStack = resultStack;
        }

        /**
         * Returns the configured replacement result.
         *
         * @return the replacement stack, or {@code null}
         */
        public ItemStack getResultStack() {
            return resultStack;
        }
    }

    /**
     * Fired after a container slot click operation has completed.
     *
     * <p>This event is fired regardless of whether the Pre event
     * was canceled. When canceled, the result comes from the
     * Pre event. Otherwise, it comes from the original slot
     * click operation.</p>
     *
     * <p>This event is not cancelable and does not allow the
     * returned result to be replaced.</p>
     *
     * <p>On the server, this event is fired before transaction
     * confirmation and the associated inventory synchronization.</p>
     */
    public static class Post extends SlotClickEvent {

        private final ItemStack resultStack;

        public Post(Container container, EntityPlayer player,
                    int slotId, int mouseButton, ClickType mode,
                    ItemStack resultStack) {
            super(container, player, slotId, mouseButton, mode);
            this.resultStack = resultStack;
        }

        /**
         * Returns the result of the slot click operation.
         *
         * @return the resulting stack, or {@code null}
         */
        public ItemStack getResultStack() {
            return resultStack;
        }
    }

    /**
     * Returns the container in which the click occurred.
     *
     * @return the active container
     */
    public Container getContainer() {
        return container;
    }

    /**
     * Returns the player performing the interaction.
     *
     * @return the interacting player
     */
    public EntityPlayer getPlayer() {
        return player;
    }

    /**
     * Returns the clicked slot index within the container.
     *
     * <p>Negative indices may represent special interactions,
     * such as clicking outside the container.</p>
     *
     * @return the container slot index
     */
    public int getSlotId() {
        return slotId;
    }

    /**
     * Returns the raw mouse button or action parameter
     * associated with the click.
     *
     * <p>Its meaning depends on the click mode.</p>
     *
     * @return the raw click button parameter
     */
    public int getMouseButton() {
        return mouseButton;
    }

    /**
     * Returns the type of inventory click operation.
     *
     * @return the click type
     */
    public ClickType getMode() {
        return mode;
    }
}
