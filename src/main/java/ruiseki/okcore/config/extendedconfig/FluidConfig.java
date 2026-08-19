package ruiseki.okcore.config.extendedconfig;

import java.util.function.Function;

import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.config.ConfigurableType;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.init.ModBase;

/**
 * Config for fluids.
 *
 * @author rubensworks
 * @see ExtendedConfig
 */
public abstract class FluidConfig extends ExtendedConfig<FluidConfig, Fluid> {

    /**
     * Make a new instance.
     *
     * @param mod            The mod instance.
     * @param enabled        If this should is enabled.
     * @param namedId        The unique name ID for the configurable.
     * @param comment        The comment to add in the config file for this configurable.
     * @param elementFactory Function factory to create the Fluid instance.
     */
    public FluidConfig(ModBase mod, boolean enabled, String namedId, String comment,
        Function<FluidConfig, Fluid> elementFactory) {
        super(mod, enabled, namedId, comment, elementFactory);
        if (MinecraftHelpers.isClientSide()) MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public String getUnlocalizedName() {
        return "fluids." + getMod().getModId() + "." + getNamedId();
    }

    @Override
    public ConfigurableType getHolderType() {
        return ConfigurableType.FLUID;
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

    /**
     * Get the still icon location.
     *
     * @return The icon location.
     */
    public ResourceLocation getIconLocationStill() {
        return new ResourceLocation(getMod().getModId(), "blocks/" + getNamedId() + "_still");
    }

    /**
     * Get the flow icon location.
     *
     * @return The icon location.
     */
    public ResourceLocation getIconLocationFlow() {
        return new ResourceLocation(getMod().getModId(), "blocks/" + getNamedId() + "_flow");
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onTextureStitch(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() == 0) {
            IIcon still = event.map.registerIcon(getMod().getModId() + getNamedId() + "_still");
            IIcon flowing = event.map.registerIcon(getMod().getModId() + getNamedId() + "_flow");
            if (getInstance() != null) {
                getInstance().setIcons(still, flowing);
            }
        }
    }
}
