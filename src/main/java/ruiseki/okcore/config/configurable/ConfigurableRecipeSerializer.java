package ruiseki.okcore.config.configurable;

import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.config.extendedconfig.RecipeConfig;
import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.IRecipeSerializer;

public abstract class ConfigurableRecipeSerializer<T extends IRecipeOK<?>>
    implements IRecipeSerializer<T>, IConfigurable<RecipeConfig<T>> {

    private RecipeConfig<T> eConfig;

    public ConfigurableRecipeSerializer(ExtendedConfig<RecipeConfig<T>> eConfig) {
        this.setConfig(eConfig);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void setConfig(ExtendedConfig eConfig) {
        this.eConfig = (RecipeConfig<T>) eConfig;
    }

    @Override
    public RecipeConfig<T> getConfig() {
        return this.eConfig;
    }
}
