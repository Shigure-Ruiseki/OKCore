package ruiseki.okcore.test;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.okcore.energy.capability.CapabilityEnergy;
import ruiseki.okcore.helper.TagHelpers;
import ruiseki.okcore.helper.TileHelpers;
import ruiseki.okcore.item.ItemOK;
import ruiseki.okcore.tag.TagKey;

public class ItemEnergyTest extends ItemOK {

    public ItemEnergyTest() {
        super("energy_test");
        setTextureName("stick");
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;

        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null) {
            ForgeDirection direction = ForgeDirection.getOrientation(side);

            return TileHelpers.getCapability(te, CapabilityEnergy.ENERGY, direction)
                .map(handler -> {
                    int extracted = handler.extractEnergy(1000, false);
                    return extracted > 0;
                })
                .orElse(false);
        }
        return false;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        for (TagKey<?> key : TagHelpers.getTags(stack)) {
            list.add(key.toString());
        }
    }
}
