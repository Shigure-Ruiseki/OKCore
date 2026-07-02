package ruiseki.okcore.data;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.datastructure.Resource;
import ruiseki.okcore.helper.GsonHelpers;

public abstract class SimpleJsonResourceReloadListener
    extends SimplePreparableReloadListener<Map<ResourceLocation, JsonElement>> {

    private final Gson gson;
    private final String directory;

    public SimpleJsonResourceReloadListener(Gson gson, String directory) {
        this.gson = gson;
        this.directory = directory;
    }

    @Override
    protected Map<ResourceLocation, JsonElement> prepare(DataManager manager) {
        Map<ResourceLocation, JsonElement> map = new HashMap<>();
        scanDirectory(manager, this.directory, this.gson, map);
        return map;
    }

    private void scanDirectory(DataManager manager, String directory, Gson gson,
        Map<ResourceLocation, JsonElement> map) {
        FileToIdConverter filetoidconverter = FileToIdConverter.json(directory);

        for (Map.Entry<ResourceLocation, Resource> entry : filetoidconverter.listMatchingResources(manager)
            .entrySet()) {
            ResourceLocation resourcelocation = entry.getKey();
            ResourceLocation resourcelocation1 = filetoidconverter.fileToId(resourcelocation);

            try (Reader reader = entry.getValue()
                .openAsReader()) {
                JsonElement jsonelement = GsonHelpers.fromJson(gson, reader, JsonElement.class);
                JsonElement jsonelement1 = map.put(resourcelocation1, jsonelement);
                if (jsonelement1 != null) {
                    throw new IllegalStateException("Duplicate data file ignored with ID " + resourcelocation1);
                }
            } catch (IllegalArgumentException | IOException | JsonParseException jsonparseexception) {
                OKCore.okLog(
                    Level.ERROR,
                    "Couldn't parse data file {} from {}",
                    resourcelocation1,
                    resourcelocation,
                    jsonparseexception);
            }
        }
    }
}
