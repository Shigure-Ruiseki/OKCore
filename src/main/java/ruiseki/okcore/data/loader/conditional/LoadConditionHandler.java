package ruiseki.okcore.data.loader.conditional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Level;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.discovery.ASMDataTable;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.json.AbstractJsonMaterial;

public class LoadConditionHandler {

    public static final String CONDITION_KEY = "okcore:load_conditions";
    private static final Map<String, Class<? extends AbstractJsonMaterial>> CONDITION_MAPPING = new HashMap<>();

    public static void loadFromASM(ASMDataTable asmDataTable) {
        Set<ASMDataTable.ASMData> asmDataSet = asmDataTable.getAll(LoadCondition.class.getName());

        for (ASMDataTable.ASMData asmData : asmDataSet) {
            try {
                Class<?> clazz = Class.forName(asmData.getClassName());

                if (AbstractJsonMaterial.class.isAssignableFrom(clazz)) {
                    LoadCondition annotation = clazz.getAnnotation(LoadCondition.class);
                    if (annotation != null) {
                        @SuppressWarnings("unchecked")
                        Class<? extends AbstractJsonMaterial> condition = (Class<? extends AbstractJsonMaterial>) clazz;
                        CONDITION_MAPPING.put(annotation.value(), condition);
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

    public static AbstractJsonMaterial createConditionInstance(JsonObject condElement) {
        if (!condElement.has("type")) {
            OKCore.okLog(Level.WARN, "Missing 'type' field in load condition JSON.");
            return null;
        }

        String type = condElement.get("type")
            .getAsString();
        Class<? extends AbstractJsonMaterial> conditionClass = CONDITION_MAPPING.get(type);
        if (conditionClass == null) {
            OKCore.okLog(Level.WARN, "Unknown load condition type: {}", type);
            return null;
        }

        try {
            return conditionClass.getDeclaredConstructor()
                .newInstance();
        } catch (Exception e) {
            OKCore.okLog(Level.ERROR, "Failed to instantiate condition [{}]: {}", type, e.toString());
            return null;
        }
    }

    public static boolean checkSingleCondition(JsonObject condElement) {
        AbstractJsonMaterial conditionInstance = createConditionInstance(condElement);
        if (conditionInstance == null) {
            return false;
        }

        try {
            conditionInstance.read(condElement);
            return conditionInstance.validate();
        } catch (Exception e) {
            OKCore.okLog(Level.ERROR, "Failed to evaluate condition: {}", e.toString());
            return false;
        }
    }
}
