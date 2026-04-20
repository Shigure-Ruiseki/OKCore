package ruiseki.okcore.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.helper.TileHelpers;
import ruiseki.okcore.item.ItemBlockOK;
import ruiseki.okcore.tileentity.TileEntityNBTStorage;
import ruiseki.okcore.tileentity.TileEntityOK;

public class BlockOK extends Block implements IBlock, IBlockTooltipProvider {

    protected final Class<? extends TileEntityOK> teClass;
    protected final String name;

    protected boolean isOpaque = true;
    protected boolean isFullSize = true;
    public boolean hasSubtypes = false;

    protected BlockOK(String name) {
        this(name, null, new Material(MapColor.ironColor));
    }

    public BlockOK(String name, Material material) {
        this(name, null, material);
    }

    protected BlockOK(String name, Class<? extends TileEntityOK> teClass) {
        this(name, teClass, new Material(MapColor.ironColor));
    }

    protected BlockOK(String name, @Nullable Class<? extends TileEntityOK> teClass, Material mat) {
        super(mat);
        this.teClass = teClass;
        this.name = name;
        setHardness(0.5F);
        setBlockName(name);
        setHarvestLevel("pickaxe", 0);
        this.setStepSound(getSoundForMaterial(mat));
    }

    @Override
    public void init() {
        registerBlock();
        registerTileEntity();
        registerComponent();
    }

    @Override
    public Block getBlock() {
        return this;
    }

    @Override
    public boolean isHasSubtypes() {
        return hasSubtypes;
    }

    protected void registerBlock() {
        GameRegistry.registerBlock(this, getItemBlockClass(), name);
    }

    protected Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockOK.class;
    }

    protected void registerTileEntity() {
        if (teClass != null) {
            GameRegistry.registerTileEntity(teClass, name + "TileEntity");
        }
    }

    protected void registerComponent() {}

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {}

    @Override
    public boolean hasTileEntity(int metadata) {
        return teClass != null;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        if (teClass != null) {
            try {
                return teClass.getDeclaredConstructor()
                    .newInstance();
            } catch (Exception e) {
                OKCore.okLog(Level.ERROR, "Could not create tile entity for block " + name + " for class " + teClass);
            }
        }
        return null;
    }

    public BlockOK setTextureName(String texture) {
        this.textureName = texture;
        return this;
    }

    @Override
    public String getTextureName() {
        return textureName == null ? name : textureName;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        blockIcon = reg.registerIcon(getTextureName());
    }

    /* Subclass Helpers */

    @Override
    public final boolean isOpaqueCube() {
        return this.isOpaque;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return this.isFullSize && this.isOpaque;
    }

    @Override
    public final boolean isNormalCube(final IBlockAccess world, final int x, final int y, final int z) {
        return this.isFullSize;
    }

    public SoundType getSoundForMaterial(Material mat) {
        if (mat == Material.glass) return Block.soundTypeGlass;
        if (mat == Material.rock) return Block.soundTypeStone;
        if (mat == Material.wood) return Block.soundTypeWood;
        return Block.soundTypeMetal;
    }

    // Because the vanilla method takes floats...
    public void setBlockBounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    // Block Destroy

    /**
     * If the NBT data of this tile entity should be added to the dropped meta.
     *
     * @return If the NBT data should be added.
     */
    public boolean saveNBTToDroppedItem() {
        return true;
    }

    /**
     * @return If the items should be dropped.
     */
    public boolean shouldDropInventory(World world, int x, int y, int z) {
        return true;
    }

    /**
     * Sets a block to air, but also plays the sound and particles and can spawn drops.
     * This includes calls to {@link BlockOK#onPreBlockDestroyed(World, int x, int y, int z, EntityPlayer)}
     * and {@link BlockOK#onPostBlockDestroyed(World, int x, int y, int z)}.
     *
     * @param world     The world.
     * @param x,        y, z The position.
     * @param dropBlock If this should produce item drops.
     * @return If the block was destroyed and not air.
     */
    public boolean destroyBlock(World world, int x, int y, int z, boolean dropBlock) {
        onPreBlockDestroyedPersistence(world, x, y, z);
        boolean result = world.func_147480_a(x, y, z, dropBlock);
        onPostBlockDestroyed(world, x, y, z);
        return result;
    }

    /**
     * Called before the block is broken or destroyed.
     *
     * @param world  The world.
     * @param x,     y, z The position of the to-be-destroyed block.
     * @param player The player destroying the block.
     */
    protected void onPreBlockDestroyed(World world, int x, int y, int z, @Nullable EntityPlayer player) {
        onPreBlockDestroyedPersistence(world, x, y, z);
    }

    /**
     * Called before the block is broken or destroyed when the tile data needs to be persisted.
     *
     * @param world The world.
     * @param x,    y, z The position of the to-be-destroyed block.
     */
    protected void onPreBlockDestroyedPersistence(World world, int x, int y, int z) {
        if (!world.isRemote) {
            MinecraftHelpers.preDestroyBlock(this, world, x, y, z, saveNBTToDroppedItem());
        }
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
            if (TileEntityNBTStorage.TILE != null) {
                itemStack = tileDataToItemStack(TileEntityNBTStorage.TILE, itemStack);
            }
            drops.add(itemStack);
        }
        return drops;
    }

    protected ItemStack tileDataToItemStack(TileEntityOK tile, ItemStack itemStack) {
        if (isKeepNBTOnDrop()) {
            if (TileEntityNBTStorage.TAG != null) {
                itemStack.setTagCompound(TileEntityNBTStorage.TAG);
            }
            if (TileEntityNBTStorage.NAME != null) {
                itemStack.setStackDisplayName(TileEntityNBTStorage.NAME);
            }
        }
        return itemStack;
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
}
