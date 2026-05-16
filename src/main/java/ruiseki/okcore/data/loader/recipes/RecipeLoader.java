package ruiseki.okcore.data.loader.recipes;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.minecraft.item.crafting.IRecipe;

import ruiseki.okcore.data.loader.DataLoader;
import ruiseki.okcore.data.loader.IDataLoader;

@DataLoader
public class RecipeLoader implements IDataLoader {

    @Override
    public String getTargetFolder() {
        return "recipes";
    }

    @Override
    public void process(String namespace, String folder, String[] subPath, String fileName, File json) {
        RecipeReader reader = new RecipeReader(json);
        try {
            AbstractRecipeMaterial material = reader.read();
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
