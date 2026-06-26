package ruiseki.okcore.init;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ruiseki.okcore.helper.VersionHelpers;

public class UpdateChecker {

    private static final int TIMEOUT_MS = 5000;

    public static void checkUpdates(ModBase mod) {
        if (mod == null) return;

        String urlStr = mod.getReferenceValue(ModBase.REFKEY_VERSION_CHECKER_URL);
        if (urlStr == null || urlStr.trim()
            .isEmpty()) {
            mod.log(Level.INFO, "Update check skipped: No URL provided.");
            return;
        }

        Thread thread = new Thread(() -> {
            try {
                String currentVersion = mod.getReferenceValue(ModBase.REFKEY_MOD_VERSION);
                String userAgent = mod.getModName() + " UpdateChecker/" + currentVersion;

                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", userAgent);
                connection.setConnectTimeout(TIMEOUT_MS);
                connection.setReadTimeout(TIMEOUT_MS);

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
                        JsonElement element = new JsonParser().parse(reader);
                        if (element != null && element.isJsonObject()) {
                            JsonObject json = element.getAsJsonObject();

                            if (json.has("version")) {
                                String latestVersion = json.get("version")
                                    .getAsString();
                                mod.putGenericReference(ModBase.REFKEY_VERSION_CHECKER_LATEST, latestVersion);

                                String status = VersionHelpers.checkStatus(currentVersion, latestVersion);
                                mod.putGenericReference(ModBase.REFKEY_VERSION_CHECKER_STATUS, status);

                                if (VersionHelpers.STATUS_OUTDATED.equals(status)) {
                                    mod.log(
                                        Level.WARN,
                                        "An update is available! Current: %s, Latest: %s",
                                        currentVersion,
                                        latestVersion);
                                } else {
                                    mod.log(Level.INFO, "Mod is up to date (%s).", currentVersion);
                                }
                            }

                            if (json.has("download") && json.get("download")
                                .isJsonObject()) {
                                JsonObject downloadObj = json.getAsJsonObject("download");
                                Map<String, String> downloadMap = Maps.newHashMap();

                                for (Map.Entry<String, JsonElement> entry : downloadObj.entrySet()) {
                                    if (entry.getValue()
                                        .isJsonPrimitive()) {
                                        downloadMap.put(
                                            entry.getKey(),
                                            entry.getValue()
                                                .getAsString());
                                    }
                                }
                                mod.putGenericReference(ModBase.REFKEY_VERSION_CHECKER_DOWNLOADS, downloadMap);
                            }
                        }
                    }
                } else {
                    mod.putGenericReference(ModBase.REFKEY_VERSION_CHECKER_STATUS, VersionHelpers.STATUS_UNKNOWN);
                }
            } catch (Exception e) {
                mod.putGenericReference(ModBase.REFKEY_VERSION_CHECKER_STATUS, VersionHelpers.STATUS_UNKNOWN);
                mod.log(Level.WARN, "Failed to check for updates: " + e.getMessage());
            }
        }, mod.getModName() + " Update Checker");

        thread.setDaemon(true);
        thread.start();
    }
}
