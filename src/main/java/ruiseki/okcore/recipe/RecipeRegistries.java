package ruiseki.okcore.recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.discovery.ASMDataTable;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.data.loader.recipes.RecipeHolder;

public class RecipeRegistries {

    private static final Map<String, IRecipeSerializer<?>> SERIALIZER_MAPPING = new HashMap<>();
    private static final Map<String, IRecipeType<?>> TYPE_MAPPING = new HashMap<>();

    private static final Map<ResourceLocation, RecipeHolder> RECIPE_HOLDERS = new HashMap<>();

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

    public static void addHolder(RecipeHolder holder) {
        RECIPE_HOLDERS.put(holder.id(), holder);
    }

    @SuppressWarnings("unchecked")
    public static List<IRecipeOK<?>> fromHolder(RecipeHolder holder) {
        IRecipeSerializer<?> serializer = RecipeRegistries.getSerializer(holder.type());
        if (serializer == null) {
            return Collections.emptyList();
        }

        try {
            List<IRecipeOK<?>> parsedRecipes = (List<IRecipeOK<?>>) serializer.fromJson(holder.id(), holder.json());
            return parsedRecipes != null ? parsedRecipes : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public static void processGlobalHolders() {
        List<IRecipeOK<?>> allBaseRecipes = new ArrayList<>();

        for (RecipeHolder holder : RECIPE_HOLDERS.values()) {
            List<IRecipeOK<?>> recipes = fromHolder(holder);
            if (!recipes.isEmpty()) {
                allBaseRecipes.addAll(recipes);
            }
        }

        RecipeManager.setupGlobalRecipes(allBaseRecipes);

        OKCore.okLog(Level.INFO, "Registered {} base recipes directly into Global storage.", allBaseRecipes.size());
        RECIPE_HOLDERS.clear();
    }

    public static void processWorldHolders() {
        List<IRecipeOK<?>> allWorldRecipes = new ArrayList<>();

        for (RecipeHolder holder : RECIPE_HOLDERS.values()) {
            List<IRecipeOK<?>> recipes = fromHolder(holder);
            if (!recipes.isEmpty()) {
                allWorldRecipes.addAll(recipes);
            }
        }

        RecipeManager.getManager()
            .addWorldRecipes(allWorldRecipes);

        OKCore.okLog(Level.INFO, "Injected {} custom world recipes into current session.", allWorldRecipes.size());
        RECIPE_HOLDERS.clear();
    }

}
