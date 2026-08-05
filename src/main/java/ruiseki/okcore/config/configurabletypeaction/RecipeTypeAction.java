package ruiseki.okcore.config.configurabletypeaction;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import ruiseki.okcore.config.extendedconfig.RecipeTypeConfig;
import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.IRecipeType;

/**
 * The action used for {@link RecipeTypeConfig}.
 *
 * @author rubensworks
 * @see ConfigurableTypeAction
 */
public class RecipeTypeAction<T extends IRecipeOK<?>> extends ConfigurableTypeAction<RecipeTypeConfig<T>> {

    /**
     * Registers a recipe serializer.
     *
     * @param type The type instance.
     * @param key  The unique key (e.g., "modid:name").
     */
    public static <T extends IRecipeOK<?>> void register(String key, IRecipeType<T> type) {
        if (type != null) {
            IRecipeType.register(key, type);
        }
    }

    @Override
    public void preRun(RecipeTypeConfig<T> eConfig, Configuration config, boolean startup) {
        // Get property in config file and set comment
        Property property = config.get(
            eConfig.getHolderType()
                .getCategory(),
            eConfig.getNamedId(),
            eConfig.isEnabled());
        property.setRequiresMcRestart(true);
        property.comment = eConfig.getComment();

        if (startup) {
            // Update the enabled state from config file
            eConfig.setEnabled(property.getBoolean(true));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void postRun(RecipeTypeConfig<T> eConfig, Configuration config) {
        // Save the config inside the correct element
        eConfig.save();

        IRecipeType<T> type = (IRecipeType<T>) eConfig.getSubInstance();
        String key = eConfig.getMod()
            .getModId() + ":"
            + eConfig.getNamedId();

        // Register the recipe type
        register(key, type);
    }
}
