package ruiseki.okcore.datastructure;

import java.util.Map;

import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.data.DataManager;

public class FileToIdConverter {

    private final String prefix;
    private final String extension;

    public FileToIdConverter(String prefix, String extension) {
        this.prefix = prefix;
        this.extension = extension;
    }

    public static FileToIdConverter json(String prefix) {
        return new FileToIdConverter(prefix, ".json");
    }

    public ResourceLocation idToFile(ResourceLocation id) {
        return new ResourceLocation(id.getResourceDomain(), this.prefix + "/" + id.getResourcePath() + this.extension);
    }

    public ResourceLocation fileToId(ResourceLocation fileLocation) {
        String path = fileLocation.getResourcePath();
        String idPath = path.substring(this.prefix.length() + 1, path.length() - this.extension.length());
        return new ResourceLocation(fileLocation.getResourceDomain(), idPath);
    }

    public Map<ResourceLocation, Resource> listMatchingResources(DataManager manager) {
        return manager.listResources(
            this.prefix,
            (location) -> {
                return location.getResourcePath()
                    .endsWith(this.extension);
            });
    }
}
