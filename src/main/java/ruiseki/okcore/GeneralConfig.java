package ruiseki.okcore;

import ruiseki.okcore.config.ConfigLocation;
import ruiseki.okcore.config.ConfigurableProperty;
import ruiseki.okcore.config.extendedconfig.DummyConfig;
import ruiseki.okcore.tracking.Versions;

/**
 * A config with general options for this mod.
 *
 * @author rubensworks
 *
 */
public class GeneralConfig extends DummyConfig {

    @ConfigurableProperty(
        category = "core",
        comment = "If the version checker should be enabled.",
        configLocation = ConfigLocation.COMMON)
    public static boolean versionChecker = true;

    @ConfigurableProperty(
        category = "core.potion",
        comment = "The minimum array size of potion types, increase to allow for more potion types.",
        requiresMcRestart = true,
        configLocation = ConfigLocation.COMMON)
    public static int minimumPotionTypesArraySize = 256;

    public GeneralConfig() {
        super(OKCore._instance, true, "general", null);
    }

    @Override
    public void onRegistered() {
        if (versionChecker) {
            Versions.registerMod(getMod(), OKCore._instance, Reference.VERSION_URL);
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
