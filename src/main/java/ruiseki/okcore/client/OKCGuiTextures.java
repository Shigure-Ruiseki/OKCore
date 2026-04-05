package ruiseki.okcore.client;

import com.cleanroommc.modularui.drawable.UITexture;

import ruiseki.okcore.Reference;

public class OKCGuiTextures {

    public static final UITexture EMPTY_BATTERY_INPUT = UITexture.builder()
        .location(Reference.MOD_ID, "item/empty_battery_input_slot")
        .imageSize(16, 16)
        .build();

    public static final UITexture EMPTY_BATTERY_OUTPUT = UITexture.builder()
        .location(Reference.MOD_ID, "item/empty_battery_output_slot")
        .imageSize(16, 16)
        .build();

    public static final UITexture EMPTY_TANK_INPUT = UITexture.builder()
        .location(Reference.MOD_ID, "item/empty_tank_input_slot")
        .imageSize(16, 16)
        .build();

    public static final UITexture EMPTY_TANK_OUTPUT = UITexture.builder()
        .location(Reference.MOD_ID, "item/empty_tank_output_slot")
        .imageSize(16, 16)
        .build();

    public static final UITexture EMPTY_UPGRADE = UITexture.builder()
        .location(Reference.MOD_ID, "item/empty_upgrade_slot")
        .imageSize(16, 16)
        .build();

    public static final UITexture INACCESSIBLE_SLOT = UITexture.builder()
        .location(Reference.MOD_ID, "item/inaccessible_slot")
        .imageSize(16, 16)
        .build();
}
