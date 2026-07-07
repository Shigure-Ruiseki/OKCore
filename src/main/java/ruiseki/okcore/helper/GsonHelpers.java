package ruiseki.okcore.helper;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

public class GsonHelpers {

    public static String getAsString(JsonObject object, String key) {
        if (object.has(key)) {
            return convertToString(object.get(key), key);
        } else {
            throw new JsonSyntaxException("Missing " + key + ", expected to find a string");
        }
    }

    public static String convertToString(JsonElement element, String key) {
        if (element.isJsonPrimitive()) {
            return element.getAsString();
        } else {
            throw new JsonSyntaxException("Expected " + key + " to be a string, was " + getType(element));
        }
    }

    public static boolean getAsBoolean(JsonObject object, String key) {
        if (object.has(key)) {
            return convertToBoolean(object.get(key), key);
        } else {
            throw new JsonSyntaxException("Missing " + key + ", expected to find a Boolean");
        }
    }

    public static boolean getAsBoolean(JsonObject object, String key, boolean defaultValue) {
        return object.has(key) ? convertToBoolean(object.get(key), key) : defaultValue;
    }

    public static boolean convertToBoolean(JsonElement element, String key) {
        if (element.isJsonPrimitive()) {
            return element.getAsBoolean();
        } else {
            throw new JsonSyntaxException("Expected " + key + " to be a Boolean, was " + getType(element));
        }
    }

    public static double getAsDouble(JsonObject object, String key) {
        if (object.has(key)) {
            return convertToDouble(object.get(key), key);
        } else {
            throw new JsonSyntaxException("Missing " + key + ", expected to find a Double");
        }
    }

    public static double getAsDouble(JsonObject object, String key, double defaultValue) {
        return object.has(key) ? convertToDouble(object.get(key), key) : defaultValue;
    }

    public static double convertToDouble(JsonElement p_144770_, String p_144771_) {
        if (p_144770_.isJsonPrimitive() && p_144770_.getAsJsonPrimitive()
            .isNumber()) {
            return p_144770_.getAsDouble();
        } else {
            throw new JsonSyntaxException("Expected " + p_144771_ + " to be a Double, was " + getType(p_144770_));
        }
    }

    public static long getAsLong(JsonObject object, String key) {
        if (object.has(key)) {
            return convertToLong(object.get(key), key);
        } else {
            throw new JsonSyntaxException("Missing " + key + ", expected to find a Long");
        }
    }

    public static long getAsLong(JsonObject object, String key, long defaultValue) {
        return object.has(key) ? convertToLong(object.get(key), key) : defaultValue;
    }

