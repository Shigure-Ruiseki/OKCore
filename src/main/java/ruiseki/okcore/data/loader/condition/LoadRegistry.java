package ruiseki.okcore.data.loader.condition;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cpw.mods.fml.common.discovery.ASMDataTable;
import ruiseki.okcore.OKCore;

public class LoadRegistry {

    public static final String CONDITION_KEY = "okcore:load_conditions";
    private static final Map<String, ILoadCondition> CONDITION_MAPPING = new HashMap<>();

    public static void loadFromASM(ASMDataTable asmDataTable) {
        Set<ASMDataTable.ASMData> asmDataSet = asmDataTable.getAll(LoadCondition.class.getName());

        for (ASMDataTable.ASMData asmData : asmDataSet) {
            try {
                Class<?> clazz = Class.forName(asmData.getClassName());

                if (ILoadCondition.class.isAssignableFrom(clazz)) {
                    LoadCondition annotation = clazz.getAnnotation(LoadCondition.class);
                    if (annotation != null) {
                        Object instance = clazz.getDeclaredConstructor()
                            .newInstance();
                        ILoadCondition condition = (ILoadCondition) instance;
                        if (condition.shouldRegisterType()) {
                            CONDITION_MAPPING.put(condition.getID(), condition);
                        } else {
                            OKCore.okLog(
                                Level.INFO,
                                "Skipping condition type: {} (Condition not met)",
                                condition.getID());
                        }
                    }
                }
            } catch (Exception e) {
                OKCore.okLog(
                    Level.ERROR,
                    "Failed to initialize ASM LoadCondition [{}]: {}",
                    asmData.getClassName(),
                    e.toString());
            }
        }
    }

    public static ILoadCondition createConditionInstance(JsonObject condElement) {
        if (!condElement.has("type")) {
            OKCore.okLog(Level.WARN, "Missing 'type' field in load condition JSON.");
            return null;
        }

        String type = condElement.get("type")
            .getAsString();
        ILoadCondition condition = CONDITION_MAPPING.get(type);
        if (condition == null) {
            OKCore.okLog(Level.WARN, "Unknown load condition type: {}", type);
            return null;
        }

        try {
            return condition;
        } catch (Exception e) {
            OKCore.okLog(Level.ERROR, "Failed to instantiate condition [{}]: {}", type, e.toString());
            return null;
        }
    }

    public static boolean checkSingleCondition(JsonObject condElement) {
        ILoadCondition condition = createConditionInstance(condElement);
        if (condition == null) {
            return false;
        }

        try {
            return condition.test(condElement);
        } catch (Exception e) {
            OKCore.okLog(Level.ERROR, "Failed to evaluate condition: {}", e.toString());
            return false;
        }
    }

    /**
     * Evaluates custom load conditions defined within a single JSON object.
     * <p>
     * This method acts as a flexible entry point and handles two distinct JSON structures:
     * </p>
     * <ul>
     * <li><strong>Wrapper Format:</strong> The object contains the ("conditions")
     * mapping to a {@link JsonArray} of nested condition objects. In this case, it iterates and evaluates
     * each nested object internally to avoid dependency on legacy array handlers.</li>
     * <li><strong>Single Condition Format:</strong> The object itself represents a specific concrete condition
     * (e.g., {@code {"type": "mod_loaded", "modid": "jei"}}). It will be evaluated directly via
     * {@link LoadRegistry#checkSingleCondition(JsonObject)}.</li>
     * </ul>
     * <p>
     * <strong>CRITICAL LIFECYCLE NOTE:</strong> This method must <strong>ONLY</strong> be invoked
     * during or after the {@code POSTINIT} stage. Checking conditions (such as mod presence, item
     * existence, or fluid registration) before all registries are fully frozen and populated will
     * result in false negatives, improperly skipping valid data files.
     * </p>
     *
     * @param id           The {@link ResourceLocation} registry name or identifier of the resource being evaluated
     *                     (used strictly for logging and tracking purposes).
     * @param conditionObj The {@link JsonObject} containing either the wrapped condition array or the raw
     *                     single condition properties. Passing null or an empty object returns {@code true}.
     * @return {@code true} if the condition (or all nested conditions inside the array) is successfully met,
     *         or if the object is null/empty; {@code false} if any single condition evaluation fails,
     *         indicating the resource should be skipped.
     */
    public static boolean checkConditional(ResourceLocation id, JsonObject conditionObj) {
        if (conditionObj == null || conditionObj.isJsonNull()) {
            return true;
        }
        if (conditionObj.has(CONDITION_KEY)) {
            JsonElement condElement = conditionObj.get(CONDITION_KEY);
            if (condElement.isJsonArray()) {
                JsonArray conditionsArray = condElement.getAsJsonArray();
                for (JsonElement element : conditionsArray) {
                    if (element.isJsonObject()) {
                        if (!checkSingleCondition(element.getAsJsonObject())) {
                            OKCore.okLog(Level.INFO, "Skipping data file [{}] due to unfulfilled load conditions.", id);
                            return false;
                        }
                    }
                }
                return true;
            }
        }

        if (!checkSingleCondition(conditionObj)) {
            OKCore.okLog(Level.INFO, "Skipping data file [{}] due to unfulfilled load conditions.", id);
            return false;
        }

        return true;
    }
}
