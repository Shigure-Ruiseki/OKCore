package ruiseki.commoncapabilities.ingredient.storage;

import java.util.Iterator;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.Iterators;

import cofh.api.energy.IEnergyStorage;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorageWrapperHandler;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.datastructure.LazyOptional;
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
    public LazyOptional<IEnergyStorage> getStorage(ICapabilityProvider capabilityProvider,
        @Nullable ForgeDirection facing) {
        return capabilityProvider.getCapability(CapabilityEnergy.ENERGY, facing);
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
        public Iterator<Long> iterator() {
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
            return ingredient - storage.receiveEnergy(Helpers.castSafe(ingredient), simulate);
        }

        @Override
        public Long extract(@Nonnull Long prototype, Boolean matchFlags, boolean simulate) {
            if (matchFlags) {
                int extractable = storage.extractEnergy(Helpers.castSafe(prototype), true);
                if (extractable != prototype) {
                    return 0L;
                }
            }
            return (long) storage.extractEnergy(Helpers.castSafe(prototype), simulate);
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
            return maxReceive - Helpers.castSafe(storage.insert((long) maxReceive, simulate));
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return Helpers.castSafe(storage.extract(maxExtract, simulate));
        }

        @Override
        public int getEnergyStored() {
            long total = 0;
            for (Long stored : storage) {
                total = Math.addExact(total, stored);
            }
            return Helpers.castSafe(total);
        }

        @Override
        public int getMaxEnergyStored() {
            return Helpers.castSafe(storage.getMaxQuantity());
        }
    }
}
