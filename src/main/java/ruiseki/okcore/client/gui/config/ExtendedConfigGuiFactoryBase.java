package ruiseki.okcore.client.gui.config;

import java.util.Set;

import net.minecraft.client.Minecraft;

import cpw.mods.fml.client.IModGuiFactory;

/**
 * Config gui factory class.
 * 
 * @author rubensworks
 *
 */
public abstract class ExtendedConfigGuiFactoryBase implements IModGuiFactory {

    @Override
    public void initialize(Minecraft minecraftInstance) {

    }

    @Override
    public abstract Class<? extends GuiConfigOverviewBase> mainConfigGuiClass();

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return null;
    }

}
