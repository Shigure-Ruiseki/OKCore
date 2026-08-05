package ruiseki.commoncapabilities.capability.ingredient.storage;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorageHandler;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

/**
 * Config for the {@link IIngredientComponentStorageHandler} capability.
 * 
 * @author rubensworks
 */
public class IngredientComponentStorageHandlerConfig extends CapabilityConfig<IIngredientComponentStorageHandler> {

    /**
     * The unique instance.
     */
    public static IngredientComponentStorageHandlerConfig _instance;

    @CapabilityInject(IIngredientComponentStorageHandler.class)
    public static Capability<IIngredientComponentStorageHandler> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public IngredientComponentStorageHandlerConfig() {
        super(
            CommonCapabilities._instance,
            true,
            "inventoryState",
            "Holds ingredient component storages",
            IIngredientComponentStorageHandler.class);
    }
}
