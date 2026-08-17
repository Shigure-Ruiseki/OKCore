package ruiseki.okcore.config.configurable;

import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.config.extendedconfig.FluidConfig;

/**
 * Fluid that can hold ExtendedConfigs
 *
 * @author rubensworks
 */
public abstract class ConfigurableFluid extends Fluid implements IConfigurable<FluidConfig> {

    protected FluidConfig eConfig = null;

    /**
     * Make a new fluid instance.
     *
     * @param eConfig Config for this blockState.
     */
    protected ConfigurableFluid(ExtendedConfig<FluidConfig> eConfig) {
        super(eConfig.getNamedId());
        this.setConfig((FluidConfig) eConfig);
        this.setUnlocalizedName(eConfig.getUnlocalizedName());
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setConfig(FluidConfig eConfig) {
        this.eConfig = eConfig;
    }

    @Override
    public FluidConfig getConfig() {
        return eConfig;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onTextureStitch(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() == 0) {
            String modId = eConfig.getMod()
                .getModId();
            String name = eConfig.getNamedId();
            IIcon still = event.map.registerIcon(modId + ":" + name + "_still");
            IIcon flowing = event.map.registerIcon(modId + ":" + name + "_flow");
            this.setIcons(still, flowing);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getStillIcon() {
        return this.stillIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getFlowingIcon() {
        return this.flowingIcon;
    }
}
