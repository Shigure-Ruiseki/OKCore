
package ruiseki.commoncapabilities.ingredient.storage;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

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
        public @NotNull Iterator<FluidStack> iterator() {
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
            for (int tank = 0; tank < storage.getTanks(); tank++) {
                sum = Math.addExact(sum, storage.getTankCapacity(tank));
            }
            return sum;
        }

        @Override
        public FluidStack insert(@Nonnull FluidStack ingredient, boolean simulate) {
            if (FluidHelpers.isEmpty(ingredient)) {
                return FluidHelpers.EMPTY;
            }
            int totalAmount = ingredient.amount;
            int filledAmount = storage.fill(ingredient, simulate);
            if (filledAmount >= totalAmount) {
                return FluidHelpers.EMPTY;
            }
            int remaining = totalAmount - filledAmount;
            return new FluidStack(ingredient, remaining);
        }

        @Override
        public FluidStack extract(@Nonnull FluidStack prototype, Integer matchFlags, boolean simulate) {
            if (FluidHelpers.isEmpty(prototype)) {
                return FluidHelpers.EMPTY;
            }

            // ANY: drain as much as possible.
            if (matchFlags == FluidMatch.ANY) {
                return storage.drain(FluidHelpers.getAmount(prototype), simulate);
            }

            // AMOUNT: drain only when the exact requested amount is available.
            if (matchFlags == FluidMatch.AMOUNT) {
                FluidStack simulated = storage.drain(prototype.amount, true);

                if (FluidHelpers.isEmpty(simulated) || simulated.amount != prototype.amount) {
                    return FluidHelpers.EMPTY;
                }

                return simulate ? simulated : storage.drain(prototype.amount, false);
            }

            // Other match conditions: inspect each tank's contents.
            for (int tank = 0; tank < storage.getTanks(); tank++) {
                FluidStack contents = storage.getFluidInTank(tank);

                if (!FluidHelpers.isEmpty(contents)
                    && FluidMatch.areFluidStacksEqual(contents, prototype, matchFlags & ~FluidMatch.AMOUNT)) {

                    FluidStack toDrain = contents.copy();
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
            return storage.getTanks();
        }

        @Override
        public FluidStack getSlotContents(int slot) {
            if (slot < 0 || slot >= storage.getTanks()) {
                return FluidHelpers.EMPTY;
            }
            return storage.getFluidInTank(slot);
        }

        @Override
        public long getMaxQuantity(int slot) {
            if (slot < 0 || slot >= storage.getTanks()) {
                return 0;
            }
            return storage.getTankCapacity(slot);
        }

        @Override
        public FluidStack insert(int slot, @Nonnull FluidStack ingredient, boolean simulate) {
            // IFluidHandler has no tank-indexed fill operation.
            return insert(ingredient, simulate);
        }

        @Override
        public FluidStack extract(int slot, long maxQuantity, boolean simulate) {
            if (slot < 0 || slot >= storage.getTanks()) {
                return FluidHelpers.EMPTY;
            }
            FluidStack slotContents = storage.getFluidInTank(slot);
            if (!FluidHelpers.isEmpty(slotContents)) {
                FluidStack toDrain = slotContents.copy();
                toDrain.amount = Helpers.castSafe(maxQuantity);

                return storage.drain(toDrain, simulate);
            }
            return extract(maxQuantity, simulate);
        }
    }

    public static class FluidStorageWrapper implements IFluidHandler {

        private final IIngredientComponentStorage<FluidStack, Integer> storage;

        public FluidStorageWrapper(IIngredientComponentStorage<FluidStack, Integer> storage) {
            this.storage = storage;
        }

        private List<FluidStack> getContents() {
            return Lists.newArrayList(storage);
        }

        @Override
        public int getTanks() {
            return getContents().size();
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            List<FluidStack> contents = getContents();
            if (tank < 0 || tank >= contents.size()) {
                return FluidHelpers.EMPTY;
            }
            FluidStack fluid = contents.get(tank);
            return FluidHelpers.isEmpty(fluid) ? FluidHelpers.EMPTY : fluid;
        }

        @Override
        public int getTankCapacity(int tank) {
            return tank >= 0 && tank < getTanks() ? Integer.MAX_VALUE : 0;
        }

        @Override
        public boolean isFluidValid(int tank, @Nonnull FluidStack fluid) {
            return tank >= 0 && tank < getTanks() && !FluidHelpers.isEmpty(fluid);
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
            if (FluidHelpers.isEmpty(resource)) {
                return FluidHelpers.EMPTY;
            }
            FluidStack simulated = storage.extract(resource, FluidMatch.FLUID | FluidMatch.NBT, true);
            if (FluidHelpers.isEmpty(simulated)) {
                return FluidHelpers.EMPTY;
            }
            FluidStack prototype = resource;
            if (prototype.amount > simulated.amount) {
                prototype = prototype.copy();
                prototype.amount = simulated.amount;
            }
            return storage.extract(prototype, FluidMatch.EXACT, action);
        }

        @Override
        public FluidStack drain(int maxDrain, boolean action) {
            if (maxDrain <= 0) {
                return FluidHelpers.EMPTY;
            }
            FluidStack drained = storage.extract(maxDrain, action);
            return FluidHelpers.isEmpty(drained) ? FluidHelpers.EMPTY : drained;
        }
    }
}
