package ruiseki.okcore;

import org.apache.logging.log4j.Level;

import ruiseki.okcore.config.ConfigurableProperty;
import ruiseki.okcore.config.ConfigurableType;
import ruiseki.okcore.config.ConfigurableTypeCategory;
import ruiseki.okcore.config.extendedconfig.DummyConfig;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.tracking.Versions;

/**
 * A config with general options for this mod.
 *
 * @author rubensworks
 *
 */
public class GeneralConfig extends DummyConfig {

    /**
     * The current mod version, will be used to check if the player's config isn't out of date and
     * warn the player accordingly.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.CORE,
        comment = "Config version for " + Reference.MOD_NAME + ".\nDO NOT EDIT MANUALLY!")
    public static String version = Reference.MOD_VERSION;

    /**
     * If the debug mode should be enabled. @see Debug
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.CORE,
        comment = "Set 'true' to enable development debug mode. This will result in a lower performance!",
        requiresMcRestart = true)
    public static boolean debug = false;

    /**
     * If the recipe loader should crash when finding invalid recipes.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.CORE,
        comment = "If the recipe loader should crash when finding invalid recipes.",
        requiresMcRestart = true)
    public static boolean crashOnInvalidRecipe = false;

    /**
     * If mod compatibility loader should crash hard if errors occur in that process.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.CORE,
        comment = "If mod compatibility loader should crash hard if errors occur in that process.",
        requiresMcRestart = true)
    public static boolean crashOnModCompatCrash = false;

    /**
     * If the version checker should be enabled.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.CORE,
        comment = "If the version checker should be enabled.")
    public static boolean versionChecker = true;

    /**
     * The minimum array size of potion types, increase to allow for more potion types.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.CORE,
        comment = "The minimum array size of potion types, increase to allow for more potion types.",
        requiresMcRestart = true)
    public static int minimumPotionTypesArraySize = 256;

    /**
     * The type of this config.
     */
    public static ConfigurableType TYPE = ConfigurableType.DUMMY;

    /**
     * Create a new instance.
     */
    public GeneralConfig() {
        super(OKCore._instance, true, "general", null);
    }

    @Override
    public void onRegistered() {
        // Check version of config file
        if (!version.equals(Reference.MOD_VERSION)) {
            getMod().log(
                Level.WARN,
                "The config file of " + Reference.MOD_NAME
                    + " is out of date and might cause problems, please remove it so it can be regenerated.");
        }

        getMod().putGenericReference(ModBase.REFKEY_CRASH_ON_INVALID_RECIPE, GeneralConfig.crashOnInvalidRecipe);
        getMod().putGenericReference(ModBase.REFKEY_DEBUGCONFIG, GeneralConfig.debug);
        getMod().putGenericReference(ModBase.REFKEY_CRASH_ON_MODCOMPAT_CRASH, GeneralConfig.crashOnModCompatCrash);

        if (versionChecker) {
            Versions.registerMod(getMod(), OKCore._instance, Reference.VERSION_URL);
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
