package ruiseki.commoncapabilities.ingredient.storage;

import java.util.Iterator;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import com.google.common.collect.Lists;

import ruiseki.commoncapabilities.api.capability.fluidhandler.FluidHandlerFluidStackIterator;
import ruiseki.commoncapabilities.api.capability.fluidhandler.FluidMatch;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorageSlotted;
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorageWrapperHandler;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidTankProperties;
import ruiseki.okcore.helper.FluidHelpers;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.ingredient.collection.FilteredIngredientCollectionIterator;

/**
 * Fluid storage wrapper handler for {@link IFluidHandler}.
 * 
 * @author rubensworks
 */
public class IngredientComponentStorageWrapperHandlerFluidStack
    implements IIngredientComponentStorageWrapperHandler<FluidStack, Integer, IFluidHandler> {

    private final IngredientComponent<FluidStack, Integer> ingredientComponent;

    public IngredientComponentStorageWrapperHandlerFluidStack(
        IngredientComponent<FluidStack, Integer> ingredientComponent) {
        this.ingredientComponent = Objects.requireNonNull(ingredientComponent);
    }

    @Override
    public IIngredientComponentStorage<FluidStack, Integer> wrapComponentStorage(IFluidHandler storage) {
        return new ComponentStorageWrapper(getComponent(), storage);
    }

    @Override
    public IFluidHandler wrapStorage(IIngredientComponentStorage<FluidStack, Integer> componentStorage) {
        return new FluidStorageWrapper(componentStorage);
    }

    @Override
    public LazyOptional<IFluidHandler> getStorage(ICapabilityProvider capabilityProvider,
        @Nullable ForgeDirection facing) {
        return capabilityProvider.getCapability(CapabilityFluidHandler.FLUID_HANDLER, facing);
    }

    @Override
    public IngredientComponent<FluidStack, Integer> getComponent() {
        return this.ingredientComponent;
    }

    public static class ComponentStorageWrapper implements IIngredientComponentStorageSlotted<FluidStack, Integer> {

        private final IngredientComponent<FluidStack, Integer> ingredientComponent;
        private final IFluidHandler storage;

        public ComponentStorageWrapper(IngredientComponent<FluidStack, Integer> ingredientComponent,
            IFluidHandler storage) {
            this.ingredientComponent = ingredientComponent;
            this.storage = storage;
        }

        @Override
        public IngredientComponent<FluidStack, Integer> getComponent() {
            return this.ingredientComponent;
        }

        @Override
        public Iterator<FluidStack> iterator() {
            return new FluidHandlerFluidStackIterator(storage);
        }

        @Override
        public Iterator<FluidStack> iterator(@Nonnull FluidStack prototype, Integer matchFlags) {
            if (getComponent().getMatcher()
                .getAnyMatchCondition()
                .equals(matchFlags)) {
                return iterator();
            }
            return new FilteredIngredientCollectionIterator<>(
                iterator(),
                getComponent().getMatcher(),
                prototype,
                matchFlags);
        }

        @Override
        public long getMaxQuantity() {
            long sum = 0;
            for (IFluidTankProperties properties : storage.getTankProperties()) {
                sum = Math.addExact(sum, properties.getCapacity());
            }
            return sum;
        }

        @Override
        public FluidStack insert(@Nonnull FluidStack ingredient, boolean simulate) {
            // Don't continue if stack is empty
            if (FluidHelpers.isEmpty(ingredient)) {
                return FluidHelpers.EMPTY;
            }

            int totalAmount = ingredient.amount;
            int filledAmount = storage.fill(ingredient, simulate);
            if (filledAmount >= totalAmount) {
                return FluidHelpers.EMPTY;
            } else {
                int remaining = totalAmount - filledAmount;
                return new FluidStack(ingredient, remaining);
            }
        }

        @Override
        public FluidStack extract(@Nonnull FluidStack prototype, Integer matchFlags, boolean simulate) {
            // Don't continue if stack is empty
            if (FluidHelpers.isEmpty(prototype)) {
                return FluidHelpers.EMPTY;
            }

            // Optimize if ANY condition
            if (matchFlags == FluidMatch.ANY) {
                // Drain as much as possible
                return storage.drain(FluidHelpers.getAmount(prototype), simulate);
            }

            // Optimize if AMOUNT condition
            if (matchFlags == FluidMatch.AMOUNT) {
                // Drain the exact given amount
                FluidStack drainedSimulated = storage.drain(prototype.amount, true);
                if (FluidHelpers.isEmpty(drainedSimulated) || drainedSimulated.amount != prototype.amount) {
                    return FluidHelpers.EMPTY;
                }
                return simulate ? drainedSimulated : storage.drain(prototype.amount, true);
            }

            // In all other cases, we have to iterate over the tank contents,
            // and drain based on their contents.
            for (IFluidTankProperties properties : storage.getTankProperties()) {
                if (properties.getContents() != null && FluidMatch
                    .areFluidStacksEqual(properties.getContents(), prototype, matchFlags & ~FluidMatch.AMOUNT)) {
                    FluidStack toDrain = properties.getContents();
                    toDrain = toDrain.copy();
                    toDrain.amount = prototype.amount;
                    FluidStack drained = storage.drain(toDrain, !simulate);
                    if (FluidMatch.areFluidStacksEqual(drained, prototype, matchFlags)) {
                        return drained;
                    }
                }
            }

            return FluidHelpers.EMPTY;
        }

        @Override
        public FluidStack extract(long maxQuantity, boolean simulate) {
            return storage.drain(Helpers.castSafe(maxQuantity), simulate);
        }

        @Override
        public int getSlots() {
            return storage.getTankProperties().length;
        }

        @Override
        public FluidStack getSlotContents(int slot) {
            return storage.getTankProperties()[slot].getContents();
        }

        @Override
        public long getMaxQuantity(int slot) {
            return storage.getTankProperties()[slot].getCapacity();
        }

        @Override
        public FluidStack insert(int slot, @Nonnull FluidStack ingredient, boolean simulate) {
            // There's no way to extract from a specific slot in IFluidHandler
            return insert(ingredient, simulate);
        }

        @Override
        public FluidStack extract(int slot, long maxQuantity, boolean simulate) {
            // There's no way to extract from a specific slot in IFluidHandler,
            // so we first determine the fluid in the given slot, and then extract with that fluid type.
            // There are cases where this will select the wrong slot,
            // but it's the best we can do given the current interface.
            FluidStack slotContents = storage.getTankProperties()[slot].getContents();
            if (!FluidHelpers.isEmpty(slotContents)) {
                if (slotContents.amount != maxQuantity) {
                    slotContents = slotContents.copy();
                    slotContents.amount = Math.toIntExact(maxQuantity);
                }
                return storage.drain(slotContents, simulate);
            }
            return extract(maxQuantity, simulate);
        }
    }

    public static class FluidStorageWrapper implements IFluidHandler {

        private final IIngredientComponentStorage<FluidStack, Integer> storage;

        public FluidStorageWrapper(IIngredientComponentStorage<FluidStack, Integer> storage) {
            this.storage = storage;
        }

        @Override
        public IFluidTankProperties[] getTankProperties() {
            return Lists.newArrayList(storage)
                .stream()
                .map(DummyFluidTankProperties::new)
                .toArray(IFluidTankProperties[]::new);
        }

        @Override
        public int fill(FluidStack resource, boolean action) {
            if (FluidHelpers.isEmpty(resource)) {
                return 0;
            }

            FluidStack inserted = storage.insert(resource, action);
            return FluidHelpers.isEmpty(inserted) ? resource.amount : resource.amount - inserted.amount;
        }

        @Override
        public FluidStack drain(FluidStack resource, boolean action) {
            // Don't continue if stack is empty
            if (FluidHelpers.isEmpty(resource)) {
                return FluidHelpers.EMPTY;
            }

            FluidStack extractSimulated = storage.extract(resource, FluidMatch.FLUID | FluidMatch.NBT, true);
            if (extractSimulated != null) {
                FluidStack prototype = resource;
                if (prototype.amount > extractSimulated.amount) {
                    prototype = prototype.copy();
                    prototype.amount = extractSimulated.amount;
                }
                return storage.extract(prototype, FluidMatch.EXACT, action);
            }
            return FluidHelpers.EMPTY;
        }

        @Override
        public FluidStack drain(int maxDrain, boolean action) {
            return storage.extract(maxDrain, action);
        }
    }

    public static class DummyFluidTankProperties implements IFluidTankProperties {

        private final FluidStack fluidStack;

        public DummyFluidTankProperties(FluidStack fluidStack) {
            this.fluidStack = fluidStack;
        }

        @Nullable
        @Override
        public FluidStack getContents() {
            return this.fluidStack;
        }

        @Override
        public int getCapacity() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean canFill() {
            return true;
        }

        @Override
        public boolean canDrain() {
            return true;
        }

        @Override
        public boolean canFillFluidType(FluidStack fluidStack) {
            return true;
        }

        @Override
        public boolean canDrainFluidType(FluidStack fluidStack) {
            return true;
        }
    }
}
