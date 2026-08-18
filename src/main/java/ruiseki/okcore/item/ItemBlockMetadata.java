package ruiseki.okcore.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.block.IBlockRarityProvider;
import ruiseki.okcore.block.IBlockStateAction;
import ruiseki.okcore.block.IBlockTooltipProvider;
import ruiseki.okcore.capabilities.IItemCapability;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.LangHelpers;

/**
 * An extended {@link net.minecraft.item.ItemBlock} that will automatically add information to the blockState
 * item if that blockState implements {@link IInformationProvider}.
 *
 * @author rubensworks
 *
 */
public class ItemBlockMetadata extends ItemBlock implements IItemCapability, IItemSharedTag {

    protected InformationProviderComponent informationProvider;
    protected IBlockRarityProvider rarityProvider = null;

    /**
     * Make a new instance.
     *
     * @param block The blockState instance.
     */
    public ItemBlockMetadata(Block block) {
        super(block);
        this.setHasSubtypes(true);
        informationProvider = new InformationProviderComponent(block);
        if (block instanceof IBlockRarityProvider) {
            rarityProvider = (IBlockRarityProvider) block;
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        super.addInformation(stack, player, list, flag);
        if (this.field_150939_a instanceof IBlockTooltipProvider provider)
            provider.addInformation(stack, player, list, flag);
        LangHelpers.addOptionalInfo(list, getUnlocalizedName());
        informationProvider.addInformation(stack, player, list, flag);
    }

    @Override
    public EnumRarity getRarity(ItemStack itemStack) {
        if (rarityProvider != null) {
            return rarityProvider.getRarity(itemStack);
        }
        return super.getRarity(itemStack);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ, int metadata) {
        if (this.field_150939_a instanceof IBlockStateAction configurableBlock) {
            BlockState state = configurableBlock.getStateForPlacement(
                world,
                new BlockPos(x, y, z),
                ForgeDirection.getOrientation(side),
                hitX,
                hitY,
                hitZ,
                metadata,
                player);
            if (!state.place(world, x, y, z, 3)) {
                return false;
            }

            if (world.getBlock(x, y, z) == field_150939_a) {
                field_150939_a.onBlockPlacedBy(world, x, y, z, player, stack);
                field_150939_a.onPostBlockPlaced(world, x, y, z, metadata);
            }
            return true;
        }
        return super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
    }
}
