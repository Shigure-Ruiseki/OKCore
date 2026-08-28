package ruiseki.commoncapabilities.ingredient.storage;

import java.util.Iterator;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.Iterators;

import cofh.api.energy.IEnergyStorage;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorageWrapperHandler;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.energy.capability.CapabilityEnergy;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.ingredient.collection.FilteredIngredientCollectionIterator;

/**
 * Energy storage wrapper handler for {@link IEnergyStorage}.
 *
 * @author rubensworks
 */
public class IngredientComponentStorageWrapperHandlerEnergyStorage
    implements IIngredientComponentStorageWrapperHandler<Long, Boolean, IEnergyStorage> {

    private final IngredientComponent<Long, Boolean> ingredientComponent;

    public IngredientComponentStorageWrapperHandlerEnergyStorage(
        IngredientComponent<Long, Boolean> ingredientComponent) {
        this.ingredientComponent = Objects.requireNonNull(ingredientComponent);
    }

    @Override
    public IIngredientComponentStorage<Long, Boolean> wrapComponentStorage(IEnergyStorage storage) {
        return new ComponentStorageWrapper(getComponent(), storage);
    }

    @Override
    public IEnergyStorage wrapStorage(IIngredientComponentStorage<Long, Boolean> componentStorage) {
        return new EnergyStorageWrapper(componentStorage);
    }

    @Nullable
    @Override
    public IEnergyStorage getStorage(ICapabilityProvider capabilityProvider, @Nullable ForgeDirection facing) {
        return capabilityProvider.getCapability(CapabilityEnergy.ENERGY, facing)
            .getOrNull();
    }

    @Override
    public IngredientComponent<Long, Boolean> getComponent() {
        return this.ingredientComponent;
    }

    public static class ComponentStorageWrapper implements IIngredientComponentStorage<Long, Boolean> {

        private final IngredientComponent<Long, Boolean> ingredientComponent;
        private final IEnergyStorage storage;

        public ComponentStorageWrapper(IngredientComponent<Long, Boolean> ingredientComponent, IEnergyStorage storage) {
            this.ingredientComponent = ingredientComponent;
            this.storage = storage;
        }

        @Override
        public IngredientComponent<Long, Boolean> getComponent() {
            return this.ingredientComponent;
        }

        @Override
        public @NotNull Iterator<Long> iterator() {
            return Iterators.forArray((long) storage.getEnergyStored());
        }

        @Override
        public Iterator<Long> iterator(@Nonnull Long prototype, Boolean matchFlags) {
            return new FilteredIngredientCollectionIterator<>(
                iterator(),
                getComponent().getMatcher(),
                prototype,
                matchFlags);
        }

        @Override
        public long getMaxQuantity() {
            return storage.getMaxEnergyStored();
        }

        @Override
        public Long insert(@Nonnull Long ingredient, boolean simulate) {
            int toReceive = Helpers.castSafe(ingredient);
            return ingredient - storage.receiveEnergy(toReceive, simulate);
        }

        @Override
        public Long extract(@Nonnull Long prototype, Boolean matchFlags, boolean simulate) {
            int toExtract = Helpers.castSafe(prototype);
            if (Boolean.TRUE.equals(matchFlags)) {
                int extractable = storage.extractEnergy(toExtract, true);
                if (extractable != toExtract) {
                    return 0L;
                }
            }
            return (long) storage.extractEnergy(toExtract, simulate);
        }

        @Override
        public Long extract(long maxQuantity, boolean simulate) {
            return (long) storage.extractEnergy(Helpers.castSafe(maxQuantity), simulate);
        }
    }

    public static class EnergyStorageWrapper implements IEnergyStorage {

        private final IIngredientComponentStorage<Long, Boolean> storage;

        public EnergyStorageWrapper(IIngredientComponentStorage<Long, Boolean> storage) {
            this.storage = storage;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            Long notInserted = storage.insert((long) maxReceive, simulate);
            return maxReceive - Helpers.castSafe(notInserted);
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            Long extracted = storage.extract((long) maxExtract, simulate);
            return Helpers.castSafe(extracted);
        }

        @Override
        public int getEnergyStored() {
            long total = 0;
            for (Long stored : storage) {
                if (stored != null) {
                    total += stored;
                }
            }
            return Helpers.castSafe(total);
        }

        @Override
        public int getMaxEnergyStored() {
            return Helpers.castSafe(storage.getMaxQuantity());
        }
    }
}
