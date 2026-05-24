package ruiseki.okcore.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidContainerItem;

import org.apache.logging.log4j.Level;

import ruiseki.okcore.OKCore;

public class NEIHelpers {

    public static String translate(String unlocalized) {
        return StatCollector.translateToLocal("neiintegration." + unlocalized);
    }

    public static List<ItemStack> getItemVariations(ItemStack base) {
        List<ItemStack> variations = new ArrayList<>();
        base.getItem()
            .getSubItems(base.getItem(), null, variations);
        Iterator<ItemStack> itr = variations.iterator();
        ItemStack stack;
        while (itr.hasNext()) {
            stack = itr.next();
            if (!base.isItemEqual(stack) || !stack.hasTagCompound()) {
                itr.remove();
            }
        }
        if (variations.isEmpty()) {
            return Collections.singletonList(base);
        }
        return variations;
    }

    public static FluidStack getFluidStack(ItemStack stack) {
        if (stack != null) {
            FluidStack fluidStack = null;
            if (stack.getItem() instanceof IFluidContainerItem) {
                fluidStack = ((IFluidContainerItem) stack.getItem()).getFluid(stack);
            }
            if (fluidStack == null) {
                fluidStack = FluidContainerRegistry.getFluidForFilledItem(stack);
            }
            if (fluidStack == null && Block.getBlockFromItem(stack.getItem()) instanceof IFluidBlock) {
                Fluid fluid = ((IFluidBlock) Block.getBlockFromItem(stack.getItem())).getFluid();
                if (fluid != null) {
                    fluidStack = new FluidStack(fluid, 1000);
                }
            }
            return fluidStack;
        }
        return null;
    }

    public static boolean areFluidsSameType(FluidStack fluidStack1, FluidStack fluidStack2) {
        if (fluidStack1 == null || fluidStack2 == null) {
            return false;
        }
        return fluidStack1.getFluid() == fluidStack2.getFluid();
    }

    public static void reloadNEIFuels() {
        try {
            Class.forName("codechicken.nei.recipe.FurnaceRecipeHandler");
            codechicken.nei.recipe.FurnaceRecipeHandler.afuels = null;
            codechicken.nei.recipe.TemplateRecipeHandler.findFuelsOnce();

            OKCore.okLog(Level.INFO, "Successfully refreshed and synchronized fuel list into NEI.");
        } catch (ClassNotFoundException e) {
            OKCore.okLog(Level.INFO, "NEI is not installed. Skipping NEI fuel cache update.");
        } catch (Exception e) {
            OKCore
                .okLog(Level.ERROR, "An unexpected error occurred while resetting NEI fuel registry: {}", e.toString());
        }
    }
}
