package ruiseki.okcore.helper;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;

public class GsonHelpers {

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
}
