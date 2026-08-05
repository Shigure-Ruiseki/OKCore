package ruiseki.okcore.recipe;

import net.minecraft.util.ResourceLocation;

public interface IRecipeType<T extends IRecipeOK<?>> {

    public static <T extends IRecipeOK<?>> IRecipeType<T> simple(final ResourceLocation name) {
        return new IRecipeType<T>() {

            @Override
            public String toString() {
                return name.toString();
            }
        };
    }

    public static <S extends IRecipeType<T>, T extends IRecipeOK<?>> S register(String key, S type) {
        return RecipeRegistry.registerType(new ResourceLocation(key), type);
    }
}
