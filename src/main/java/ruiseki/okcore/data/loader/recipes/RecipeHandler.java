package ruiseki.okcore.data.loader.recipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.crafting.IRecipe;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.init.IInitListener;

public class RecipeHandler implements IInitListener {

    private static final Map<String, Class<? extends AbstractRecipeMaterial>> MATERIAL_MAPPING = new HashMap<>();
    private static final List<IRecipe> CACHED_RECIPES = new ArrayList<>();

    public static void loadFromASM(ASMDataTable asmDataTable) {
        Set<ASMDataTable.ASMData> asmDataSet = asmDataTable.getAll(RecipeType.class.getName());

        for (ASMDataTable.ASMData asmData : asmDataSet) {
            try {
                Class<?> clazz = Class.forName(asmData.getClassName());

                if (AbstractRecipeMaterial.class.isAssignableFrom(clazz)) {
                    RecipeType annotation = clazz.getAnnotation(RecipeType.class);
                    if (annotation != null) {
                        @SuppressWarnings("unchecked")
                        Class<? extends AbstractRecipeMaterial> materialClass = (Class<? extends AbstractRecipeMaterial>) clazz;
                        MATERIAL_MAPPING.put(annotation.value(), materialClass);
                    }
                }
            } catch (Exception e) {
                OKCore.okLog(
                    Level.ERROR,
                    "Failed to initialize ASM RecipeMaterial [{}]: {}",
                    asmData.getClassName(),
                    e.toString());
            }
        }
    }

    public static AbstractRecipeMaterial createMaterial(String type) {
        Class<? extends AbstractRecipeMaterial> clazz = MATERIAL_MAPPING.get(type);
        if (clazz != null) {
            try {
                return clazz.getDeclaredConstructor()
                    .newInstance();
            } catch (Exception e) {
                OKCore.okLog(Level.ERROR, "Failed to instantiate Recipe Material for type {}: {}", type, e.toString());
            }
        }
        return null;
    }

    public static void addRecipe(IRecipe recipe) {
        CACHED_RECIPES.add(recipe);
    }

    public static void addRecipes(List<IRecipe> recipe) {
        CACHED_RECIPES.addAll(recipe);
    }

    @Override
    public void onInit(Step initStep) {
        if (initStep == Step.POSTINIT) {
            if (CACHED_RECIPES.isEmpty()) {
                OKCore.okLog(Level.INFO, "No custom recipes found to register.");
                return;
            }

            int count = 0;
            for (IRecipe recipe : CACHED_RECIPES) {
                if (recipe != null) {
                    GameRegistry.addRecipe(recipe);
                    count++;
                }
            }

            OKCore.okLog(Level.INFO, "Successfully registered {} custom recipes to GameRegistry.", count);
            CACHED_RECIPES.clear();
        }
    }
}
