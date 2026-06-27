package ruiseki.okcore.data.loader.tags;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.data.loader.DataReader;
import ruiseki.okcore.tag.entry.TagEntry;
import ruiseki.okcore.tag.entry.TagEntryRegistry;

public class TagReader<T> extends DataReader<TagHolder<T>> {

    private final String subfolder;

    public TagReader(ResourceLocation id, String fileName, String subfolder) {
        super(id, fileName);
        this.subfolder = subfolder;
    }

    @Override
    protected TagHolder<T> readData(ResourceLocation id, JsonElement root, String resourceName) {
        if (!root.isJsonObject()) return null;
        JsonObject jsonObject = root.getAsJsonObject();

        boolean replace = jsonObject.has("replace") && jsonObject.get("replace")
            .getAsBoolean();

        TagEntry<T> factory = TagEntryRegistry.getFactory(this.subfolder);
        if (factory == null) return null;

        List<TagEntry<T>> entries = new ArrayList<>();

        if (jsonObject.has("values") && jsonObject.get("values")
            .isJsonArray()) {
            JsonArray valuesArray = jsonObject.getAsJsonArray("values");

            for (JsonElement element : valuesArray) {
                if (!element.isJsonPrimitive()) continue;
                String rawValue = element.getAsString();

                String[] parts = rawValue.split(":");
                if (parts.length < 2) continue;

                String namespace = parts[0];
                String path = parts[1];
                int meta = 0;

                if (parts.length >= 3) {
                    String rawMeta = parts[2];
                    if (!rawMeta.equalsIgnoreCase("#wildcard")) {
                        try {
                            meta = Integer.parseInt(rawMeta);
                        } catch (NumberFormatException ignored) {}
                    }
                }

                ResourceLocation entryId = new ResourceLocation(namespace, path);

                TagEntry<T> newEntry = factory.create(entryId, meta);
                if (newEntry != null) {
                    entries.add(newEntry);
                }
            }
        }

        return new TagHolder<>(replace, entries);
    }
}
