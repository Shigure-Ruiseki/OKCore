package ruiseki.okcore.config.extendedconfig;

import java.util.function.Function;

import net.minecraft.client.gui.GuiScreen;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.client.gui.GuiScreens;
import ruiseki.okcore.client.gui.GuiType;
import ruiseki.okcore.client.gui.IContainerAccess;
import ruiseki.okcore.config.ConfigurableType;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.inventory.container.ContainerExtended;

public abstract class GuiConfig<T extends ContainerExtended> extends ExtendedConfig<GuiConfig<T>, GuiType<T>> {

    /**
     * Create a new config
     *
     * @param mod            The mod instance.
     * @param enabled        Whether this config is enabled.
     * @param namedId        A unique name id.
     * @param comment        Comment for config file.
     * @param elementFactory The element constructor.
     */
    public GuiConfig(ModBase mod, boolean enabled, String namedId, String comment,
        Function<GuiConfig<T>, GuiType<T>> elementFactory) {
        super(mod, enabled, namedId, comment, elementFactory);
    }

    @Override
    public String getUnlocalizedName() {
        return "guis." + getMod().getModId() + "." + getNamedId();
    }

    @Override
    public String getFullUnlocalizedName() {
        return "gui." + getUnlocalizedName() + ".name";
    }

    @Override
    public ConfigurableType getHolderType() {
        return ConfigurableType.GUI;
    }

    @SideOnly(Side.CLIENT)
    public abstract <U extends GuiScreen & IContainerAccess<T>> GuiScreens.ScreenConstructor<T, U> getScreenFactory();

    @Override
    public boolean isDisableable() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onRegistered() {
        super.onRegistered();
        GuiScreens.register(getInstance(), getScreenFactory());
    }
}
