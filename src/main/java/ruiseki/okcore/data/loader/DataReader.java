package ruiseki.okcore.data.loader;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Level;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.data.loader.conditional.LoadConditionHandler;
import ruiseki.okcore.json.AbstractJsonReader;

public abstract class DataReader<T> extends AbstractJsonReader<T> {

    public DataReader(File path) {
        super(path);
    }

    @Override
    public T read() throws IOException {
        return readFile(path);
    }

    @Override
    protected T readFile(JsonElement root, File file) {
        if (root.isJsonObject()) {
            JsonObject rootObj = root.getAsJsonObject();

            String conditionKey = "okcore:load_conditions";
            if (rootObj.has(conditionKey)) {
                JsonElement conditionsElement = rootObj.get(conditionKey);

                if (conditionsElement.isJsonArray()) {
                    JsonArray conditionsArray = conditionsElement.getAsJsonArray();

                    for (JsonElement condElement : conditionsArray) {
                        if (condElement.isJsonObject()) {
                            if (!LoadConditionHandler.checkSingleCondition(condElement.getAsJsonObject())) {
                                OKCore.okLog(
                                    Level.INFO,
                                    "Skipping data file [{}] due to unfulfilled load conditions.",
                                    file.getName());
                                return null;
                            }
                        }
                    }
                }
            }
        }

        return readData(root, file);
    }

    protected abstract T readData(JsonElement root, File file);
}
