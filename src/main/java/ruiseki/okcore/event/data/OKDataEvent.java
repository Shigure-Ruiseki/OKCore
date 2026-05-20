package ruiseki.okcore.event.data;

import java.io.File;

import net.minecraft.server.MinecraftServer;

import cpw.mods.fml.common.eventhandler.Event;

public class OKDataEvent extends Event {

    public static class Pre extends OKDataEvent {
    }

    public static class Post extends OKDataEvent {
    }

    public static class WorldPre extends OKDataEvent {

        private final MinecraftServer server;
        private final File worldDir;

        public WorldPre(MinecraftServer server, File worldDir) {
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

    public static class WorldPost extends OKDataEvent {

        private final MinecraftServer server;
        private final File worldDir;

        public WorldPost(MinecraftServer server, File worldDir) {
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

    public static class WorldUnload extends OKDataEvent {

        public WorldUnload() {}
    }

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
