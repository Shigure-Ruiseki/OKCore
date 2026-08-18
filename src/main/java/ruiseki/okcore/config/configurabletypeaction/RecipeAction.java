package ruiseki.okcore.config.configurabletypeaction;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import ruiseki.okcore.config.extendedconfig.RecipeConfig;
import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.IRecipeSerializer;

/**
 * The action used for {@link RecipeConfig}.
 *
 * @author rubensworks
 * @see ConfigurableTypeAction
 */
public class RecipeAction<T extends IRecipeOK<?>>
    extends ConfigurableTypeAction<RecipeConfig<T>, IRecipeSerializer<T>> {

    /**
     * Registers a recipe serializer.
     *
     * @param serializer The serializer instance.
     * @param key        The unique key (e.g., "modid:name").
     */
    public static <T extends IRecipeOK<?>> void register(String key, IRecipeSerializer<T> serializer) {
        if (serializer != null) {
            IRecipeSerializer.register(key, serializer);
        }
    }

    @Override
    public void preRun(RecipeConfig<T> eConfig, Configuration config, boolean startup) {
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
    public void postRun(RecipeConfig<T> eConfig, Configuration config) {
        // Save the config inside the correct element
        eConfig.save();

        IRecipeSerializer<T> serializer = eConfig.getInstance();
        String key = eConfig.getMod()
            .getModId() + ":"
            + eConfig.getNamedId();

        // Register the recipe serializer
        register(key, serializer);
    }
}
