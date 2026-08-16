package ruiseki.commoncapabilities.capability.recipehandler;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.commoncapabilities.api.capability.recipehandler.IRecipeHandler;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

/**
 * Config for the recipe handler capability.
 * 
 * @author rubensworks
 */
public class RecipeHandlerConfig extends CapabilityConfig<IRecipeHandler> {

    /**
     * The unique instance.
     */
    public static RecipeHandlerConfig _instance;

    @CapabilityInject(IRecipeHandler.class)
    public static Capability<IRecipeHandler> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public RecipeHandlerConfig() {
        super(
            CommonCapabilities._instance,
            true,
            "recipeHandler",
            "Something that is able to process recipes",
            IRecipeHandler.class);
    }
}
