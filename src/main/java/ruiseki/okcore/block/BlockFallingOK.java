package ruiseki.okcore.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.okcore.item.ItemBlockOK;

public class BlockFallingOK extends BlockFalling implements IBlock, IBlockTooltipProvider {

    protected final String name;

    protected boolean isOpaque = true;
    protected boolean isFullSize = true;
    public boolean hasSubtypes = false;

    public BlockFallingOK(String name, Material material) {
        super(material);
        this.name = name;
        this.setStepSound(getSoundForMaterial(material));
    }

    @Override
    public void init() {
        registerBlock();
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

    protected void registerComponent() {}

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {}

    public BlockFallingOK setTextureName(String texture) {
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
}
