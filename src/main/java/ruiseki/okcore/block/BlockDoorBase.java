package ruiseki.okcore.block;

import java.util.Random;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import lombok.experimental.Delegate;
import ruiseki.okcore.block.property.BlockPropertyProviderComponent;
import ruiseki.okcore.block.property.IBlockPropertyProvider;

/**
 * Door block that can hold ExtendedConfigs.
 */
public class BlockDoorBase extends BlockDoor
    implements IBlockPropertyProvider, IBlockGui, IBlockStateAction, IBlockTooltipProvider {

    @Delegate
    protected IBlockPropertyProvider propertyProvider = new BlockPropertyProviderComponent(this);

    public Item item;

    protected boolean hasGui = false;

    /**
     * Make a new block instance.
     *
     * @param material The door material.
     */
    public BlockDoorBase(Material material) {
        super(material);
        disableStats();
    }

    @Override
    public Item getItem(World world, int x, int y, int z) {
        return item != null ? item : super.getItem(world, x, y, z);
    }

    @Override
    public Item getItemDropped(int meta, Random rand, int fortune) {
        return (meta & 8) != 0 ? null : this.item;
    }

    @Override
    public BlockDoorBase setStepSound(SoundType sound) {
        super.setStepSound(sound);
        return this;
    }

    @Override
    public boolean hasGui() {
        return hasGui;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        this.field_150017_a = new IIcon[2];
        this.field_150016_b = new IIcon[2];
        this.field_150017_a[0] = reg.registerIcon(this.getTextureName() + "_upper");
        this.field_150016_b[0] = reg.registerIcon(this.getTextureName() + "_lower");
        this.field_150017_a[1] = new IconFlipped(this.field_150017_a[0], true, false);
        this.field_150016_b[1] = new IconFlipped(this.field_150016_b[0], true, false);
    }

}
