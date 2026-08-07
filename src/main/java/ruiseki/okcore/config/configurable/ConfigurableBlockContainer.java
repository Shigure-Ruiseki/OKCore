package ruiseki.okcore.config.configurable;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import lombok.experimental.Delegate;
import ruiseki.okcore.block.IBlockTooltipProvider;
import ruiseki.okcore.block.property.BlockPropertyProviderComponent;
import ruiseki.okcore.block.property.IBlockPropertyProvider;
import ruiseki.okcore.config.extendedconfig.BlockConfig;
import ruiseki.okcore.config.extendedconfig.BlockContainerConfig;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.helper.TileHelpers;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.tileentity.TileEntityNBTStorage;
import ruiseki.okcore.tileentity.TileEntityOK;

public class ConfigurableBlockContainer extends BlockContainer
    implements IConfigurableBlock, IBlockPropertyProvider, IBlockTooltipProvider {

    @SuppressWarnings("rawtypes")
    protected BlockConfig eConfig = null;

    protected Random random;
    private Class<? extends TileEntityOK> tileEntity;

    protected boolean hasGui = false;

    private boolean rotatable;

    protected int pass = 0;
    protected boolean isInventoryBlock = false;

    @Delegate
    protected IBlockPropertyProvider propertyProvider = new BlockPropertyProviderComponent(this);

    /**
     * Make a new blockState instance.
     *
     * @param eConfig    Config for this blockState.
     * @param material   Material of this blockState.
     * @param tileEntity The class of the tile entity this blockState holds.
     */
    @SuppressWarnings({ "rawtypes" })
    public ConfigurableBlockContainer(ExtendedConfig eConfig, Material material,
        Class<? extends TileEntityOK> tileEntity) {
        super(material);
        this.setConfig(eConfig);
        this.setBlockName(eConfig.getUnlocalizedName());
        this.setBlockTextureName(
            eConfig.getMod()
                .getModId() + ":"
                + eConfig.getNamedId());
        this.random = new Random();
        this.tileEntity = tileEntity;
        setHardness(5F);
        setStepSound(Block.soundTypePiston);
    }

    /**
     * Get the class of the tile entity this blockState holds.
     *
     * @return The tile entity class.
     */
    public Class<? extends TileEntity> getTileEntity() {
        return this.tileEntity;
    }

    @SuppressWarnings("rawtypes")
    private void setConfig(ExtendedConfig eConfig) {
        this.eConfig = (BlockConfig) eConfig;
    }

    @Override
    public boolean hasGui() {
        return hasGui;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        try {
            TileEntityOK tile = tileEntity.newInstance();
            tile.onLoad();
            tile.setRotatable(isRotatable());
            return tile;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * If the NBT data of this tile entity should be added to the dropped blockState.
     *
     * @return If the NBT data should be added.
     */
    public boolean saveNBTToDroppedItem() {
        return true;
    }

    /**
     * Sets a block to air, but also plays the sound and particles and can spawn drops.
     * This includes calls to {@link ConfigurableBlockContainer#onPreBlockDestroyed(World, int, int, int)}
     * and {@link ConfigurableBlockContainer#onPostBlockDestroyed(World, int, int, int)}.
     *
     * @param world     The world.
     * @param dropBlock If this should produce item drops.
     * @return If the block was destroyed and not air.
     */
    public boolean destroyBlock(World world, int x, int y, int z, boolean dropBlock) {
        onPreBlockDestroyed(world, x, y, z);
        boolean result = world.func_147480_a(x, y, z, dropBlock);
        onPostBlockDestroyed(world, x, y, z);
        return result;
    }

    /**
     * Called before the block is broken or destroyed.
     *
     * @param world  The world.
     * @param player The player destroying the block.
     */
    protected void onPreBlockDestroyed(World world, int x, int y, int z, EntityPlayer player) {
        onPreBlockDestroyed(world, x, y, z);
    }

    /**
     * Called before the block is broken or destroyed.
     *
     * @param world The world.
     */
    protected void onPreBlockDestroyed(World world, int x, int y, int z) {
        MinecraftHelpers.preDestroyBlock(this, world, x, y, z, saveNBTToDroppedItem());
    }

    /**
     * Called before the block is broken or destroyed when the tile data needs to be persisted.
     *
     * @param world The world.
     * @param x,    y, z The position of the to-be-destroyed block.
     */
    protected void onPreBlockDestroyedPersistence(World world, int x, int y, int z) {
        MinecraftHelpers.preDestroyBlock(this, world, x, y, z, saveNBTToDroppedItem());
    }

    /**
     * Called before the block is broken or destroyed.
     *
     * @param world The world.
     * @param x,    y, z The position of the to-be-destroyed block.
     */
    protected void onPostBlockDestroyed(World world, int x, int y, int z) {

    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block blockBroken, int meta) {
        onPreBlockDestroyed(world, x, y, z, null);
        super.breakBlock(world, x, y, z, blockBroken, meta);
        onPostBlockDestroyed(world, x, y, z);
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        onPreBlockDestroyed(world, x, y, z, player);
        if (willHarvest) return true;
        return super.removedByPlayer(world, player, x, y, z, willHarvest);
    }

    @Override
    public void onBlockExploded(World world, int x, int y, int z, Explosion explosion) {
        onPreBlockDestroyed(world, x, y, z, null);
        super.onBlockExploded(world, x, y, z, explosion);
        onPostBlockDestroyed(world, x, y, z);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta) {
        super.harvestBlock(world, player, x, y, z, meta);
        world.setBlockToAir(x, y, z);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        if (entity != null) {
            TileEntityOK tile = TileHelpers.getSafeTile(world, x, y, z, TileEntityOK.class);
            if (tile != null && stack.getTagCompound() != null) {
                stack.getTagCompound()
                    .setInteger("x", x);
                stack.getTagCompound()
                    .setInteger("y", y);
                stack.getTagCompound()
                    .setInteger("z", z);
                tile.readFromNBT(stack.getTagCompound());
            }

            if (tile instanceof TileEntityOK.ITickingTile ticking) {
                ticking.update();
            }
        }
        super.onBlockPlacedBy(world, x, y, z, entity, stack);
    }

    /**
     * Write additional info about the tile into the item.
     *
     * @param tile The tile that is being broken.
     * @param tag  The tag that will be added to the dropped item.
     */
    public void writeAdditionalInfo(TileEntity tile, NBTTagCompound tag) {

    }

    /**
     * If this block should drop its block item.
     *
     * @param world   The world.
     * @param x,      y, z The position.
     * @param fortune Fortune level.
     * @return If the item should drop.
     */
    public boolean isDropBlockItem(IBlockAccess world, int x, int y, int z, int fortune) {
        return true;
    }

    @Override
    public final ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();

        Item item = getItemDropped(meta, world.rand, fortune);
        if (item != null && isDropBlockItem(world, x, y, z, fortune)) {
            ItemStack itemStack = new ItemStack(item, 1, damageDropped(meta));
            if (isKeepNBTOnDrop()) {
                if (TileEntityNBTStorage.TAG != null) {
                    itemStack.setTagCompound(TileEntityNBTStorage.TAG);
                }
                if (TileEntityNBTStorage.NAME != null) {
                    itemStack.setStackDisplayName(TileEntityNBTStorage.NAME);
                }
            }
            drops.add(itemStack);
        }

        MinecraftHelpers.postDestroyBlock(world, x, y, z);
        return drops;
    }

    /**
     * If the NBT data of this blockState should be preserved in the item when it
     * is broken into an item.
     *
     * @return If it should keep NBT data.
     */
    public boolean isKeepNBTOnDrop() {
        return true;
    }

    /**
     * If this blockState can be rotated.
     *
     * @return Can be rotated.
     */
    public boolean isRotatable() {
        return rotatable;
    }

    /**
     * Set whether of not this container must be able to be rotated.
     *
     * @param rotatable Can be rotated.
     */
    public void setRotatable(boolean rotatable) {
        this.rotatable = rotatable;
    }

    /**
     * Get the texture path of the GUI.
     *
     * @return The path of the GUI for this blockState.
     */
    public String getGuiTexture() {
        return getGuiTexture("");
    }

    /**
     * Get the texture path of the GUI.
     *
     * @param suffix Suffix to add to the path.
     * @return The path of the GUI for this blockState.
     */
    public String getGuiTexture(String suffix) {
        return getConfig().getMod()
            .getReferenceValue(ModBase.REFKEY_TEXTURE_PATH_GUI) + eConfig.getNamedId() + "_gui" + suffix + ".png";
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z,
        @Nullable EntityPlayer player) {
        ItemStack itemStack = super.getPickBlock(target, world, x, y, z, player);
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileEntityOK teok && isKeepNBTOnDrop()) {
            itemStack.setTagCompound(teok.getNBTTagCompound());
        }
        return itemStack;
    }

    @Override
    public final BlockContainerConfig getConfig() {
        return (BlockContainerConfig) this.eConfig;
    }
}
