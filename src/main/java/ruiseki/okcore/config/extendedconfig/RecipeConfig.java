package ruiseki.okcore.config.extendedconfig;

import java.util.function.Function;

import ruiseki.okcore.config.ConfigurableType;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.IRecipeSerializer;

/**
 * Config for recipe serializers.
 *
 * @author rubensworks
 * @see ExtendedConfig
 */
public class RecipeConfig<T extends IRecipeOK<?>> extends ExtendedConfig<RecipeConfig<T>, IRecipeSerializer<T>> {

    /**
     * Full Constructor.
     *
     * @param mod            The mod instance.
     * @param enabled        If this should be enabled by default.
     * @param namedId        A unique name id.
     * @param comment        A comment that can be added to the config file line.
     * @param elementFactory Function factory to create the IRecipeSerializer instance.
     */
    public RecipeConfig(ModBase mod, boolean enabled, String namedId, String comment,
        Function<RecipeConfig<T>, IRecipeSerializer<T>> elementFactory) {
        super(mod, enabled, namedId, comment, elementFactory);
    }

    @Override
    public String getUnlocalizedName() {
        return "recipe." + getMod().getModId() + "." + getNamedId();
    }

    @Override
    public ConfigurableType getHolderType() {
        return ConfigurableType.RECIPE;
    }
}
