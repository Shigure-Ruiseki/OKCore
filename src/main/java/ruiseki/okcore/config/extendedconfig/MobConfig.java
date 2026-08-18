package ruiseki.okcore.config.extendedconfig;

import java.util.function.Function;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLiving;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.config.ConfigurableType;
import ruiseki.okcore.init.ModBase;

/**
 * Config for mobs.
 *
 * @author rubensworks
 * @see ExtendedConfig
 */
public abstract class MobConfig extends ExtendedConfig<MobConfig, EntityLiving> {

    /**
     * Make a new instance.
     *
     * @param mod            The mod instance.
     * @param enabled        If this should is enabled.
     * @param namedId        The unique name ID for the configurable.
     * @param comment        The comment to add in the config file for this configurable.
     * @param elementFactory Function factory to create the EntityLiving instance.
     */
    public MobConfig(ModBase mod, boolean enabled, String namedId, String comment,
        Function<MobConfig, EntityLiving> elementFactory) {
        super(mod, enabled, namedId, comment, elementFactory);
    }

    @Override
    public String getUnlocalizedName() {
        return "entity.mob." + getNamedId();
    }

    @Override
    public ConfigurableType getHolderType() {
        return ConfigurableType.MOB;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onInit(Step step) {
        super.onInit(step);
        if (step == Step.INIT) {
            Render render = getRender(RenderManager.instance);
            if (render != null) {
                getMod().getProxy()
                    .registerRenderer(getMobClass(), render);
            }
        }
    }

    /**
     * Get the class of the mob entity.
     *
     * @return The mob entity class.
     */
    public abstract Class<? extends EntityLiving> getMobClass();

    /**
     * @return If a spawn egg should be registered for this mob.
     */
    public boolean hasSpawnEgg() {
        return true;
    }

    /**
     * Get the background color of the spawn egg.
     *
     * @return The spawn egg background color.
     */
    public abstract int getBackgroundEggColor();

    /**
     * Get the foreground color of the spawn egg.
     *
     * @return The spawn egg foreground color.
     */
    public abstract int getForegroundEggColor();

    /**
     * Get the render for this configurable.
     *
     * @param renderManager The render manager.
     * @return Get the render.
     */
    public abstract Render getRender(RenderManager renderManager);

}
