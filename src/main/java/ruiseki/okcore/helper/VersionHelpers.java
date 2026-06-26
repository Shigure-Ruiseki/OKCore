package ruiseki.okcore.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionHelpers {

    // Default scheme: Ignores all non-numeric characters at the beginning of the string (e.g., "v", "ver-", "release-")
    private static final Pattern DEFAULT_SCHEME_PATTERN = Pattern.compile("^[^0-9]*+(.*)$");

    public static final String STATUS_UP_TO_DATE = "UP_TO_DATE";
    public static final String STATUS_OUTDATED = "OUTDATED";
    public static final String STATUS_UNKNOWN = "UNKNOWN";

    /**
     * Determines the status of the current version compared to the latest hosted version.
     * * @param currentVersion The active version of the mod.
     * 
     * @param latestVersion The latest version parsed from the remote JSON host.
     * @param schemeRegex   Optional custom regex scheme. Pass null to use default.
     * @return "UP_TO_DATE" if current >= latest, "OUTDATED" if current < latest, or "UNKNOWN" on error.
     */
    public static String checkStatus(String currentVersion, String latestVersion, String schemeRegex) {
        if (currentVersion == null || latestVersion == null) {
            return STATUS_UNKNOWN;
        }

        int comparison = compare(currentVersion, latestVersion, schemeRegex);

        if (comparison < 0) {
            return STATUS_OUTDATED;
        }

        return STATUS_UP_TO_DATE;
    }

    /**
     * Overload method for checkStatus utilizing the default scheme.
     */
    public static String checkStatus(String currentVersion, String latestVersion) {
        return checkStatus(currentVersion, latestVersion, null);
    }

    /**
     * Strips build metadata, pre-release identifiers, and custom scheme prefixes.
     * 
     * @param version     The version string to normalize.
     * @param schemeRegex Custom regex to match and extract the version part.
     *                    Must contain at least 1 capturing group to extract the numeric version.
     *                    If null or empty, the default scheme is used to strip leading non-numeric characters.
     * @return The base numeric version string (e.g., "26.06.21.0")
     */
    public static String toBaseVersion(String version, String schemeRegex) {
        if (version == null || version.isEmpty()) return "0.0.0.0";

        // 1. Strip build metadata first ("+...")
        int plus = version.indexOf('+');
        if (plus != -1) version = version.substring(0, plus);

        // 2. Strip pre-release identifier ("-...")
        int hyphen = version.indexOf('-');
        if (hyphen != -1) version = version.substring(0, hyphen);

        // 3. Handle Scheme (Prefix) using Regex
        Pattern pattern = (schemeRegex != null && !schemeRegex.isEmpty()) ? Pattern.compile(schemeRegex)
            : DEFAULT_SCHEME_PATTERN;

        Matcher matcher = pattern.matcher(version);
        if (matcher.find() && matcher.groupCount() >= 1) {
            version = matcher.group(1); // Extract the content from the first capturing group
        }

        return version;
    }

    /**
     * Overload method utilizing the default scheme (automatically strips leading non-numeric characters).
     */
    public static String toBaseVersion(String version) {
        return toBaseVersion(version, null);
    }

    /**
     * Compares two version strings segment-by-segment (dot-separated), allowing a custom scheme regex.
     *
     * @param v1          The first version string
     * @param v2          The second version string
     * @param schemeRegex Regex used to extract the version part (e.g., "^release-(.*)$"). Pass null to use default.
     * @return 0 if equal, negative if v1 < v2, positive if v1 > v2
     */
    public static int compare(String v1, String v2, String schemeRegex) {
        v1 = toBaseVersion(v1, schemeRegex);
        v2 = toBaseVersion(v2, schemeRegex);

        if (v1.isEmpty()) v1 = "0.0.0.0";
        if (v2.isEmpty()) v2 = "0.0.0.0";

        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");
        int length = Math.max(parts1.length, parts2.length);

        for (int i = 0; i < length; i++) {
            int p1 = i < parts1.length ? parseSafe(parts1[i]) : 0;
            int p2 = i < parts2.length ? parseSafe(parts2[i]) : 0;

            if (p1 < p2) return -1;
            if (p1 > p2) return 1;
        }

        return 0;
    }

    /**
     * Overload method comparing versions using the default scheme.
     */
    public static int compare(String v1, String v2) {
        return compare(v1, v2, null);
    }

    private static int parseSafe(String s) {
        try {
            // Keep only valid numeric digits for parsing
            String cleaned = s.replaceAll("[^0-9]", "");
            if (cleaned.isEmpty()) return 0;
            return Integer.parseInt(cleaned);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
