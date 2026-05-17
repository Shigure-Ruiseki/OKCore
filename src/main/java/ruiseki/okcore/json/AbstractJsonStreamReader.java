package ruiseki.okcore.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import ruiseki.okcore.OKCore;

/**
 * Base class for JSON stream readers.
 * Supports reading JSON data directly from an {@link InputStream}.
 * Useful for processing data packed inside JARs, assets, or network streams.
 *
 * @param <T> The type of object produced by this reader.
 */
public abstract class AbstractJsonStreamReader<T> {

    protected final Gson gson;
    protected T cache;
    protected final Map<String, Object> index = new HashMap<>();

    public AbstractJsonStreamReader() {
        this.gson = createGson();
    }

    protected Gson createGson() {
        return new GsonBuilder().setPrettyPrinting()
            .create();
    }

    /**
     * Reads the JSON data from the provided input stream and converts it to the target type.
     * Subclasses should use this to fill their cache.
     *
     * @param stream The input stream containing JSON data.
     * @return The parsed object of type T.
     * @throws IOException If an I/O error occurs during reading.
     */
    public abstract T read(InputStream stream) throws IOException;

    /**
     * Rebuilds the search index. Subclasses should override this.
     */
    protected void rebuildIndex() {
        index.clear();
    }

    /**
     * Gets an element by its identifier (name or ID) from the index.
     */
    @SuppressWarnings("unchecked")
    public <E> E get(String identifier) {
        return (E) index.get(identifier);
    }

    /**
     * Gets the cached data.
     */
    public T getData() {
        return cache;
    }

    /**
     * Internal helper to read a JSON element from an input stream with error handling.
     * Automatically handles UTF-8 encoding.
     */
    protected JsonElement readJsonElement(InputStream stream) throws IOException {
        if (stream == null) {
            OKCore.okLog(Level.ERROR, "Input stream is null!");
            return null;
        }
        try (InputStreamReader streamReader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            JsonReader reader = new JsonReader(streamReader);
            reader.setLenient(true);
            return new JsonParser().parse(reader);
        } catch (JsonSyntaxException e) {
            OKCore.okLog(Level.ERROR, "Malformed JSON stream (perhaps empty or corrupted): {}", e.getMessage());
            return null;
        }
    }

    /**
     * Reads a single JSON stream and converts it to the target type.
     * Tracks the execution context using the provided resource identifier.
     *
     * @param stream The input stream containing JSON data.
     * @return The parsed object of type T, or null if an error occurs.
     */
    public T readStream(InputStream stream) {
        try {
            JsonElement root = readJsonElement(stream);
            if (root == null) return null;
            return readStream(root);
        } catch (IOException e) {
            OKCore.okLog(Level.ERROR, "Failed to read JSON stream: {}", e.getMessage());
            return null;
        } finally {
            ParsingContext.clear();
        }
    }

    /**
     * Implementation-specific logic to parse a JSON element into the target type.
     */
    protected abstract T readStream(JsonElement root);
}
