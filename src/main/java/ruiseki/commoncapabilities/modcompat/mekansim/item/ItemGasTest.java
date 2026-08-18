package ruiseki.commoncapabilities.modcompat.mekansim.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import mekanism.api.gas.GasStack;
import ruiseki.commoncapabilities.modcompat.mekansim.capability.gashandler.GasHandlerConfig;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.item.ItemBase;

public class ItemGasTest extends ItemBase {

    private static ItemGasTest _instance = null;

    /**
     * Get the unique instance.
     *
     * @return The instance.
     */
    public static ItemGasTest getInstance() {
        return _instance;
    }

    public ItemGasTest() {
        super();
        setTextureName("stick");
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null) {
            ForgeDirection direction = ForgeDirection.getOrientation(side);
            return CapabilityHelpers.getCapability(te, GasHandlerConfig.CAPABILITY, direction)
                .map(handler -> {
                    GasStack extracted = handler.drawGas(direction, 1000, true);
                    return extracted != null;
                })
                .orElse(false);
        }
        return super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
    }
}
