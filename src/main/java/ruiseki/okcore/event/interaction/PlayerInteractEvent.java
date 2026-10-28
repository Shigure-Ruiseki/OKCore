
package ruiseki.okcore.event.interaction;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerEvent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.base.Preconditions;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.relauncher.Side;
import ruiseki.okcore.datastructure.BlockPos;

/**
 * Fired when a player interacts with a block, entity, or item.
 *
 * <p>
 * All implemented events are posted to
 * {@link MinecraftForge#EVENT_BUS}.
 * </p>
 *
 * <p>
 * Events are dispatched when their corresponding player
 * interaction occurs.
 * </p>
 */
public abstract class PlayerInteractEvent extends PlayerEvent {

    private final BlockPos pos;

    @Nullable
    private final ForgeDirection face;

    private InteractionResult cancellationResult = InteractionResult.PASS;

    protected PlayerInteractEvent(EntityPlayer player, BlockPos pos, @Nullable ForgeDirection face) {
        super(Preconditions.checkNotNull(player, "Player cannot be null"));

        this.pos = Preconditions.checkNotNull(pos, "Position cannot be null");

        this.face = face;
    }

    /**
     * Fired when a player right-clicks a specific point
     * on an entity.
     *
     * <p>
     * The local interaction position is relative to
     * the target entity, not world coordinates.
     * </p>
     *
     * <p>
     * The event is cancelable.
     * </p>
     */
    @Cancelable
    public static class EntityInteractSpecific extends PlayerInteractEvent {

        private final Vec3 localPos;
        private final Entity target;

        public EntityInteractSpecific(EntityPlayer player, Entity target, Vec3 localPos) {
            super(player, new BlockPos(target), null);
            this.target = Preconditions.checkNotNull(target, "Target cannot be null");
            this.localPos = Preconditions.checkNotNull(localPos, "Local position cannot be null");
        }

        /**
         * Returns the interaction position relative
         * to the target entity.
         *
         * @return Local interaction coordinates.
         */
        public Vec3 getLocalPos() {
            return localPos;
        }

        /**
         * @return The entity being interacted with.
         */
        public Entity getTarget() {
            return target;
        }
    }

    /**
     * Fired when a player right-clicks an entity.
     *
     * <p>
     * Allows listeners to intercept entity interactions.
     * </p>
     *
     * <p>
     * Canceling this event cancels the corresponding
     * interaction.
     * </p>
     */
    @Cancelable
    public static class EntityInteract extends PlayerInteractEvent {

        private final Entity target;

        public EntityInteract(EntityPlayer player, Entity target) {
            super(player, new BlockPos(target), null);

            this.target = Preconditions.checkNotNull(target, "Target cannot be null");
        }

        /**
         * @return The entity being interacted with.
         */
        public Entity getTarget() {
            return target;
        }
    }

    /**
     * Fired when a player right-clicks a block.
     *
     * <p>
     * Controls whether block activation and item
     * usage are permitted.
     * </p>
     *
     * <p>
     * Changes to useBlock and useItem are forwarded
     * to the underlying interaction.
     * </p>
     *
     * <p>
     * Canceling the event denies both operations.
     * </p>
     */
    @Cancelable
    public static class RightClickBlock extends PlayerInteractEvent {

        private Result useBlock = Result.DEFAULT;
        private Result useItem = Result.DEFAULT;

        public RightClickBlock(EntityPlayer player, BlockPos pos, @Nullable ForgeDirection face) {
            super(player, pos, face);
        }

        /**
         * @return Whether block activation is allowed.
         */
        public Result getUseBlock() {
            return useBlock;
        }

        /**
         * @return Whether item usage is allowed.
         */
        public Result getUseItem() {
            return useItem;
        }

        /**
         * Sets the block activation result.
         *
         * @param result ALLOW, DEFAULT, or DENY.
         */
        public void setUseBlock(Result result) {
            this.useBlock = Preconditions.checkNotNull(result);
        }

        /**
         * Sets the item usage result.
         *
         * @param result ALLOW, DEFAULT, or DENY.
         */
        public void setUseItem(Result result) {
            this.useItem = Preconditions.checkNotNull(result);
        }

        @Override
        public void setCanceled(boolean canceled) {
            super.setCanceled(canceled);

            if (canceled) {
                useBlock = Result.DENY;
                useItem = Result.DENY;
            }
        }
    }

    /**
     * Fired before a player uses the currently held item
     * without targeting a block or entity.
     *
     * <p>
     * The event is cancelable and allows item usage
     * to be controlled through its result.
     * </p>
     */
    @Cancelable
    public static class RightClickItem extends PlayerInteractEvent {

        private Result useItem = Result.DEFAULT;

        public RightClickItem(EntityPlayer player) {
            super(player, new BlockPos(player), null);
        }

        /**
         * @return Whether item usage is allowed.
         */
        public Result getUseItem() {
            return useItem;
        }

        /**
         * Sets whether item usage is allowed.
         */
        public void setUseItem(Result result) {
            this.useItem = Preconditions.checkNotNull(result);
        }

