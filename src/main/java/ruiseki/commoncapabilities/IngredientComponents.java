package ruiseki.commoncapabilities;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import com.google.common.collect.Lists;

import ruiseki.commoncapabilities.api.capability.fluidhandler.FluidMatch;
import ruiseki.commoncapabilities.api.capability.itemhandler.ItemMatch;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponentCategoryType;
import ruiseki.commoncapabilities.capability.itemhandler.SlotlessItemHandlerConfig;
import ruiseki.commoncapabilities.ingredient.IngredientMatcherEnergy;
import ruiseki.commoncapabilities.ingredient.IngredientMatcherFluidStack;
import ruiseki.commoncapabilities.ingredient.IngredientMatcherItemStack;
import ruiseki.commoncapabilities.ingredient.IngredientSerializerEnergy;
import ruiseki.commoncapabilities.ingredient.IngredientSerializerFluidStack;
import ruiseki.commoncapabilities.ingredient.IngredientSerializerItemStack;
import ruiseki.commoncapabilities.ingredient.storage.IngredientComponentStorageWrapperHandlerEnergyStorage;
import ruiseki.commoncapabilities.ingredient.storage.IngredientComponentStorageWrapperHandlerFluidStack;
import ruiseki.commoncapabilities.ingredient.storage.IngredientComponentStorageWrapperHandlerItemStack;
import ruiseki.commoncapabilities.ingredient.storage.IngredientComponentStorageWrapperHandlerItemStackSlotless;
import ruiseki.okcore.energy.capability.CapabilityEnergy;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.item.capability.CapabilityItemHandler;

/**
 * The ingredient components that will be registered by this mod.
 *
 * These should not be used directly, get their instances via the registry instead!
 *
 * @author rubensworks
 */
public class IngredientComponents {

    public static final IngredientComponent<ItemStack, Integer> ITEMSTACK = new IngredientComponent<>(
        "minecraft:itemstack",
        new IngredientMatcherItemStack(),
        new IngredientSerializerItemStack(),
        Lists.newArrayList(
            new IngredientComponentCategoryType<>(
                new ResourceLocation("itemstack/item"),
                Item.class,
                true,
                ItemStack::getItem,
                ItemMatch.ITEM,
                false),
            new IngredientComponentCategoryType<>(
                new ResourceLocation("itemstack/metadata"),
                Integer.class,
                false,
                ItemStack::getItemDamage,
                ItemMatch.DAMAGE,
                false),
            new IngredientComponentCategoryType<>(
                new ResourceLocation("itemstack/count"),
                Integer.class,
                false,
                stack -> stack.stackSize,
                ItemMatch.STACKSIZE,
                true),
            new IngredientComponentCategoryType<>(
                new ResourceLocation("itemstack/tag"),
                NBTTagCompound.class,
                false,
                ItemStack::getTagCompound,
                ItemMatch.NBT,
                false))).setTranslationKey("recipecomponent.minecraft.itemstack");

    public static final IngredientComponent<FluidStack, Integer> FLUIDSTACK = new IngredientComponent<>(
        "minecraft:fluidstack",
        new IngredientMatcherFluidStack(),
        new IngredientSerializerFluidStack(),
        Lists.newArrayList(
            new IngredientComponentCategoryType<>(
                new ResourceLocation("fluidstack/fluid"),
                Fluid.class,
                true,
                FluidStack::getFluid,
                FluidMatch.FLUID,
                false),
            new IngredientComponentCategoryType<>(
                new ResourceLocation("fluidstack/amount"),
                Integer.class,
                false,
                fluidStack -> fluidStack.amount,
                FluidMatch.AMOUNT,
                true),
            new IngredientComponentCategoryType<>(
                new ResourceLocation("fluidstack/tag"),
                NBTTagCompound.class,
                false,
                fluidStack -> fluidStack.tag,
                FluidMatch.NBT,
                false))).setTranslationKey("recipecomponent.minecraft.fluidstack");

    public static final IngredientComponent<Integer, Boolean> ENERGY = new IngredientComponent<>(
        "minecraft:energy",
        new IngredientMatcherEnergy(),
        new IngredientSerializerEnergy(),
        Lists.newArrayList(
            new IngredientComponentCategoryType<>(
                new ResourceLocation("energy/amount"),
                Integer.class,
                false,
                amount -> amount,
                true,
                true))).setTranslationKey("recipecomponent.minecraft.energy");

    static {
        ENERGY.setStorageWrapperHandler(
            CapabilityEnergy.ENERGY,
            new IngredientComponentStorageWrapperHandlerEnergyStorage(ENERGY));
        ITEMSTACK.setStorageWrapperHandler(
            CapabilityItemHandler.ITEM_HANDLER,
            new IngredientComponentStorageWrapperHandlerItemStack(ITEMSTACK));
        ITEMSTACK.setStorageWrapperHandler(
            SlotlessItemHandlerConfig.CAPABILITY,
            new IngredientComponentStorageWrapperHandlerItemStackSlotless(ITEMSTACK));
        IngredientComponentStorageWrapperHandlerFluidStack fluidWrapper = new IngredientComponentStorageWrapperHandlerFluidStack(
            FLUIDSTACK);
        FLUIDSTACK.setStorageWrapperHandler(CapabilityFluidHandler.FLUID_HANDLER, fluidWrapper);
        FLUIDSTACK.setStorageWrapperHandler(CapabilityFluidHandler.FLUID_HANDLER_ITEM, fluidWrapper);
    }

}
