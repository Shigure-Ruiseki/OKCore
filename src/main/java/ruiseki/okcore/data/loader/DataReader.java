package ruiseki.okcore.data.loader;

import java.io.IOException;
import java.io.InputStream;

import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonElement;

import ruiseki.okcore.json.AbstractJsonStreamReader;

public abstract class DataReader<T> extends AbstractJsonStreamReader<T> {

    private final String fileName;
    private final ResourceLocation id;

    public DataReader(ResourceLocation id, String fileName) {
        this.id = id;
        this.fileName = fileName;
    }

    @Override
    public T read(InputStream stream) throws IOException {
        return readStream(stream);
    }

    @Override
    protected T readStream(JsonElement root) {
        return readData(this.id, root, fileName);
    }

    protected abstract T readData(ResourceLocation id, JsonElement root, String resourceName);

    public ResourceLocation getId() {
        return this.id;
    }
}
