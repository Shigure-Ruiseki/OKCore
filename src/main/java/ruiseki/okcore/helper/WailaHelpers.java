package ruiseki.okcore.helper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import cpw.mods.fml.common.registry.GameData;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.SpecialChars;

public class WailaHelpers {

    public static List<String> getFluidTooltip(IFluidHandler handler) {
        if (handler == null) return new ArrayList<>();

        List<String> list = new ArrayList<>();
        FluidTankInfo[] tanks = handler.getTankInfo(ForgeDirection.UNKNOWN);

        if (tanks == null) return list;

        for (FluidTankInfo tank : tanks) {
            if (tank == null) continue;

            boolean empty = tank.fluid == null;

            list.add(
                SpecialChars.getRenderString(
                    "waila.fluid",
                    empty ? "EMPTYFLUID"
                        : tank.fluid.getFluid()
                            .getName(),
                    empty ? "EMPTYFLUID" : tank.fluid.getLocalizedName(),
                    String.valueOf(empty ? 0 : tank.fluid.amount),
                    String.valueOf(tank.capacity)));
        }
        return list;
    }

    public static String getInventoryTooltip(IInventory inv) {
        String renderStr = "";
        if (inv == null) return null;

        int index = 1;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);

            if (stack == null || stack.getItem() == null) {
                continue;
            }
            String name = GameData.getItemRegistry()
                .getNameForObject(stack.getItem());
            renderStr += SpecialChars.getRenderString(
                "waila.stack",
                String.valueOf(index),
                name,
                String.valueOf(stack.stackSize),
                String.valueOf(stack.getItemDamage()));

        }

        return renderStr;
    }

    public static Vec3 getLocalHit(IWailaDataAccessor accessor) {
        if (accessor == null) return null;

        MovingObjectPosition mop = accessor.getPosition();
        if (mop == null || mop.hitVec == null) return null;

        return Vec3.createVectorHelper(
            mop.hitVec.xCoord - mop.blockX,
            mop.hitVec.yCoord - mop.blockY,
            mop.hitVec.zCoord - mop.blockZ);
    }

}
