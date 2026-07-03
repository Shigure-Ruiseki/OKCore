package ruiseki.okcore.data;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.datastructure.Resource;
import ruiseki.okcore.helper.GsonHelpers;

public abstract class MultiJsonResourceReloadListener
    extends SimplePreparableReloadListener<Map<ResourceLocation, List<JsonElement>>> {

    private final Gson gson;
    private final String directory;

    public MultiJsonResourceReloadListener(Gson gson, String directory) {
        this.gson = gson;
        this.directory = directory;
    }

    @Override
    protected Map<ResourceLocation, List<JsonElement>> prepare(DataManager manager) {
        Map<ResourceLocation, List<JsonElement>> map = new HashMap<>();
        scanDirectory(manager, this.directory, this.gson, map);
        return map;
    }

    private void scanDirectory(DataManager manager, String directory, Gson gson,
        Map<ResourceLocation, List<JsonElement>> map) {
        FileToIdConverter filetoidconverter = FileToIdConverter.json(directory);

        for (Map.Entry<ResourceLocation, Resource> entry : filetoidconverter.listMatchingResources(manager)
            .entrySet()) {
            ResourceLocation resourcelocation = entry.getKey();
            ResourceLocation resourcelocation1 = filetoidconverter.fileToId(resourcelocation);

            try (Reader reader = entry.getValue()
                .openAsReader()) {
                JsonElement jsonelement = GsonHelpers.fromJson(gson, reader, JsonElement.class);
                map.computeIfAbsent(resourcelocation1, k -> new ArrayList<>())
                    .add(jsonelement);
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
