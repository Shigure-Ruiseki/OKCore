package ruiseki.okcore.network.packet;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import ruiseki.okcore.config.ConfigHandler;
import ruiseki.okcore.config.ConfigProperty;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.network.PacketCodec;

public class PacketSyncConfig extends PacketCodec {

    private Map<String, String> syncedProperties = new HashMap<>();

    public PacketSyncConfig() {}

    public PacketSyncConfig(Map<String, ConfigProperty> properties) {
        for (Map.Entry<String, ConfigProperty> entry : properties.entrySet()) {
            ConfigProperty prop = entry.getValue();
            if (prop.getLocation() != null && prop.getLocation()
                .isSyncToServer()) {
                Object val = prop.getValue();
                String stringVal;

                // FIX: Properly stringify array elements instead of default Object.toString()
                if (val instanceof String[]) {
                    stringVal = String.join(";", (String[]) val);
                } else if (val instanceof int[]arr) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < arr.length; i++) {
                        if (i > 0) sb.append(";");
                        sb.append(arr[i]);
                    }
                    stringVal = sb.toString();
                } else {
                    stringVal = String.valueOf(val);
                }

                this.syncedProperties.put(entry.getKey(), stringVal);
            }
        }
    }

    @Override
    public void encode(ExtendedBuffer output) {
        super.encode(output);
        output.writeInt(syncedProperties.size());
        for (Map.Entry<String, String> entry : syncedProperties.entrySet()) {
            output.writeString(entry.getKey());
            output.writeString(entry.getValue());
        }
    }

    @Override
    public void decode(ExtendedBuffer input) {
        super.decode(input);
        int size = input.readInt();
        for (int i = 0; i < size; i++) {
            String key = input.readString();
            String value = input.readString();
            syncedProperties.put(key, value);
        }
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void actionClient(World world, EntityPlayer player) {
        ConfigHandler.onSync(this);
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {

    }

    public Map<String, String> getSyncedProperties() {
        return syncedProperties;
    }
}
