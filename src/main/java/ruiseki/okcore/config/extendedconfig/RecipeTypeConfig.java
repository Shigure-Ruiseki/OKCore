package ruiseki.okcore.config.extendedconfig;

import java.util.function.Function;

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
public class RecipeTypeConfig<T extends IRecipeOK<?>> extends ExtendedConfig<RecipeTypeConfig<T>, IRecipeType<T>> {

    /**
     * Full Constructor.
     *
     * @param mod            The mod instance.
     * @param enabled        If this should be enabled by default.
     * @param namedId        A unique name id.
     * @param comment        A comment that can be added to the config file line.
     * @param elementFactory Function factory to create the IRecipeType instance.
     */
    public RecipeTypeConfig(ModBase mod, boolean enabled, String namedId, String comment,
        Function<RecipeTypeConfig<T>, IRecipeType<T>> elementFactory) {
        super(mod, enabled, namedId, comment, elementFactory);
    }

    @Override
    public String getUnlocalizedName() {
        return "recipetype." + getMod().getModId() + "." + getNamedId();
    }

    @Override
    public ConfigurableType getHolderType() {
        return ConfigurableType.RECIPE_TYPE;
    }
}
