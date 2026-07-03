package ruiseki.okcore.data.condition;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import ruiseki.okcore.helper.GsonHelpers;
import ruiseki.okcore.helper.Helpers;

public class ConditionRegistry {

    public static final String CONDITION_KEY = "conditions";
    private static final Map<ResourceLocation, IConditionSerializer<?>> conditions = new HashMap<>();

    static {
        register(AndCondition.Serializer.INSTANCE);
        register(FalseCondition.Serializer.INSTANCE);
        register(ItemExistsCondition.Serializer.INSTANCE);
        register(ModLoadedCondition.Serializer.INSTANCE);
        register(NotCondition.Serializer.INSTANCE);
        register(OrCondition.Serializer.INSTANCE);
        register(TrueCondition.Serializer.INSTANCE);
        register(ConfigCondition.Serializer.INSTANCE);
    }

    public static IConditionSerializer<?> register(IConditionSerializer<?> serializer) {
        ResourceLocation key = serializer.getID();
        if (conditions.containsKey(key))
            throw new IllegalStateException("Duplicate recipe condition serializer: " + key);
        conditions.put(key, serializer);
        return serializer;
    }

    public static boolean checkConditional(JsonObject json) {
        return processConditions(json, CONDITION_KEY, ICondition.IContext.EMPTY);
    }

    public static boolean processConditions(JsonObject json, String memberName, ICondition.IContext context) {
        if (json == null || !json.has(memberName)) return true;
        return processConditions(GsonHelpers.getAsJsonArray(json, memberName), context);
    }

    public static boolean processConditions(JsonArray conditions, ICondition.IContext context) {
        for (int x = 0; x < conditions.size(); x++) {
            if (!conditions.get(x)
                .isJsonObject()) throw new JsonSyntaxException("Conditions must be an array of JsonObjects");

            JsonObject json = conditions.get(x)
                .getAsJsonObject();
            if (!getCondition(json).test(context)) return false;
        }
        return true;
    }

    public static ICondition getCondition(JsonObject json) {
        ResourceLocation type = Helpers.parseLocation(GsonHelpers.getAsString(json, "type"));
        IConditionSerializer<?> serializer = conditions.get(type);
        if (serializer == null) throw new JsonSyntaxException("Unknown condition type: " + type.toString());
        return serializer.read(json);
    }

    public static <T extends ICondition> JsonObject serialize(T condition) {
        @SuppressWarnings("unchecked")
        IConditionSerializer<T> serializer = (IConditionSerializer<T>) conditions.get(condition.getID());
        if (serializer == null) throw new JsonSyntaxException(
            "Unknown condition type: " + condition.getID()
                .toString());
        return serializer.getJson(condition);
    }

    public static JsonArray serialize(ICondition... conditions) {
        JsonArray arr = new JsonArray();
        for (ICondition iCond : conditions) {
            arr.add(serialize(iCond));
        }
        return arr;
    }
}
