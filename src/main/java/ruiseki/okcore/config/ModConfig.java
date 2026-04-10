package ruiseki.okcore.config;

import com.gtnewhorizon.gtnhlib.config.Config;
import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;

import ruiseki.okcore.Reference;

@Config.LangKey("config.generalConfig")
@Config(modid = Reference.MOD_ID, configSubDirectory = Reference.MOD_ID, category = "general")
public class ModConfig {

    public static void registerConfig() throws ConfigException {
        ConfigurationManager.registerConfig(ModConfig.class);
    }

    @Config.DefaultBoolean(true)
    public static boolean useItemStackCap;

    @Config.DefaultBoolean(true)
    public static boolean useEntityCap;

    @Config.DefaultBoolean(true)
    public static boolean useItemSharedNBT;
}
