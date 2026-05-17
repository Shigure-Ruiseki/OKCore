package ruiseki.okcore.data.loader;

import static ruiseki.okcore.data.loader.conditional.LoadConditionHandler.CONDITION_KEY;

import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.Level;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.data.loader.conditional.LoadConditionHandler;
import ruiseki.okcore.json.AbstractJsonStreamReader;

public abstract class DataReader<T> extends AbstractJsonStreamReader<T> {

    private final String fileName;

    public DataReader(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public T read(InputStream stream) throws IOException {
        return readStream(stream);
    }

    @Override
    protected T readStream(JsonElement root) {
        if (root.isJsonObject()) {
            JsonObject rootObj = root.getAsJsonObject();

            if (rootObj.has(CONDITION_KEY)) {
                JsonElement conditionsElement = rootObj.get(CONDITION_KEY);

                if (conditionsElement.isJsonArray()) {
                    JsonArray conditionsArray = conditionsElement.getAsJsonArray();

                    for (JsonElement condElement : conditionsArray) {
                        if (condElement.isJsonObject()) {
                            if (!LoadConditionHandler.checkSingleCondition(condElement.getAsJsonObject())) {
                                OKCore.okLog(
                                    Level.INFO,
                                    "Skipping data file [{}] due to unfulfilled load conditions.",
                                    fileName);
                                return null;
                            }
                        }
                    }
                }
            }
        }

        return readData(root, fileName);
    }

    protected abstract T readData(JsonElement root, String resourceName);
}
