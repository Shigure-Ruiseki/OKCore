package ruiseki.commoncapabilities;

import ruiseki.okcore.Tags;

/**
 * Class that can hold basic static things that are better not hard-coded
 * like mod details, texture paths, ID's...
 *
 * @author rubensworks (aka kroeserr)
 *
 */
@SuppressWarnings("javadoc")
public class Reference {

    // Mod info
    public static final String MOD_ID = "commoncapabilities";
    public static final String MOD_NAME = "CommonCapabilities";
    public static final String MOD_VERSION = Tags.VERSION;
    public static final String MOD_MC_VERSION = Tags.MC_VERSION;
    public static final String VERSION_URL = "https://raw.githubusercontent.com/CyclopsMC/Versions/master/1.12/CommonCapabilities.txt";

    public static final String MOD_DEPENDENCIES = "";

    // Paths
    public static final String TEXTURE_PATH_GUI = "textures/gui/";
    public static final String TEXTURE_PATH_SKINS = "textures/skins/";
    public static final String TEXTURE_PATH_MODELS = "textures/models/";
    public static final String TEXTURE_PATH_ENTITIES = "textures/entities/";
    public static final String TEXTURE_PATH_GUIBACKGROUNDS = "textures/gui/title/background/";
    public static final String TEXTURE_PATH_ITEMS = "textures/items/";
    public static final String TEXTURE_PATH_PARTICLES = "textures/particles/";
    public static final String MODEL_PATH = "models/";

    public static final String MOD_VANILLA = "Minecraft";
}
