package ruiseki.okcore.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.capabilities.IItemCapability;
import ruiseki.okcore.fluid.handler.FluidHandlerItemCapacity;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.helper.FluidHelpers;
import ruiseki.okcore.helper.ItemHelpers;

/**
 * This extension on {@link ItemFluidContainer} with a fluid capability will show a damage indicator depending on how
 * full
 * the container is. This can be used to hold certain amounts of Fluids in an Item.
 * When this item is available in a CreativeTab, it will add itself as a full and an empty container.
 *
 * This container ONLY allows the fluid from the given type.
 *
 * @author rubensworks
 *
 */
public abstract class DamageIndicatedItemFluidContainer extends ItemFluidContainer
    implements IInformationProvider, IItemCapability, IItemSharedTag {

    protected DamageIndicatedItemComponent component;
    protected Fluid fluid;

    /**
     * Create a new DamageIndicatedItemFluidContainer.
     *
     * @param capacity The capacity this container will have.
     * @param fluid    The Fluid instance this container must hold.
     */
    public DamageIndicatedItemFluidContainer(int capacity, Fluid fluid) {
        super(capacity);
        this.fluid = fluid;
        init();
    }

    private void init() {
        component = new DamageIndicatedItemComponent(this);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> itemList) {
        if (!ItemHelpers.isValidCreativeTab(this, tab) || itemList == null) {
            return;
        }
        component.getSubItems(tab, itemList, fluid, 0);
    }

    @Override
    public String getInfo(ItemStack itemStack) {
        if (ItemHelpers.isEmpty(itemStack)) {
            return "";
        }
        return component.getInfo(itemStack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void provideInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean flag) {

    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer world, List<String> list, boolean flag) {
        if (ItemHelpers.isEmpty(itemStack) || list == null) {
            return;
        }
        component.addInformation(itemStack, world, list, flag);
        super.addInformation(itemStack, world, list, flag);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack itemStack) {
        if (ItemHelpers.isEmpty(itemStack)) {
            return 1.0;
        }
        return component.getDurability(itemStack);
    }

    /**
     * Get the fluid.
     *
     * @return The fluid.
     */
    public Fluid getFluid() {
        return this.fluid;
    }

    /**
     * If the given amount can be drained. (Will drain in simulation mode)
     *
     * @param amount    The amount to try to drain.
     * @param itemStack The item stack to drain from.
     * @return If it could be drained.
     */
    public boolean canDrain(int amount, ItemStack itemStack) {
        if (ItemHelpers.isEmpty(itemStack) || amount <= 0) {
            return false;
        }

        IFluidHandler fluidHandler = FluidHelpers.getFluidHandler(itemStack)
            .getOrNull();
        if (fluidHandler == null) {
            return false;
        }

        FluidStack simulatedDrain = fluidHandler.drain(amount, false);
        return simulatedDrain != null && simulatedDrain.amount == amount;
    }

    @Override
    public ICapabilityProvider initCapabilities(@NotNull ItemStack stack, NBTTagCompound nbt) {
        if (ItemHelpers.isEmpty(stack)) {
            return null;
        }
        return new FluidHandlerItemCapacity(stack, capacity, getFluid());
    }
}
