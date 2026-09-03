package ruiseki.okcore.config;

import java.util.Locale;

public enum ConfigLocation {

    /**
     * Common mod config for configuration that needs to be loaded on both environments.
     * Loaded on both servers and clients.
     * Stored in the global config directory.
     * Not synced.
     * Suffix is "-common" by default.
     */
    COMMON(true),
    /**
     * Client config is for configuration affecting the ONLY client state such as graphical options.
     * Only loaded on the client side.
     * Stored in the global config directory.
     * Not synced.
     * Suffix is "-client" by default.
     */
    CLIENT(false),
    /**
     * Server type config is configuration that is associated with a server instance.
     * Only loaded during server startup.
     * Stored in a server/save specific "serverconfig" directory.
     * Synced to clients during connection.
     * Suffix is "-server" by default.
     */
    SERVER(true);

    private final boolean syncToServer;

    ConfigLocation(boolean syncToServer) {
        this.syncToServer = syncToServer;
    }

    public boolean isSyncToServer() {
        return syncToServer;
    }

    public String extension() {
        return this.name()
            .toLowerCase(Locale.ENGLISH);
    }
}
