package ruiseki.okcore.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.okcore.fluid.handler.IFluidHandlerItemCapacity;
import ruiseki.okcore.helper.FluidHelpers;
import ruiseki.okcore.helper.ItemStackHelpers;

/**
 * A component that has to be added for classes that want to implement the DamageIndicator behaviour.
 *
 * Items can add this component (Composite design-pattern) to any item that needs to have a damage
 * indicator based on a custom value. Like for example the amount of energy left in an IC2 electrical
 * wrench, or the amount of MJ's left in a redstone energy cell from Thermal Expansion.
 *
 * See {@link DamageIndicatedItemFluidContainer} for an example.
 * This could be for example an Item or an ItemFluidContainer.
 *
 * @author rubensworks
 *
 */
public class DamageIndicatedItemComponent {

    /**
     * The item class on which the behaviour will be added.
     */
    public ItemFluidContainer item;

    /**
     * Create a new DamageIndicatedItemComponent
     *
     * @param item The item class on which the behaviour will be added.
     */
    public DamageIndicatedItemComponent(ItemFluidContainer item) {
        this.item = item;
        if (this.item != null) {
            this.item.setMaxStackSize(1);
        }
    }

    /**
     * Add the creative tab items.
     *
     * @param tab      The creative tab to add to.
     * @param itemList The item list to add to.
     * @param fluid    The fluid in the container that needs to be added.
     * @param meta     The meta data for the item to add.
     */
    public void getSubItems(CreativeTabs tab, List<ItemStack> itemList, Fluid fluid, int meta) {
        if (itemList == null || this.item == null) {
            return;
        }

        // Add the 'full' container.
        ItemStack itemStackFull = new ItemStack(this.item, 1, meta);
        IFluidHandlerItemCapacity fluidHandlerFull = FluidHelpers.getFluidHandlerItemCapacity(itemStackFull);
        if (fluidHandlerFull != null) {
            fluidHandlerFull.fill(new FluidStack(fluid, fluidHandlerFull.getCapacity()), true);
        }
        itemList.add(itemStackFull);

        // Add the 'empty' container.
        ItemStack itemStackEmpty = new ItemStack(this.item, 1, meta);
        itemList.add(itemStackEmpty);
    }

    /**
     * Get hovering info for the given {@link ItemStack}.
     *
     * @param itemStack The item stack to add the info for.
     * @return The info for the item.
     */
    public String getInfo(ItemStack itemStack) {
        if (ItemStackHelpers.isEmpty(itemStack)) {
            return getInfo(null, 0, 0);
        }

        int amount = 0;
        IFluidHandlerItemCapacity fluidHandler = FluidHelpers.getFluidHandlerItemCapacity(itemStack);
        FluidStack fluidStack = FluidHelpers.getFluidContained(itemStack);
        if (fluidStack != null) {
            amount = fluidStack.amount;
        }
        int capacity = fluidHandler != null ? fluidHandler.getCapacity() : 0;
        return getInfo(fluidStack, amount, capacity);
    }

    /**
     * Get hovering info for the given amount and capacity.
     *
     * @param fluidStack The fluid stack for this container, can be null.
     * @param amount     The amount to show.
     * @param capacity   The capacity to show.
     * @return The info generated from the given parameters.
     */
    public static String getInfo(FluidStack fluidStack, int amount, int capacity) {
        String prefix = "";
        if (fluidStack != null && fluidStack.getFluid() != null) {
            prefix = fluidStack.getFluid()
                .getLocalizedName(fluidStack) + ": ";
        }
        return prefix + String.format("%,d", amount) + " / " + String.format("%,d", capacity) + " mB";
    }

    /**
     * Add information to the given list for the given item.
     *
     * @param itemStack The {@link ItemStack} to add info for.
     * @param world     The player that will see the info.
     * @param list      The info list where the info will be added.
     * @param flag      the tooltip flag
     */
    public void addInformation(ItemStack itemStack, EntityPlayer world, List<String> list, boolean flag) {
        if (ItemStackHelpers.isEmpty(itemStack) || list == null) {
            return;
        }

        if (itemStack.getItem() instanceof IInformationProvider provider) {
            list.add(IInformationProvider.ITEM_PREFIX + provider.getInfo(itemStack));
        }
    }

    /**
     * Get the displayed durability value for the given {@link ItemStack}.
     *
     * @param itemStack The {@link ItemStack} to get the displayed damage for.
     * @return The displayed durability.
     */
    public double getDurability(ItemStack itemStack) {
        if (ItemStackHelpers.isEmpty(itemStack)) {
            return 1.0;
        }

        IFluidHandlerItemCapacity fluidHandler = FluidHelpers.getFluidHandlerItemCapacity(itemStack);
        if (fluidHandler == null || fluidHandler.getCapacity() <= 0) {
            return 1.0;
        }

        FluidStack fluidStack = FluidHelpers.getFluidContained(itemStack);
        double capacity = fluidHandler.getCapacity();
        double amount = FluidHelpers.getAmount(fluidStack);
        return (capacity - amount) / capacity;
    }
}
