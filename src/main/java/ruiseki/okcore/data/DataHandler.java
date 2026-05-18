package ruiseki.okcore.data;

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

    public static void handle(ResourceLocation id, String namespace, String folder, String[] subPaths, String fileName,
        InputStream inputStream) {
        List<IDataLoader> componentList = COMPONENTS.get(folder);

        if (componentList != null) {
            for (IDataLoader component : componentList) {
                try {
                    component.process(id, namespace, folder, subPaths, fileName, inputStream);
                } catch (Exception e) {
                    OKCore.okLog(
                        Level.ERROR,
                        "Loader {} failed to process file {}: {}",
                        component.getClass()
                            .getSimpleName(),
                        fileName,
                        e.toString());
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
     * Evaluates an array of custom load conditions defined within a data file's JSON structure.
     * <p>
     * <strong>CRITICAL LIFECYCLE NOTE:</strong> This method must <strong>ONLY</strong> be invoked
     * during or after the {@code POSTINIT} stage. Checking conditions (such as mod presence, item
     * existence, or fluid registration) before all registries are fully frozen and populated will
     * result in false negatives, improperly skipping valid data files.
     * </p>
     * <p>
     * The method iterates directly through the provided array of conditions. If any single condition
     * fails the evaluation performed by {@link LoadConditionHandler#checkSingleCondition(JsonObject)},
     * the entire file is rejected immediately.
     * </p>
     *
     * @param id              The unique identifier or filename of the resource being evaluated (used for logging
     *                        purposes).
     * @param conditionsArray The {@link JsonArray} containing the condition configurations extracted from the file.
     * @return {@code true} if all conditions within the array are successfully met, or if the array is empty;
     *         {@code false} if at least one condition fails, indicating the file should be skipped.
     */
    public static boolean checkConditional(String id, JsonArray conditionsArray) {
        for (JsonElement condElement : conditionsArray) {
            if (condElement.isJsonObject()) {
                if (!LoadConditionHandler.checkSingleCondition(condElement.getAsJsonObject())) {
                    OKCore.okLog(Level.INFO, "Skipping data file [{}] due to unfulfilled load conditions.", id);
                    return false;
                }
            }
        }
        return true;
    }
}
