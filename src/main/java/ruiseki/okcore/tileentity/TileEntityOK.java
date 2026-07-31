package ruiseki.okcore.tileentity;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.experimental.Delegate;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityCache;
import ruiseki.okcore.capabilities.CapabilityDispatcher;
import ruiseki.okcore.capabilities.ICapabilityInternal;
import ruiseki.okcore.capabilities.ICapabilitySerializable;
import ruiseki.okcore.config.configurable.ConfigurableBlockContainer;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.persist.nbt.INBTProvider;
import ruiseki.okcore.persist.nbt.NBTPersist;
import ruiseki.okcore.persist.nbt.NBTProviderComponent;

public abstract class TileEntityOK extends TileEntity implements INBTProvider, ICapabilitySerializable {

    private static final int UPDATE_BACKOFF_TICKS = 1;

    @NBTPersist
    private Boolean rotatable = false;
    @NBTPersist
    private ForgeDirection rotation = ForgeDirection.NORTH;
    @Delegate
    private final INBTProvider nbtProvider = new NBTProviderComponent(this);

    private boolean shouldSendUpdate = false;
    private int sendUpdateBackoff = 0;
    private final boolean ticking;
    protected final CapabilityCache capabilityCache = new CapabilityCache();

    private CapabilityDispatcher capabilities;
    public BlockPos pos = BlockPos.ORIGIN;

    public TileEntityOK() {
        this.sendUpdateBackoff = (int) (Math.random() * UPDATE_BACKOFF_TICKS);
        this.ticking = this instanceof ITickingTile;
        if (this instanceof ICapabilityInternal internal) this.capabilities = internal.getCapabilities();
    }

    /**
     * for dormant chunk cache.
     */
    public void onChunkLoad() {
        if (this.isInvalid()) {
            this.validate();
        }
    }

    @Override
    public final boolean canUpdate() {
        return true;
    }

    @Override
    public boolean shouldRefresh(Block oldBlock, Block newBlock, int oldMeta, int newMeta, World world, int x, int y,
        int z) {
        return (oldBlock != newBlock);
    }

    protected boolean isTicking() {
        return ticking;
    }

    /**
     * Send a world update for the coordinates of this tile entity.
     * This will always send lag-safe updates, so calling this many times per tick will
     * not cause multiple packets to be sent, more info in the class javadoc.
     */
    public final void sendUpdate() {
        if (!isTicking()) {
            throw new RuntimeException("If you want to update, you must implement ITickingTile. This is a mod error.");
        }
        shouldSendUpdate = true;
    }

    /**
     * Send an immediate world update for the coordinates of this tile entity.
     * This does the same as {@link TileEntityOK#sendUpdate()} but will
     * ignore the update backoff.
     */
    public final void sendImmediateUpdate() {
        sendUpdate();
        sendUpdateBackoff = 0;
    }

    @Override
    public final void updateEntity() {
        if (isTicking()) {
            ((ITickingTile) this).update();
        }
    }

    /**
     * Do not override this method (you won't even be able to do so).
     * Use updateTileEntity() instead.
     */
    private void updateTicking() {
        doUpdate();
        updateTileEntity();
        trySendActualUpdate();
    }

    /**
     * Override this method instead of {@link TileEntityOK#updateEntity()}.
     * This method is called each tick.
     */
    @Deprecated
    protected void doUpdate() {}

    /**
     * Override this method instead of {@link TileEntityOK#updateTicking()}.
     * This method is called each tick.
     */
    protected void updateTileEntity() {

    }

    private void trySendActualUpdate() {
        sendUpdateBackoff--;
        if (sendUpdateBackoff <= 0) {
            sendUpdateBackoff = getUpdateBackoffTicks();

            if (shouldSendUpdate) {
                shouldSendUpdate = false;

                beforeSendUpdate();
                onSendUpdate();
                afterSendUpdate();
            }
        }
    }

