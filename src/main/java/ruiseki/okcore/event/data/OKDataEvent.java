package ruiseki.okcore.event.data;

import java.io.File;

import net.minecraft.server.MinecraftServer;

import cpw.mods.fml.common.eventhandler.Event;

public class OKDataEvent extends Event {

    public static class Reload extends OKDataEvent {

        private final MinecraftServer server;
        private final File worldDir;

        public Reload(MinecraftServer server, File worldDir) {
            this.server = server;
            this.worldDir = worldDir;
        }

        public MinecraftServer getServer() {
            return server;
        }

        public File getWorldDir() {
            return worldDir;
        }
    }
}
