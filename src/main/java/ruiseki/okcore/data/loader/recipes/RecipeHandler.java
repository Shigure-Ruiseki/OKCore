package ruiseki.okcore.data.loader.recipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.crafting.IRecipe;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.init.IInitListener;

public class RecipeHandler implements IInitListener {

    private static final Map<String, IRecipeSerializer<?>> SERIALIZER_MAPPING = new HashMap<>();
    private static final Map<String, IRecipeType<?>> TYPE_MAPPING = new HashMap<>();
    private static final List<IRecipe> CACHED_RECIPES = new ArrayList<>();

    public static void loadFromASM(ASMDataTable dataTable) {
        for (ASMDataTable.ASMData data : dataTable.getAll(RecipeData.class.getCanonicalName())) {
            try {
                Class<?> clazz = Class.forName(data.getClassName());

                boolean isType = IRecipeType.class.isAssignableFrom(clazz);
                boolean isSerializer = IRecipeSerializer.class.isAssignableFrom(clazz);

                if (!isType && !isSerializer) continue;

                Object instance = clazz.getDeclaredConstructor()
                    .newInstance();
                boolean registeredAny = false;

                if (isType) {
                    IRecipeType<?> recipeType = (IRecipeType<?>) instance;
                    if (recipeType.shouldRegisterType()) {
                        registerType(recipeType);
                        registeredAny = true;
                    } else {
                        OKCore
                            .okLog(Level.INFO, "Skipping recipe type: {} (Condition not met)", recipeType.getTypeKey());
                    }
                }

                if (isSerializer) {
                    IRecipeSerializer<?> recipeSerializer = (IRecipeSerializer<?>) instance;
                    if (recipeSerializer.shouldRegisterSerializer()) {
                        registerSerializer(recipeSerializer);
                        registeredAny = true;
                    } else {
                        OKCore.okLog(
                            Level.INFO,
                            "Skipping recipe serializer: {} (Condition not met)",
                            recipeSerializer.getTypeKey());
                    }
                }

                if (registeredAny) {
                    OKCore.okLog(
                        Level.INFO,
                        "Successfully registered @RecipeData components for class: [{}]",
                        data.getClassName());
                }
            } catch (Exception e) {
                OKCore.okLog(
                    Level.ERROR,
                    "Failed to initialize ASM RecipeHandler [{}]: {}",
                    data.getClassName(),
                    e.toString());
            }
        }
    }

    public static IRecipeType<?> registerType(IRecipeType<?> recipeType) {
        if (recipeType == null || recipeType.getTypeKey() == null) return null;
        String key = recipeType.getTypeKey();
        return TYPE_MAPPING.put(key, recipeType);
    }

    public static IRecipeSerializer<?> registerSerializer(IRecipeSerializer<?> recipeSerializer) {
        if (recipeSerializer == null || recipeSerializer.getTypeKey() == null) return null;
        String key = recipeSerializer.getTypeKey();
        return SERIALIZER_MAPPING.put(key, recipeSerializer);
    }

    public static IRecipeType<?> getType(String key) {
        if (key == null) return null;
        return TYPE_MAPPING.get(key);
    }

    public static Map<String, IRecipeType<?>> getTypeMapping() {
        return TYPE_MAPPING;
    }

    public static IRecipeSerializer<?> getSerializer(String type) {
        if (type == null) return null;
        return SERIALIZER_MAPPING.get(type);
    }

    public static Map<String, IRecipeSerializer<?>> getSerializerMapping() {
        return SERIALIZER_MAPPING;
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
