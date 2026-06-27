package ruiseki.okcore.data.loader.tags;

import java.io.InputStream;

import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.data.loader.DataLoader;
import ruiseki.okcore.data.loader.IDataLoader;
import ruiseki.okcore.tag.ResourceKey;
import ruiseki.okcore.tag.TagKey;
import ruiseki.okcore.tag.TagRegistry;
import ruiseki.okcore.tag.entry.TagEntry;
import ruiseki.okcore.tag.entry.TagEntryRegistry;

@DataLoader
public class TagLoader implements IDataLoader {

    @Override
    public String getTargetFolder() {
        return "tags";
    }

    @Override
    public void process(ResourceLocation id, String namespace, String folder, String[] subPaths, String fileName,
        InputStream inputStream) {
        if (subPaths == null || subPaths.length == 0) return;

        String subfolder = subPaths[0];
        TagReader<?> reader = new TagReader<>(id, fileName, subfolder);

        try {
            TagHolder<?> holder = reader.read(inputStream);
            if (holder == null || holder.values()
                .isEmpty()) return;

            TagEntry<?> factory = TagEntryRegistry.getFactory(subfolder);
            if (factory == null) {
                OKCore.okLog(Level.WARN, "No TagEntry factory registered for subfolder [{}]", subfolder);
                return;
            }

            ResourceKey<?> registryKey = ResourceKey.createRegistryKey(new ResourceLocation("minecraft", subfolder));

            StringBuilder tagPathBuilder = new StringBuilder();
            for (int i = 1; i < subPaths.length; i++) {
                tagPathBuilder.append(subPaths[i])
                    .append("/");
            }

            String cleanFileName = fileName.endsWith(".json") ? fileName.substring(0, fileName.length() - 5) : fileName;
            tagPathBuilder.append(cleanFileName);

            ResourceLocation tagId = new ResourceLocation(namespace, tagPathBuilder.toString());
            TagKey<?> tagKey = TagKey.create(registryKey, tagId);

            TagRegistry.addHolder(tagKey, holder);

        } catch (Exception e) {
            OKCore.okLog(Level.ERROR, "Failed to load datapack tag file [{}]: {}", id.toString(), e.getMessage());
        }
    }

    @Override
    public boolean isModLoader() {
        return false;
    }

    @Override
    public boolean isWorldLoader() {
        return true;
    }
}
