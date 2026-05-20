package ruiseki.okcore.data.loader.recipes;

import java.io.IOException;
import java.io.InputStream;

import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.data.loader.DataLoader;
import ruiseki.okcore.data.loader.IDataLoader;
import ruiseki.okcore.recipe.RecipeRegistry;

@DataLoader
public class RecipeLoader implements IDataLoader {

    @Override
    public String getTargetFolder() {
        return "recipes";
    }

    @Override
    public void process(ResourceLocation id, String namespace, String folder, String[] subPaths, String fileName,
        InputStream inputStream) {
        RecipeReader reader = new RecipeReader(id, fileName);
        try {
            RecipeHolder holder = reader.read(inputStream);
            if (holder == null) return;
            RecipeRegistry.addHolder(holder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isWorldLoader() {
        return true;
    }
}
