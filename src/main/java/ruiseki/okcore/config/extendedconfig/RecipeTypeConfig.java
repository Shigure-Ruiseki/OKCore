package ruiseki.okcore.config.extendedconfig;

import ruiseki.okcore.config.ConfigurableType;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.IRecipeType;

/**
 * Config for recipe serializers.
 *
 * @author rubensworks
 * @see ExtendedConfig
 */
public class RecipeTypeConfig<T extends IRecipeOK<?>> extends ExtendedConfig<RecipeTypeConfig<T>> {

    /**
     * Full Constructor.
     *
     * @param mod     The mod instance.
     * @param enabled If this should be enabled by default.
     * @param namedId A unique name id.
     * @param comment A comment that can be added to the config file line.
     * @param element The class of the recipe serializer.
     */
    public RecipeTypeConfig(ModBase mod, boolean enabled, String namedId, String comment,
        Class<? extends IRecipeType<T>> element) {
        super(mod, enabled, namedId, comment, element);
    }

    @Override
    public String getUnlocalizedName() {
        return "recipetype." + getMod().getModId() + "." + getNamedId();
    }

    @Override
    public ConfigurableType getHolderType() {
        return ConfigurableType.RECIPE_TYPE;
    }

    public IRecipeType<T> getRecipeType() {
        return (IRecipeType<T>) getSubInstance();
    }
}