    public static long convertToLong(JsonElement element, String key) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive()
            .isNumber()) {
            return element.getAsLong();
        } else {
            throw new JsonSyntaxException("Expected " + key + " to be a Long, was " + getType(element));
        }
    }

    public static int getAsInt(JsonObject object, String key) {
        if (object.has(key)) {
            return convertToInt(object.get(key), key);
        } else {
            throw new JsonSyntaxException("Missing " + key + ", expected to find a Int");
        }
    }

    public static int getAsInt(JsonObject object, String key, int defaultValue) {
        return object.has(key) ? convertToInt(object.get(key), key) : defaultValue;
    }

    public static int convertToInt(JsonElement element, String key) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive()
            .isNumber()) {
            return element.getAsInt();
        } else {
            throw new JsonSyntaxException("Expected " + key + " to be a Int, was " + getType(element));
        }
    }

    public static byte convertToByte(JsonElement element, String key) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive()
            .isNumber()) {
            return element.getAsByte();
        } else {
            throw new JsonSyntaxException("Expected " + key + " to be a Byte, was " + getType(element));
        }
    }

    public static byte getAsByte(JsonObject object, String key) {
        if (object.has(key)) {
            return convertToByte(object.get(key), key);
        } else {
            throw new JsonSyntaxException("Missing " + key + ", expected to find a Byte");
        }
    }

    public static byte getAsByte(JsonObject object, String key, byte defaultValute) {
        return object.has(key) ? convertToByte(object.get(key), key) : defaultValute;
    }

    public static char convertToCharacter(JsonElement element, String key) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive()
            .isNumber()) {
            return element.getAsCharacter();
        } else {
            throw new JsonSyntaxException("Expected " + key + " to be a Character, was " + getType(element));
        }
    }

    public static char getAsCharacter(JsonObject object, String key) {
        if (object.has(key)) {
            return convertToCharacter(object.get(key), key);
        } else {
            throw new JsonSyntaxException("Missing " + key + ", expected to find a Character");
        }
    }

    public static char getAsCharacter(JsonObject object, String key, char defaultValute) {
        return object.has(key) ? convertToCharacter(object.get(key), key) : defaultValute;
    }

    public static BigDecimal convertToBigDecimal(JsonElement element, String key) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive()
            .isNumber()) {
            return element.getAsBigDecimal();
        } else {
            throw new JsonSyntaxException("Expected " + key + " to be a BigDecimal, was " + getType(element));
        }
    }

    public static BigDecimal getAsBigDecimal(JsonObject object, String key) {
        if (object.has(key)) {
            return convertToBigDecimal(object.get(key), key);
        } else {
            throw new JsonSyntaxException("Missing " + key + ", expected to find a BigDecimal");
        }
    }

    public static BigDecimal getAsBigDecimal(JsonObject object, String key, BigDecimal defaultValute) {
        return object.has(key) ? convertToBigDecimal(object.get(key), key) : defaultValute;
    }

    public static BigInteger convertToBigInteger(JsonElement element, String key) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive()
            .isNumber()) {
            return element.getAsBigInteger();
        } else {
            throw new JsonSyntaxException("Expected " + key + " to be a BigInteger, was " + getType(element));
        }
    }

    public static BigInteger getAsBigInteger(JsonObject object, String key) {
        if (object.has(key)) {
            return convertToBigInteger(object.get(key), key);
        } else {
            throw new JsonSyntaxException("Missing " + key + ", expected to find a BigInteger");
        }
    }

    public static BigInteger getAsBigInteger(JsonObject object, String key, BigInteger defaultValute) {
        return object.has(key) ? convertToBigInteger(object.get(key), key) : defaultValute;
    }

    public static short convertToShort(JsonElement element, String key) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive()
            .isNumber()) {
            return element.getAsShort();
        } else {
            throw new JsonSyntaxException("Expected " + key + " to be a Short, was " + getType(element));
        }
    }

    public static short getAsShort(JsonObject object, String key) {
        if (object.has(key)) {
            return convertToShort(object.get(key), key);
        } else {
            throw new JsonSyntaxException("Missing " + key + ", expected to find a Short");
        }
    }

    public static short getAsShort(JsonObject object, String key, short defaultValute) {
        return object.has(key) ? convertToShort(object.get(key), key) : defaultValute;
    }

    public static JsonObject getAsJsonObject(JsonObject object, String key) {
        if (object.has(key)) {
            return convertToJsonObject(object.get(key), key);
        } else {
            throw new JsonSyntaxException("Missing " + key + ", expected to find a JsonObject");
        }
    }

    public static JsonObject convertToJsonObject(JsonElement element, String key) {
        if (element.isJsonObject()) {
            return element.getAsJsonObject();
        } else {
            throw new JsonSyntaxException("Expected " + key + " to be a JsonObject, was " + getType(element));
        }
    }

    public static JsonArray getAsJsonArray(JsonObject object, String key) {
        if (object.has(key)) {
            return convertToJsonArray(object.get(key), key);
        } else {
            throw new JsonSyntaxException("Missing " + key + ", expected to find a JsonArray");
        }
    }

    public static JsonArray convertToJsonArray(JsonElement element, String key) {
        if (element.isJsonArray()) {
            return element.getAsJsonArray();
        } else {
            throw new JsonSyntaxException("Expected " + key + " to be a JsonArray, was " + getType(element));
        }
    }

    public static String getType(@Nullable JsonElement element) {
        String s = StringUtils.abbreviateMiddle(String.valueOf((Object) element), "...", 10);
        if (element == null) {
            return "null (missing)";
        } else if (element.isJsonNull()) {
            return "null (json)";
        } else if (element.isJsonArray()) {
            return "an array (" + s + ")";
        } else if (element.isJsonObject()) {
            return "an object (" + s + ")";
        } else {
            if (element.isJsonPrimitive()) {
                JsonPrimitive jsonprimitive = element.getAsJsonPrimitive();
                if (jsonprimitive.isNumber()) {
                    return "a number (" + s + ")";
                }

                if (jsonprimitive.isBoolean()) {
                    return "a boolean (" + s + ")";
                }
            }

            return s;
        }
    }

    @Nullable
    public static <T> T fromNullableJson(Gson gson, Reader reader, Class<T> type, boolean lenient) {
        try {
            JsonReader jsonreader = new JsonReader(reader);
            jsonreader.setLenient(lenient);
            return gson.getAdapter(type)
                .read(jsonreader);
        } catch (IOException ioexception) {
            throw new JsonParseException(ioexception);
        }
    }

    public static <T> T fromJson(Gson gson, Reader reader, Class<T> type, boolean lenient) {
        T t = fromNullableJson(gson, reader, type, lenient);
        if (t == null) {
            throw new JsonParseException("JSON data was null or empty");
        } else {
            return t;
        }
    }

    public static <T> T fromJson(Gson gson, String reader, Class<T> type, boolean lenient) {
        return fromJson(gson, new StringReader(reader), type, lenient);
    }

    public static <T> T fromJson(Gson gson, Reader reader, Class<T> type) {
        return fromJson(gson, reader, type, false);
    }

    public static <T> T fromJson(Gson gson, String reader, Class<T> type) {
        return fromJson(gson, reader, type, false);
    }

    /**
     * Converts a JsonObject to an NBTTagCompound.
     */
    public static NBTTagCompound jsonToNBT(JsonObject json) {
        NBTTagCompound nbt = new NBTTagCompound();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            nbt.setTag(entry.getKey(), elementToNBT(entry.getValue()));
        }
        return nbt;
    }

    private static NBTBase elementToNBT(JsonElement element) {
        if (element.isJsonObject()) {
            return jsonToNBT(element.getAsJsonObject());
        } else if (element.isJsonArray()) {
            JsonArray arr = element.getAsJsonArray();
            NBTTagList list = new NBTTagList();
            for (JsonElement e : arr) {
                list.appendTag(elementToNBT(e));
            }
            return list;
        } else if (element.isJsonPrimitive()) {
            JsonPrimitive p = element.getAsJsonPrimitive();
            if (p.isBoolean()) {
                return new NBTTagByte((byte) (p.getAsBoolean() ? 1 : 0));
            } else if (p.isString()) {
                return new NBTTagString(p.getAsString());
            } else if (p.isNumber()) {
                Number n = p.getAsNumber();
                if (n instanceof Integer) return new NBTTagInt(n.intValue());
                if (n instanceof Long) return new NBTTagLong(n.longValue());
                if (n instanceof Double) return new NBTTagDouble(n.doubleValue());
                if (n instanceof Float) return new NBTTagFloat(n.floatValue());
                if (n instanceof Short) return new NBTTagShort(n.shortValue());
                if (n instanceof Byte) return new NBTTagByte(n.byteValue());

                // Fallback for GSON's LazilyParsedNumber
                String s = n.toString();
                try {
                    if (s.contains(".")) return new NBTTagDouble(n.doubleValue());
                    long l = n.longValue();
                    if (l <= Integer.MAX_VALUE && l >= Integer.MIN_VALUE) return new NBTTagInt((int) l);
                    return new NBTTagLong(l);
                } catch (NumberFormatException e) {
                    return new NBTTagDouble(n.doubleValue());
                }
            }
        }
        return new NBTTagString("");
    }

    /**
     * Converts an NBTTagCompound to a JsonObject.
     */
    public static JsonObject nbtToJSON(NBTTagCompound nbt) {
        JsonObject json = new JsonObject();
        for (Object keyObj : nbt.func_150296_c()) {
            String key = (String) keyObj;
            json.add(key, nbtToElement(nbt.getTag(key)));
        }
        return json;
    }

    private static JsonElement nbtToElement(NBTBase nbt) {
        if (nbt instanceof NBTTagCompound compound) {
            return nbtToJSON(compound);
        } else if (nbt instanceof NBTTagList list) {
            JsonArray arr = new JsonArray();
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound cmp = list.getCompoundTagAt(i);
                if (cmp.hasNoTags() && list.func_150303_d() != 10) {
                    if (list.func_150303_d() == 8) {
                        arr.add(new JsonPrimitive(list.getStringTagAt(i)));
                    } else {
                        arr.add(
                            new JsonPrimitive(
                                list.getCompoundTagAt(i)
                                    .toString()));
                    }
                } else {
                    arr.add(nbtToJSON(cmp));
                }
            }
            return arr;
        } else if (nbt instanceof NBTTagString s) {
            return new JsonPrimitive(s.func_150285_a_());
        } else if (nbt instanceof NBTTagInt i) {
            return new JsonPrimitive(i.func_150287_d());
        } else if (nbt instanceof NBTTagDouble d) {
            return new JsonPrimitive(d.func_150286_g());
        } else if (nbt instanceof NBTTagFloat f) {
            return new JsonPrimitive(f.func_150288_h());
        } else if (nbt instanceof NBTTagLong l) {
            return new JsonPrimitive(l.func_150291_c());
        } else if (nbt instanceof NBTTagShort s) {
            return new JsonPrimitive(s.func_150289_e());
        } else if (nbt instanceof NBTTagByte b) {
            return new JsonPrimitive(b.func_150290_f());
        } else if (nbt instanceof NBTTagIntArray ia) {
            JsonArray arr = new JsonArray();
            for (int val : ia.func_150302_c()) {
                arr.add(new JsonPrimitive(val));
            }
            return arr;
        }
        return new JsonPrimitive(nbt.toString());
    }
}
