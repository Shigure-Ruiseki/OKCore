package ruiseki.okcore.data.loader.condition;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@LoadCondition
public class GTNHConfigCondition implements ILoadCondition {

    private static final Map<Class<?>, FieldValueMatcher> MATCHERS = new HashMap<>();

    static {
        FieldValueMatcher booleanMatcher = new BooleanMatcher();
        MATCHERS.put(boolean.class, booleanMatcher);
        MATCHERS.put(Boolean.class, booleanMatcher);

        FieldValueMatcher intMatcher = new IntMatcher();
        MATCHERS.put(int.class, intMatcher);
        MATCHERS.put(Integer.class, intMatcher);

        FieldValueMatcher floatMatcher = new FloatMatcher();
        MATCHERS.put(float.class, floatMatcher);
        MATCHERS.put(Float.class, floatMatcher);

        FieldValueMatcher doubleMatcher = new DoubleMatcher();
        MATCHERS.put(double.class, doubleMatcher);
        MATCHERS.put(Double.class, doubleMatcher);

        MATCHERS.put(String.class, new StringMatcher());
        MATCHERS.put(String[].class, new StringArrayMatcher());
        MATCHERS.put(double[].class, new DoubleArrayMatcher());
        MATCHERS.put(int[].class, new IntArrayMatcher());
        MATCHERS.put(Enum.class, new EnumMatcher());
    }

    @Override
    public String getID() {
        return "okcore:gtnh_config";
    }

    @Override
    public boolean test(JsonObject json) {
        if (json == null || !json.has("class_path") || !json.has("config_name") || !json.has("value")) {
            return false;
        }

        String classPath = json.get("class_path")
            .getAsString();
        String targetConfigName = json.get("config_name")
            .getAsString();
        JsonElement expectedValue = json.get("value");

        try {
            Class<?> configClass = Class.forName(classPath);

            String[] configPathNodes = targetConfigName.split("\\.");

            return searchAndMatch(configClass, null, configPathNodes, 0, expectedValue);

        } catch (Exception e) {
            return false;
        }
    }

    private boolean searchAndMatch(Class<?> currentClass, Object currentInstance, String[] pathNodes, int depth,
        JsonElement expectedValue) throws Exception {
        String currentTargetNode = pathNodes[depth];
        boolean isLastNode = (depth == pathNodes.length - 1);

        for (Field field : currentClass.getDeclaredFields()) {
            field.setAccessible(true);

            String actualFieldName = field.getName();

            if (actualFieldName.equals(currentTargetNode)) {

                if (isLastNode) {
                    Class<?> fieldType = field.getType();
                    FieldValueMatcher matcher = MATCHERS.get(fieldType);
                    if (matcher == null && Enum.class.isAssignableFrom(fieldType)) {
                        matcher = MATCHERS.get(Enum.class);
                    }

                    if (matcher != null) {
                        return matcher.matches(field, currentInstance, expectedValue);
                    }
                    return false;
                }

                else {
                    Object nextInstance = field.get(currentInstance);
                    if (nextInstance == null) return false;
                    return searchAndMatch(nextInstance.getClass(), nextInstance, pathNodes, depth + 1, expectedValue);
                }
            }
        }
        return false;
    }

    private interface FieldValueMatcher {

        boolean matches(Field field, Object instance, JsonElement jsonElement) throws Exception;
    }

    private static class BooleanMatcher implements FieldValueMatcher {

        @Override
        public boolean matches(Field field, Object instance, JsonElement jsonElement) throws Exception {
            return field.getBoolean(instance) == jsonElement.getAsBoolean();
        }
    }

    private static class IntMatcher implements FieldValueMatcher {

        @Override
        public boolean matches(Field field, Object instance, JsonElement jsonElement) throws Exception {
            return field.getInt(instance) == jsonElement.getAsInt();
        }
    }

    private static class FloatMatcher implements FieldValueMatcher {

        @Override
        public boolean matches(Field field, Object instance, JsonElement jsonElement) throws Exception {
            return Float.compare(field.getFloat(instance), jsonElement.getAsFloat()) == 0;
        }
    }

    private static class DoubleMatcher implements FieldValueMatcher {

        @Override
        public boolean matches(Field field, Object instance, JsonElement jsonElement) throws Exception {
            return Double.compare(field.getDouble(instance), jsonElement.getAsDouble()) == 0;
        }
    }

    private static class StringMatcher implements FieldValueMatcher {

        @Override
        public boolean matches(Field field, Object instance, JsonElement jsonElement) throws Exception {
            String current = (String) field.get(instance);
            return current != null && current.equals(jsonElement.getAsString());
        }
    }

    private static class StringArrayMatcher implements FieldValueMatcher {

        @Override
        public boolean matches(Field field, Object instance, JsonElement jsonElement) throws Exception {
            String[] currentArray = (String[]) field.get(instance);
            if (currentArray == null) return false;

            if (jsonElement.isJsonArray()) {
                com.google.gson.JsonArray jsonArray = jsonElement.getAsJsonArray();
                String[] expectedArray = new String[jsonArray.size()];
                for (int i = 0; i < jsonArray.size(); i++) {
                    expectedArray[i] = jsonArray.get(i)
                        .getAsString();
                }
                return Arrays.equals(currentArray, expectedArray);
            }

            String expectedString = jsonElement.getAsString();
            return Arrays.asList(currentArray)
                .contains(expectedString);
        }
    }

    private static class DoubleArrayMatcher implements FieldValueMatcher {

        @Override
        public boolean matches(Field field, Object instance, JsonElement jsonElement) throws Exception {
            double[] currentArray = (double[]) field.get(instance);
            if (currentArray == null || !jsonElement.isJsonArray()) return false;

            com.google.gson.JsonArray jsonArray = jsonElement.getAsJsonArray();
            double[] expectedArray = new double[jsonArray.size()];
            for (int i = 0; i < jsonArray.size(); i++) {
                expectedArray[i] = jsonArray.get(i)
                    .getAsDouble();
            }
            return Arrays.equals(currentArray, expectedArray);
        }
    }

    private static class IntArrayMatcher implements FieldValueMatcher {

        @Override
        public boolean matches(Field field, Object instance, JsonElement jsonElement) throws Exception {
            int[] currentArray = (int[]) field.get(instance);
            if (currentArray == null || !jsonElement.isJsonArray()) return false;

            com.google.gson.JsonArray jsonArray = jsonElement.getAsJsonArray();
            int[] expectedArray = new int[jsonArray.size()];
            for (int i = 0; i < jsonArray.size(); i++) {
                expectedArray[i] = jsonArray.get(i)
                    .getAsInt();
            }
            return Arrays.equals(currentArray, expectedArray);
        }
    }

    private static class EnumMatcher implements FieldValueMatcher {

        @Override
        public boolean matches(Field field, Object instance, JsonElement jsonElement) throws Exception {
            Enum<?> currentEnum = (Enum<?>) field.get(instance);
            if (currentEnum == null) return false;
            return currentEnum.name()
                .equalsIgnoreCase(jsonElement.getAsString());
        }
    }
}
