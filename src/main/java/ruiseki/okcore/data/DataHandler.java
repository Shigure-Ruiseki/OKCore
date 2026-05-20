package ruiseki.okcore.data;

import static ruiseki.okcore.data.loader.conditional.LoadConditionHandler.CONDITION_KEY;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cpw.mods.fml.common.discovery.ASMDataTable;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.data.loader.DataLoader;
import ruiseki.okcore.data.loader.IDataLoader;
import ruiseki.okcore.data.loader.conditional.LoadConditionHandler;

public class DataHandler {

    private static final Map<String, List<IDataLoader>> COMPONENTS = new HashMap<>();

    public static void register(IDataLoader component) {
        COMPONENTS.computeIfAbsent(component.getTargetFolder(), k -> new ArrayList<>())
            .add(component);
    }

    public static void handleMod(ResourceLocation id, String namespace, String folder, String[] subPaths,
        String fileName, InputStream inputStream) {
        List<IDataLoader> componentList = COMPONENTS.get(folder);

        if (componentList != null) {
            for (IDataLoader component : componentList) {
                if (component.isModLoader()) {
                    try {
                        component.process(id, namespace, folder, subPaths, fileName, inputStream);
                    } catch (Exception e) {
                        OKCore.okLog(
                            Level.ERROR,
                            "Mod Loader {} failed: {}",
                            component.getClass()
                                .getSimpleName(),
                            e.toString());
                    }
                }
            }
        }
    }

    public static void handleWorld(ResourceLocation id, String namespace, String folder, String[] subPaths,
        String fileName, InputStream inputStream) {
        List<IDataLoader> componentList = COMPONENTS.get(folder);
        if (componentList != null) {
            for (IDataLoader component : componentList) {
                if (component.isWorldLoader()) {
                    try {
                        component.process(id, namespace, folder, subPaths, fileName, inputStream);
                    } catch (Exception e) {
                        OKCore.okLog(
                            Level.ERROR,
                            "World Loader {} failed: {}",
                            component.getClass()
                                .getSimpleName(),
                            e.toString());
                    }
                }
            }
        }
    }

    public static void loadFromASM(ASMDataTable asmDataTable) {
        Set<ASMDataTable.ASMData> asmDataSet = asmDataTable.getAll(DataLoader.class.getName());

        for (ASMDataTable.ASMData asmData : asmDataSet) {
            try {
                Class<?> clazz = Class.forName(asmData.getClassName());

                if (IDataLoader.class.isAssignableFrom(clazz)) {
                    IDataLoader loader = (IDataLoader) clazz.getDeclaredConstructor()
                        .newInstance();
                    if (loader.shouldRegister()) {
                        register(loader);
                    } else {
                        OKCore.okLog(
                            Level.INFO,
                            "Skipping Data Loader [{}] because its required mod/condition is not met.",
                            loader.getClass()
                                .getSimpleName());
                    }
                }
            } catch (Exception e) {
                OKCore.okLog(
                    Level.ERROR,
                    "Failed to initialize ASM DataLoader [{}]: {}",
                    asmData.getClassName(),
                    e.toString());
            }
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
     * {@link LoadConditionHandler#checkSingleCondition(JsonObject)}.</li>
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
                        if (!LoadConditionHandler.checkSingleCondition(element.getAsJsonObject())) {
                            OKCore.okLog(Level.INFO, "Skipping data file [{}] due to unfulfilled load conditions.", id);
                            return false;
                        }
                    }
                }
                return true;
            }
        }

        if (!LoadConditionHandler.checkSingleCondition(conditionObj)) {
            OKCore.okLog(Level.INFO, "Skipping data file [{}] due to unfulfilled load conditions.", id);
            return false;
        }

        return true;
    }
}
