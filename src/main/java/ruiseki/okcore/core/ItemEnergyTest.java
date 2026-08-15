package ruiseki.okcore.core;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.okcore.config.configurable.ConfigurableItem;
import ruiseki.okcore.config.extendedconfig.ItemConfig;
import ruiseki.okcore.energy.capability.CapabilityEnergy;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.TagHelpers;
import ruiseki.okcore.tag.TagKey;

public class ItemEnergyTest extends ConfigurableItem {

    private static ItemEnergyTest _instance = null;

    /**
     * Get the unique instance.
     *
     * @return The instance.
     */
    public static ItemEnergyTest getInstance() {
        return _instance;
    }

    public ItemEnergyTest(ItemConfig eConfig) {
        super(eConfig);
        setTextureName("stick");
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;

        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null) {
            ForgeDirection direction = ForgeDirection.getOrientation(side);

            return CapabilityHelpers.getCapability(te, CapabilityEnergy.ENERGY, direction)
                .map(handler -> {
                    int extracted = handler.extractEnergy(1000, false);
                    return extracted > 0;
                })
                .orElse(false);
        }
        return super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        super.addInformation(stack, player, list, flag);
        for (TagKey<?> key : TagHelpers.getTags(stack)) {
            list.add(key.toString());
        }
    }
}
