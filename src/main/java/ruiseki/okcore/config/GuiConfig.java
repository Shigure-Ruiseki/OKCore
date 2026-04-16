package ruiseki.okcore.config;

import net.minecraft.client.gui.GuiScreen;

import com.gtnewhorizon.gtnhlib.config.ConfigException;
import com.gtnewhorizon.gtnhlib.config.SimpleGuiConfig;

import ruiseki.okcore.Reference;

public class GuiConfig extends SimpleGuiConfig {

    public GuiConfig(GuiScreen parent) throws ConfigException {
        super(parent, Reference.MOD_ID, Reference.MOD_NAME);
    }
}
