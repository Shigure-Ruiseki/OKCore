package ruiseki.okcore.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.block.BlockDoorBase;
import ruiseki.okcore.block.IBlockRarityProvider;
import ruiseki.okcore.client.gui.tooltip.TooltipBuilder;
import ruiseki.okcore.helper.LangHelpers;

/**
 * A hybrid of {@link ItemBlockMetadata} and {@link net.minecraft.item.ItemDoor}.
 *
 * @author josephcsible
 *
 */
public class ItemDoorMetadata extends ItemDoor implements IItemTooltip {

    private final TooltipBuilder tooltipBuilder = new TooltipBuilder();
    protected InformationProviderComponent informationProvider;
    protected IBlockRarityProvider rarity = null;

    protected final BlockDoorBase block;

    public ItemDoorMetadata(Block block) {
        super(block.getMaterial());
        this.block = (BlockDoorBase) block;
        this.block.item = this;
        informationProvider = new InformationProviderComponent(block);
        if (block instanceof IBlockRarityProvider) {
            rarity = (IBlockRarityProvider) block;
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return block.getUnlocalizedName();
    }

    @Override
    public String getUnlocalizedName() {
        return block.getUnlocalizedName();
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return block.getCreativeTabToDisplayOn();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {
        super.addInformation(stack, player, list, advanced);
        LangHelpers.addOptionalInfo(list, getUnlocalizedName());
        informationProvider.addInformation(stack, player, list, advanced);
        this.getTooltipBuilder()
            .build(stack, player, list, advanced);
    }

    @Override
    public EnumRarity getRarity(ItemStack itemStack) {
        if (rarity != null) return rarity.getRarity(itemStack);
        return super.getRarity(itemStack);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (side != 1) return false;
        y++;
        if (player.canPlayerEdit(x, y, z, side, stack) && player.canPlayerEdit(x, y + 1, z, side, stack)) {
            if (!block.canPlaceBlockAt(world, x, y, z)) return false;
            ItemDoor.placeDoorBlock(
                world,
                x,
                y,
                z,
                MathHelper.floor_double((player.rotationYaw + 180.0F) * 4.0F / 360.0F - 0.5D) & 3,
                block);
            return true;
        }
        return false;
    }

    @Override
    public TooltipBuilder getTooltipBuilder() {
        return tooltipBuilder;
    }
}
