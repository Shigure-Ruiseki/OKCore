
package ruiseki.okcore.network.packet;

import java.io.IOException;
import java.util.Collections;
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

public class PacketUpdateTags extends PacketCodec {

    private Map<TagKey<?>, Set<String>> tagsMap = Collections.emptyMap();

    public PacketUpdateTags() {}

    public PacketUpdateTags(Map<TagKey<?>, Set<String>> tagsMap) {
        this.tagsMap = new HashMap<>(tagsMap);
    }

    @Override
    public void decode(ExtendedBuffer input) {
        Map<TagKey<?>, Set<String>> decoded = new HashMap<>();

        try {
            int tagCount = input.readVarIntFromBuffer();
            if (tagCount < 0) {
                throw new IOException("Invalid tag count: " + tagCount);
            }
            for (int i = 0; i < tagCount; i++) {
                TagKey<?> tagKey = TagKey.fromNetwork(input);
                int entryCount = input.readVarIntFromBuffer();
                if (entryCount < 0) {
                    throw new IOException("Invalid entry count: " + entryCount);
                }
                Set<String> entries = new HashSet<>();
                for (int j = 0; j < entryCount; j++) {
                    String value = input.readStringFromBuffer(32767);
                    entries.add(value);
                }
                if (tagKey != null) {
                    decoded.put(tagKey, entries);
                }
            }
            this.tagsMap = decoded;
        } catch (IOException | RuntimeException e) {
            this.tagsMap = Collections.emptyMap();
            OKCore.okLog(Level.ERROR, "Failed to decode datapack tags network packet", e);
        }
    }

    @Override
    public void encode(ExtendedBuffer output) {
        try {
            output.writeVarIntToBuffer(this.tagsMap.size());
            for (Map.Entry<TagKey<?>, Set<String>> entry : this.tagsMap.entrySet()) {
                TagKey<?> tagKey = entry.getKey();
                Set<String> entries = entry.getValue();
                tagKey.toNetwork(output);
                output.writeVarIntToBuffer(entries.size());
                for (String value : entries) {
                    output.writeStringToBuffer(value);
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