    /**
     * Called when an update will is sent.
     * This contains the logic to send the update, so make sure to call the super!
     */
    protected void onSendUpdate() {
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    /**
     * Called when before update is sent.
     */
    protected void beforeSendUpdate() {

    }

    /**
     * Called when after update is sent. (Not necessarily received yet!)
     */
    protected void afterSendUpdate() {

    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.func_148857_g());
        onUpdateReceived();
    }

    /**
     * This method is called when the tile entity receives
     * an update (ie a data packet) from the server.
     * If this tile entity uses NBT, then the NBT will have
     * already been updated when this method is called.
     */
    public void onUpdateReceived() {

    }

    /**
     * @return The minimum amount of ticks between two consecutive sent packets.
     */
    protected int getUpdateBackoffTicks() {
        return UPDATE_BACKOFF_TICKS;
    }

    /**
     * Called when the blockState of this tile entity is destroyed.
     */
    public void destroy() {
        invalidate();
    }

    /**
     * If this entity is interactable with a player.
     *
     * @param player The player that is checked.
     * @return If the given player can interact.
     */
    public boolean canInteractWith(EntityPlayer player) {
        return !isInvalid() && player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        writeGeneratedFieldsToNBT(tag);
        writeCommon(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        readGeneratedFieldsFromNBT(tag);
        readCommon(tag);
        onLoad();
    }

    @Deprecated
    public void writeCommon(NBTTagCompound tag) {}

    @Deprecated
    public void readCommon(NBTTagCompound tag) {}

    /**
     * When the tile is loaded or created.
     */
    public void onLoad() {

    }

    /**
     * Get the NBT tag for this tile entity.
     *
     * @return The NBT tag that is created with the
     *         {@link TileEntityOK#writeToNBT(NBTTagCompound)} method.
     */
    public NBTTagCompound getNBTTagCompound() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return tag;
    }

    /**
     * If the blockState this tile entity has can be rotated.
     *
     * @return If it can be rotated.
     */
    public boolean isRotatable() {
        return this.rotatable;
    }

    /**
     * Set whether or not the blockState that has this tile entity can be rotated.
     *
     * @param rotatable If it can be rotated.
     */
    public void setRotatable(boolean rotatable) {
        this.rotatable = rotatable;
    }

    /**
     * Get the current rotation of this tile entity.
     * Default is {@link net.minecraft.util.EnumFacing#NORTH}.
     *
     * @return The rotation.
     */
    public ForgeDirection getRotation() {
        return rotation;
    }

    /**
     * Set the rotation of this tile entity.
     * Default is {@link net.minecraft.util.EnumFacing#NORTH}.
     *
     * @param rotation The new rotation.
     */
    public void setRotation(ForgeDirection rotation) {
        this.rotation = rotation;
    }

    /**
     * Get the blockState type this tile entity is defined for.
     *
     * @return The blockState instance.
     */
    public ConfigurableBlockContainer getBlock() {
        return (ConfigurableBlockContainer) this.getBlockType();
    }

    @Override
    public void invalidate() {
        capabilityCache.invalidateAll();
        super.invalidate();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability,
        @Nullable ForgeDirection facing) {
        if (capabilityCache.isCapabilityDisabled(capability, facing)) {
            return LazyOptional.empty();
        } else if (capabilityCache.canResolve(capability)) {
            return capabilityCache.getCapability(capability, facing);
        }
        return this.capabilities == null ? LazyOptional.empty() : this.capabilities.getCapability(capability, facing);
    }

    public interface ITickingTile {

        void update();
    }

    public static class TickingTileComponent implements ITickingTile {

        private final TileEntityOK tile;

        public TickingTileComponent(TileEntityOK tile) {
            this.tile = tile;
        }

        @Override
        public final void update() {
            tile.updateTicking();
        }
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.readFromNBT(nbt);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound ret = new NBTTagCompound();
        this.writeToNBT(ret);
        return ret;
    }

    public BlockPos getPos() {
        if (pos == null || pos.getX() != xCoord || pos.getY() != yCoord || pos.getZ() != zCoord) {
            pos = new BlockPos(this);
        }
        return pos;
    }
}
