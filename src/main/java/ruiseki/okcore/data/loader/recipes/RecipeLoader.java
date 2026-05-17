package ruiseki.okcore.data.loader.recipes;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.data.loader.DataLoader;
import ruiseki.okcore.data.loader.IDataLoader;

@DataLoader
public class RecipeLoader implements IDataLoader {

    @Override
    public String getTargetFolder() {
        return "recipes";
    }

    @Override
    @SuppressWarnings("unchecked, rawtypes")
    public void process(ResourceLocation id, String namespace, String folder, String[] subPaths, String fileName,
        InputStream inputStream) {
        RecipeReader reader = new RecipeReader(id, fileName);
        try {
            IRecipeSerializer material = reader.read(inputStream);
            if (material == null) return;

            if (material.validate()) {
                List<IRecipe> recipes = material.getRecipes();
                RecipeHandler.addRecipes(recipes);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
