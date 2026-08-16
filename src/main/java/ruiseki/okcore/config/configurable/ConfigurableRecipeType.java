package ruiseki.okcore.config.configurable;

import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.config.extendedconfig.RecipeTypeConfig;
import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.IRecipeType;

public abstract class ConfigurableRecipeType<T extends IRecipeOK<?>>
    implements IRecipeType<T>, IConfigurable<RecipeTypeConfig<T>> {

    private RecipeTypeConfig<T> eConfig;

    public ConfigurableRecipeType(ExtendedConfig<RecipeTypeConfig<T>> eConfig) {
        this.setConfig(eConfig);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void setConfig(ExtendedConfig eConfig) {
        this.eConfig = (RecipeTypeConfig<T>) eConfig;
    }

    @Override
    public RecipeTypeConfig<T> getConfig() {
        return this.eConfig;
    }
}
