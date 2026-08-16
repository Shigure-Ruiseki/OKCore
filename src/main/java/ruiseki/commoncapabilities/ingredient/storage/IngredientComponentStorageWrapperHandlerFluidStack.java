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
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorageWrapperHandler;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.fluid.FluidHelpers;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidTankProperties;
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

    @Nullable
    @Override
    public IFluidHandler getStorage(ICapabilityProvider capabilityProvider, @Nullable ForgeDirection facing) {
        return capabilityProvider.getCapability(CapabilityFluidHandler.FLUID_HANDLER, facing)
            .getOrNull();
    }

    @Override
    public IngredientComponent<FluidStack, Integer> getComponent() {
        return this.ingredientComponent;
    }

    public static class ComponentStorageWrapper implements IIngredientComponentStorage<FluidStack, Integer> {

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
            // Null-check, because the empty fluidstack is null
            if (ingredient == null) {
                return null;
            }

            int totalAmount = ingredient.amount;
            int filledAmount = storage.fill(ingredient, !simulate);
            if (filledAmount >= totalAmount) {
                return null;
            } else {
                int remaining = totalAmount - filledAmount;
                return new FluidStack(ingredient, remaining);
            }
        }

        @Override
        public FluidStack extract(@Nonnull FluidStack prototype, Integer matchFlags, boolean simulate) {
            // Null-check, because the empty fluidstack is null
            if (prototype == null) {
                return null;
            }

            // Optimize if ANY condition
            if (matchFlags == FluidMatch.ANY) {
                // Drain as much as possible
                return storage.drain(FluidHelpers.getAmount(prototype), !simulate);
            }

            // Optimize if AMOUNT condition
            if (matchFlags == FluidMatch.AMOUNT) {
                // Drain the exact given amount
                FluidStack drainedSimulated = storage.drain(prototype.amount, false);
                if (drainedSimulated == null || drainedSimulated.amount != prototype.amount) {
                    return null;
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

            return null;
        }

        @Override
        public FluidStack extract(long maxQuantity, boolean simulate) {
            return storage.drain(Helpers.castSafe(maxQuantity), !simulate);
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
        public int fill(FluidStack resource, boolean doFill) {
            // Null-check, because the empty fluidstack is null
            if (resource == null) {
                return 0;
            }

            FluidStack inserted = storage.insert(resource, !doFill);
            return inserted == null ? resource.amount : resource.amount - inserted.amount;
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            // Null-check, because the empty fluidstack is null
            if (resource == null) {
                return null;
            }

            FluidStack extractSimulated = storage.extract(resource, FluidMatch.FLUID | FluidMatch.NBT, true);
            if (extractSimulated != null) {
                FluidStack prototype = resource;
                if (prototype.amount > extractSimulated.amount) {
                    prototype = prototype.copy();
                    prototype.amount = extractSimulated.amount;
                }
                return storage.extract(prototype, FluidMatch.EXACT, !doDrain);
            }
            return null;
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            return storage.extract(maxDrain, !doDrain);
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