        @Override
        public void setCanceled(boolean canceled) {
            super.setCanceled(canceled);

            if (canceled) {
                useItem = Result.DENY;
            }
        }
    }

    /**
     * Fired on the client when a player right-clicks
     * empty space with an empty hand.
     *
     * <p>
     * This event is not cancelable.
     * </p>
     *
     * <p>
     * The server cannot detect this interaction
     * without a custom network packet.
     * </p>
     */
    public static class RightClickEmpty extends PlayerInteractEvent {

        public RightClickEmpty(EntityPlayer player) {
            super(player, new BlockPos(player), null);
        }
    }

    /**
     * Fired when a player left-clicks a block.
     *
     * <p>
     * Canceling the event prevents the corresponding
     * Forge interaction from proceeding.
     * </p>
     *
     * <p>
     * Use {@link #getAction()} to determine the phase
     * of the block-breaking interaction.
     * </p>
     *
     * <p>
     * The useBlock and useItem results control block
     * attack and item mining behavior when supported
     * by the interaction handler.
     * </p>
     */
    @Cancelable
    public static class LeftClickBlock extends PlayerInteractEvent {

        private Result useBlock = Result.DEFAULT;
        private Result useItem = Result.DEFAULT;

        private final Action action;

        public LeftClickBlock(EntityPlayer player, BlockPos pos, @Nullable ForgeDirection face) {
            this(player, pos, face, Action.START);
        }

        public LeftClickBlock(EntityPlayer player, BlockPos pos, @Nullable ForgeDirection face, Action action) {
            super(player, pos, face);

            this.action = Preconditions.checkNotNull(action, "Action cannot be null");
        }

        /**
         * @return Whether block attack logic is allowed.
         */
        public Result getUseBlock() {
            return useBlock;
        }

        /**
         * @return Whether item mining logic is allowed.
         */
        public Result getUseItem() {
            return useItem;
        }

        /**
         * @return The current mining interaction action.
         */
        @NotNull
        public Action getAction() {
            return action;
        }

        public void setUseBlock(Result result) {
            this.useBlock = Preconditions.checkNotNull(result);
        }

        public void setUseItem(Result result) {
            this.useItem = Preconditions.checkNotNull(result);
        }

        @Override
        public void setCanceled(boolean canceled) {
            super.setCanceled(canceled);

            if (canceled) {
                useBlock = Result.DENY;
                useItem = Result.DENY;
            }
        }

        /**
         * Represents the phase of a block-breaking
         * interaction.
         */
        public enum Action {

            /** Player starts attacking a block. */
            START,

            /** Player finishes breaking a block. */
            STOP,

            /** Player aborts the current block attack. */
            ABORT,

            /** Client continues holding the attack button. */
            CLIENT_HOLD
        }
    }

    /**
     * Fired on the client when a player left-clicks
     * empty space.
     *
     * <p>
     * This event is not cancelable.
     * </p>
     *
     * <p>
     * The server is not notified of this action
     * unless a custom packet is sent.
     * </p>
     */
    public static class LeftClickEmpty extends PlayerInteractEvent {

        public LeftClickEmpty(EntityPlayer player) {
            super(player, new BlockPos(player), null);
        }
    }

    /**
     * Returns the item currently held by the player.
     *
     * <p>
     * Returns null if the player is not holding an item.
     * </p>
     *
     * @return The held item, or null.
     */
    @Nullable
    public ItemStack getItemStack() {
        return entityPlayer.getCurrentEquippedItem();
    }

    /**
     * @return The player performing the interaction.
     */
    @NotNull
    public EntityPlayer getEntity() {
        return entityPlayer;
    }

    /**
     * Returns the position associated with the interaction.
     *
     * <p>
     * For block interactions, this is the target block.
     * For entity interactions, this is the target entity's
     * position. Otherwise, it is the player's position.
     * </p>
     *
     * @return The interaction position.
     */
    @NotNull
    public BlockPos getPos() {
        return pos;
    }

    /**
     * @return The clicked block face, or null for
     *         non-block interactions.
     */
    @Nullable
    public ForgeDirection getFace() {
        return face;
    }

    /**
     * @return The world containing the interaction.
     */
    @NotNull
    public World getWorld() {
        return entityPlayer.worldObj;
    }

    /**
     * @return The logical side handling the interaction.
     */
    @NotNull
    public Side getSide() {
        return getWorld().isRemote ? Side.CLIENT : Side.SERVER;
    }

    /**
     * Returns the OkCore cancellation result.
     *
     * <p>
     * Defaults to PASS.
     * </p>
     *
     * <p>
     * The result is used by interaction handlers that
     * support cancellation results.
     * </p>
     *
     * @return The cancellation result.
     */
    @NotNull
    public InteractionResult getCancellationResult() {
        return cancellationResult;
    }

    /**
     * Sets the result used by OkCore interaction hooks
     * when this event is canceled.
     *
     * <p>
     * The result is evaluated when the interaction
     * is canceled, where supported.
     * </p>
     *
     * @param result The cancellation result.
     */
    public void setCancellationResult(InteractionResult result) {
        this.cancellationResult = Preconditions.checkNotNull(result);
    }
}
