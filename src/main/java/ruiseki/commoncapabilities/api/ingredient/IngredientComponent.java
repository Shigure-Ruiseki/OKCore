package ruiseki.commoncapabilities.api.ingredient;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.gtnewhorizon.gtnhlib.eventbus.EventBusSubscriber;
import com.gtnewhorizon.gtnhlib.eventbus.Phase;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.commoncapabilities.api.ingredient.capability.AttachCapabilitiesEventIngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorageHandler;
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorageWrapperHandler;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityDispatcher;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.registries.IForgeRegistry;
import ruiseki.okcore.registries.IForgeRegistryEntry;
import ruiseki.okcore.registries.RegistryBuilder;
import ruiseki.okcore.registries.RegistryEvent;

/**
 * A IngredientComponent is a type of component that can be used as ingredients inside recipes.
 *
 * @param <T> The instance type.
 * @param <M> The matching condition parameter, may be Void. Instances MUST properly implement the equals method.
 * @author rubensworks
 */
@EventBusSubscriber(phase = Phase.CONSTRUCT)
public final class IngredientComponent<T, M>
    implements IForgeRegistryEntry<IngredientComponent<?, ?>>, Comparable<IngredientComponent<?, ?>> {

    public static IForgeRegistry<IngredientComponent<?, ?>> REGISTRY;

    @SubscribeEvent
    public static void onRegistriesCreate(RegistryEvent.NewRegistry event) {
        REGISTRY = new RegistryBuilder<IngredientComponent<?, ?>>()
            .setName(new ResourceLocation("commoncapabilities", "registry:ingredientcomponents"))
            .setType((Class<IngredientComponent<?, ?>>) (Class) IngredientComponent.class)
            .create();
    }

    public static IngredientComponent<ItemStack, Integer> ITEMSTACK = null;
    public static IngredientComponent<FluidStack, Integer> FLUIDSTACK = null;
    public static IngredientComponent<Long, Boolean> ENERGY = null;

    @CapabilityInject(IIngredientComponentStorageHandler.class)
    private static Capability<IIngredientComponentStorageHandler> CAPABILITY_INGREDIENT_COMPONENT_STORAGE_HANDLER = null;

    private static Map<Capability<?>, IngredientComponent<?, ?>> STORAGE_WRAPPER_CAPABILITIES_COMPONENTS = Maps
        .newIdentityHashMap();

    private final IIngredientMatcher<T, M> matcher;
    private final IIngredientSerializer<T, M> serializer;
    private final List<IngredientComponentCategoryType<T, M, ?>> categoryTypes;
    private final List<Capability<?>> storageWrapperCapabilities;
    private final Map<Capability<?>, IIngredientComponentStorageWrapperHandler<T, M, ?>> storageWrapperHandler;
    private final IngredientComponentCategoryType<T, M, ?> primaryQuantifier;
    private CapabilityDispatcher capabilityDispatcher;
    private ResourceLocation name;
    private String translationKey;

    public IngredientComponent(ResourceLocation name, IIngredientMatcher<T, M> matcher,
        IIngredientSerializer<T, M> serializer, List<IngredientComponentCategoryType<T, M, ?>> categoryTypes) {
        this.setRegistryName(name);
        this.matcher = matcher;
        this.serializer = serializer;
        this.categoryTypes = Lists.newArrayList(categoryTypes);
        this.storageWrapperCapabilities = Lists.newArrayList();
        this.storageWrapperHandler = Maps.newIdentityHashMap();
        this.capabilityDispatcher = gatherCapabilities();

        // Validate if the combination of all match conditions equals the exact match condition.
        M matchCondition = this.matcher.getAnyMatchCondition();
        IngredientComponentCategoryType<T, M, ?> primaryQuantifier = null;
        for (IngredientComponentCategoryType<T, M, ?> categoryType : this.categoryTypes) {
            matchCondition = this.matcher.withCondition(matchCondition, categoryType.getMatchCondition());
            if (categoryType.isPrimaryQuantifier()) {
                if (primaryQuantifier != null) {
                    throw new IllegalArgumentException("Found more than one primary quantifier in category types.");
                }
                primaryQuantifier = categoryType;
            }
        }
        this.primaryQuantifier = primaryQuantifier;
        if (!Objects.equals(
            matchCondition,
            this.getMatcher()
                .getExactMatchCondition())) {
            throw new IllegalArgumentException(
                "The given category types when combined do not conform to the "
                    + "exact match conditions of the matcher.");
        }
    }

    public IngredientComponent(String name, IIngredientMatcher<T, M> matcher, IIngredientSerializer<T, M> serializer,
        List<IngredientComponentCategoryType<T, M, ?>> categoryTypes) {
        this(new ResourceLocation(name), matcher, serializer, categoryTypes);
    }

    public ResourceLocation getName() {
        return name;
    }

    @Override
    public String toString() {
        return "[IngredientComponent " + this.name + " " + hashCode() + "]";
    }

    @Override
    public IngredientComponent<?, ?> setRegistryName(ResourceLocation name) {
        this.name = name;
        return this;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return name;
    }

    @Override
    public Class<IngredientComponent<?, ?>> getRegistryType() {
        return (Class) IngredientComponent.class;
    }

    protected CapabilityDispatcher gatherCapabilities() {
        AttachCapabilitiesEventIngredientComponent<T, M> event = new AttachCapabilitiesEventIngredientComponent<>(this);
        MinecraftForge.EVENT_BUS.post(event);
        return !event.getCapabilities()
            .isEmpty() ? new CapabilityDispatcher(event.getCapabilities(), event.getListeners()) : null;
    }

    /**
     * Get the given capability.
     *
     * @param capability The capability to get.
     * @param <TC>       The capability type.
     * @return The capability instance.
     */
    public <TC> LazyOptional<TC> getCapability(Capability<TC> capability) {
        return capabilityDispatcher == null ? LazyOptional.empty()
            : capabilityDispatcher.getCapability(capability, null);
    }

    public IngredientComponent<T, M> setTranslationKey(String translationKey) {
        this.translationKey = translationKey;
        return this;
    }

    /**
     * @return The unlocalized name.
     */
    public String getTranslationKey() {
        return translationKey;
    }

    /**
     * @return A matcher instance for comparing instances of this component type.
     */
    public IIngredientMatcher<T, M> getMatcher() {
        return matcher;
    }

    /**
     * @return A (de)serializer instance for this component type.
     */
    public IIngredientSerializer<T, M> getSerializer() {
        return serializer;
    }

    /**
     * @return The category types of this component.
     */
    public List<IngredientComponentCategoryType<T, M, ?>> getCategoryTypes() {
        return categoryTypes;
    }

    /**
     * Wrap the given instance inside an equals, hashCode and compareTo-safe holder.
     *
     * @param instance An instance.
     * @return The wrapped instance.
     */
    public IngredientInstanceWrapper<T, M> wrap(T instance) {
        return new IngredientInstanceWrapper<>(this, instance);
    }

    /**
     * @return The primary quantifier category type, can be null.
     */
    @Nullable
    public IngredientComponentCategoryType<T, M, ?> getPrimaryQuantifier() {
        return primaryQuantifier;
    }

    /**
     * Set the storage wrapper handler for this component.
     *
     * @param capability            The capability for the storage wrapper.
     * @param storageWrapperHandler The storage wrapper handler.
     * @param <S>                   The capability type.
     */
    public <S> void setStorageWrapperHandler(Capability<S> capability,
        IIngredientComponentStorageWrapperHandler<T, M, ? super S> storageWrapperHandler) {
        if (capability != null && this.storageWrapperHandler.put(capability, storageWrapperHandler) == null) {
            this.storageWrapperCapabilities.add(capability);
            IngredientComponent<?, ?> previousValue = IngredientComponent.STORAGE_WRAPPER_CAPABILITIES_COMPONENTS
                .put(capability, this);
            if (previousValue != null) {
                throw new IllegalStateException(
                    String.format(
                        "Tried registering a storage capability (%s) for %s that was already registered to %s",
                        capability.getName(),
                        this,
                        previousValue));
            }
        }
    }

    /**
     * Get the storage wrapper handler for this component.
     *
     * @param capability The capability to get the storage wrapper for.
     * @param <S>        The external storage type.
     * @return The storage wrapper handler, can be null if none has been assigned.
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public <S> IIngredientComponentStorageWrapperHandler<T, M, S> getStorageWrapperHandler(Capability<S> capability) {
        return (IIngredientComponentStorageWrapperHandler<T, M, S>) storageWrapperHandler.get(capability);
    }

    /**
     * @return All supported capabilities that have registered wrapper handlers
     */
    public Collection<Capability<?>> getStorageWrapperHandlerCapabilities() {
        return this.storageWrapperCapabilities;
    }

    /**
     * Get the ingredient component that was attached to the given storage capability.
     *
     * @param capability A storage capability.
     * @return The attached ingredient component, or null.
     */
    @Nullable
    public static IngredientComponent<?, ?> getIngredientComponentForStorageCapability(Capability<?> capability) {
        return IngredientComponent.STORAGE_WRAPPER_CAPABILITIES_COMPONENTS.get(capability);
    }

    /**
     * Get the ingredient storage within the given capability provider.
     *
     * This will first check if the capability provider has a {@link IIngredientComponentStorageHandler} capability
     * and it will try to fetch the storage from there.
     * If this fails, then the storage wrapper handlers that are registered in this ingredient component
     * will be iterated and the first successful one will be returned.
     *
     * @param capabilityProvider A capability provider.
     * @param facing             The side to get the storage from.
     * @return An ingredient storage, or null if it does not exist.
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public IIngredientComponentStorage<T, M> getStorage(ICapabilityProvider capabilityProvider,
        @Nullable ForgeDirection facing) {
        // Check IIngredientComponentStorageHandler capability
        if (capabilityProvider.getCapability(CAPABILITY_INGREDIENT_COMPONENT_STORAGE_HANDLER, facing)
            .isPresent()) {
            IIngredientComponentStorageHandler storageHandler = capabilityProvider
                .getCapability(CAPABILITY_INGREDIENT_COMPONENT_STORAGE_HANDLER, facing)
                .getOrNull();
            IIngredientComponentStorage<T, M> storage = storageHandler.getStorage(this);
            if (storage != null) {
                return storage;
            }
        }

        // Check registered wrapper handlers
        for (Capability<?> capability : getStorageWrapperHandlerCapabilities()) {
            IIngredientComponentStorageWrapperHandler<T, M, ?> wrapperHandler = getStorageWrapperHandler(capability);
            IIngredientComponentStorage<T, M> storage = wrapperHandler.getComponentStorage(capabilityProvider, facing);
            if (storage != null) {
                return storage;
            }
        }

        // Otherwise, fail
        return null;
    }

    @Override
    public int compareTo(IngredientComponent<?, ?> that) {
        return this.getRegistryName()
            .toString()
            .compareTo(
                that.getRegistryName()
                    .toString());
    }
}
