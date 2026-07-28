package ruiseki.okcore.client.mui;

import com.cleanroommc.modularui.drawable.AdaptableUITexture;
import com.cleanroommc.modularui.drawable.UITexture;

import ruiseki.okcore.Reference;

public class OKCGuiTextures {

    public static final UITexture EMPTY_BATTERY_INPUT = UITexture.builder()
        .location(Reference.MOD_ID, "items/empty_battery_input_slot")
        .imageSize(16, 16)
        .build();

    public static final UITexture EMPTY_BATTERY_OUTPUT = UITexture.builder()
        .location(Reference.MOD_ID, "items/empty_battery_output_slot")
        .imageSize(16, 16)
        .build();

    public static final UITexture EMPTY_TANK_INPUT = UITexture.builder()
        .location(Reference.MOD_ID, "items/empty_tank_input_slot")
        .imageSize(16, 16)
        .build();

    public static final UITexture EMPTY_TANK_OUTPUT = UITexture.builder()
        .location(Reference.MOD_ID, "items/empty_tank_output_slot")
        .imageSize(16, 16)
        .build();

    public static final UITexture EMPTY_UPGRADE = UITexture.builder()
        .location(Reference.MOD_ID, "items/empty_upgrade_slot")
        .imageSize(16, 16)
        .build();

    public static final UITexture INACCESSIBLE_SLOT = UITexture.builder()
        .location(Reference.MOD_ID, "items/inaccessible_slot")
        .imageSize(16, 16)
        .build();

    public static final UITexture STANDARD_BUTTON = UITexture.builder()
        .location(Reference.MOD_ID, "gui/mui/gui_controls.png")
        .imageSize(256, 256)
        .xy(29, 0, 18, 18)
        .build();
    public static final UITexture STANDARD_BUTTON_HOVERED = UITexture.builder()
        .location(Reference.MOD_ID, "gui/mui/gui_controls.png")
        .imageSize(256, 256)
        .xy(47, 0, 18, 18)
        .build();

    public static final UITexture HIGH_OFF = UITexture.builder()
        .location(Reference.MOD_ID, "gui/mui/redstone_mode.png")
        .imageSize(256, 256)
        .xy(0, 0, 16, 16)
        .build();

    public static final UITexture HIGH_ON = UITexture.builder()
        .location(Reference.MOD_ID, "gui/mui/redstone_mode.png")
        .imageSize(256, 256)
        .xy(16, 0, 16, 16)
        .build();

    public static final UITexture ALWAYS_ON = UITexture.builder()
        .location(Reference.MOD_ID, "gui/mui/redstone_mode.png")
        .imageSize(256, 256)
        .xy(32, 0, 16, 16)
        .build();
    public static final UITexture ALWAYS_OFF = UITexture.builder()
        .location(Reference.MOD_ID, "gui/mui/redstone_mode.png")
        .imageSize(256, 256)
        .xy(48, 0, 16, 16)
        .build();

    public static final AdaptableUITexture TITLE_TEXTURE = (AdaptableUITexture) UITexture.builder()
        .location(Reference.MOD_ID, "gui/mui/gui_controls.png")
        .imageSize(256, 256)
        .xy(128, 0, 128, 10)
        .adaptable(4)
        .tiled()
        .build();

    public static final UITexture VANILLA_SEARCH_BACKGROUND = UITexture.builder()
        .location(Reference.MOD_ID, "gui/minecraft/vanilla_search")
        .imageSize(18, 18)
        .adaptable(1)
        .name("vanilla_search")
        .build();

    public static final UITexture SLIDER_BACKGROUND = UITexture.builder()
        .location(Reference.MOD_ID, "gui/minecraft/slider")
        .imageSize(200, 20)
        .name("slider")
        .build();
}
