package ruiseki.okcore.network.packet;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import org.apache.logging.log4j.Level;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.network.PacketCodec;
import ruiseki.okcore.tag.TagKey;
import ruiseki.okcore.tag.TagManager;
import ruiseki.okcore.tag.entry.ITagEntrySerializer;
import ruiseki.okcore.tag.entry.TagEntry;
import ruiseki.okcore.tag.entry.TagEntryRegistry;

public class PacketUpdateTags extends PacketCodec {

    private Map<TagKey<?>, Set<TagEntry<?>>> tagsMap = new HashMap<>();

    public PacketUpdateTags() {}

    public PacketUpdateTags(Map<TagKey<?>, Set<TagEntry<?>>> tagsMap) {
        this.tagsMap = tagsMap;
    }

    @Override
    @SuppressWarnings({ "rawtypes" })
    public void decode(ExtendedBuffer input) {
        this.tagsMap = new HashMap<>();
        try {
            int tagCount = input.readVarIntFromBuffer();
            for (int i = 0; i < tagCount; i++) {
                TagKey<?> tagKey = TagKey.fromNetwork(input);

                String subfolder = tagKey.registry()
                    .location()
                    .getResourcePath();

                ITagEntrySerializer serializer = TagEntryRegistry.getSerializer(subfolder);

                int entryCount = input.readVarIntFromBuffer();
                Set<TagEntry<?>> entries = new HashSet<>();

                for (int j = 0; j < entryCount; j++) {
                    if (serializer != null) {
                        TagEntry<?> entry = serializer.fromNetwork(input);
                        if (entry != null) {
                            entries.add(entry);
                        }
                    } else {
                        throw new IOException("Missing TagEntry serializer for subfolder: " + subfolder);
                    }
                }

                if (!entries.isEmpty()) {
                    this.tagsMap.put(tagKey, entries);
                }
            }
        } catch (IOException e) {
            OKCore.okLog(Level.ERROR, "Failed to decode datapack tags network packet", e);
        }
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void encode(ExtendedBuffer output) {
        try {
            output.writeVarIntToBuffer(this.tagsMap.size());

            for (Map.Entry<TagKey<?>, Set<TagEntry<?>>> entry : this.tagsMap.entrySet()) {
                TagKey<?> tagKey = entry.getKey();
                Set<TagEntry<?>> entries = entry.getValue();

                tagKey.toNetwork(output);

                String subfolder = tagKey.registry()
                    .location()
                    .getResourcePath();

                ITagEntrySerializer serializer = TagEntryRegistry.getSerializer(subfolder);
                if (serializer == null) {
                    throw new IOException("Missing TagEntry serializer for subfolder: " + subfolder);
                }

                output.writeVarIntToBuffer(entries.size());
                for (TagEntry<?> tagEntry : entries) {
                    if (tagEntry != null) {
                        serializer.toNetwork(output, tagEntry);
                    }
                }
            }
        } catch (IOException e) {
            OKCore.okLog(Level.ERROR, "Failed to encode datapack tags network packet", e);
        }
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void actionClient(World world, EntityPlayer player) {
        TagManager.getManager()
            .replaceTags(this.tagsMap);
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {}
}
