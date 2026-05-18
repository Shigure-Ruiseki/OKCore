package ruiseki.okcore.data.loader.recipes;

import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonObject;

public record RecipeHolder(ResourceLocation id, String type, JsonObject json, String fileName) {}
