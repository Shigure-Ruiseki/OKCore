package ruiseki.okcore.config.configurable;

import java.util.function.Function;

import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.config.extendedconfig.RecipeConfig;
import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.type.crafting.SpecialRecipeSerializer;

public class ConfigurableSpecialRecipeSerializer<T extends IRecipeOK<?>> extends SpecialRecipeSerializer<T>
    implements IConfigurable<RecipeConfig<T>> {

    private RecipeConfig<T> eConfig;

    public ConfigurableSpecialRecipeSerializer(ExtendedConfig<RecipeConfig<T>> eConfig,
        Function<ResourceLocation, T> constructor) {
        super(constructor);
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
