package ruiseki.okcore.config.extendedconfig;

import java.util.function.Function;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.config.ConfigurableType;
import ruiseki.okcore.init.ModBase;

/**
 * Config for entities.
 * For mobs, there is the {@link MobConfig}.
 *
 * @param <T> The entity type
 * @author rubensworks
 * @see ExtendedConfig
 */
public abstract class EntityConfig<T extends Entity> extends ExtendedConfig<EntityConfig<T>, Entity> {

    /**
     * Make a new instance.
     *
     * @param mod            The mod instance.
     * @param enabled        If this is enabled.
     * @param namedId        The unique name ID for the configurable.
     * @param comment        The comment to add in the config file for this configurable.
     * @param elementFactory Function factory to create the Entity instance.
     */
    public EntityConfig(ModBase mod, boolean enabled, String namedId, String comment,
        Function<EntityConfig<T>, Entity> elementFactory) {
        super(mod, enabled, namedId, comment, elementFactory);
    }

    @Override
    public String getUnlocalizedName() {
        return "entity." + getNamedId();
    }

    @Override
    public ConfigurableType getHolderType() {
        return ConfigurableType.ENTITY;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onInit(Step step) {
        super.onInit(step);
        if (step == Step.INIT) {
            Render render = getRender(RenderManager.instance, RenderItem.getInstance());
            if (render != null) {
                getMod().getProxy()
                    .registerRenderer(getEntityClass(), render);
            }
        }
    }

    /**
     * Get the class of the entity.
     *
     * @return The entity class.
     */
    public abstract Class<? extends T> getEntityClass();

    /**
     * The range at which MC will send tracking updates.
     *
     * @return The tracking range.
     */
    public int getTrackingRange() {
        return 160;
    }

    /**
     * The frequency of tracking updates.
     *
     * @return The update frequency.
     */
    public int getUpdateFrequency() {
        return 10;
    }

    /**
     * Whether to send velocity information packets as well.
     *
     * @return Send velocity updates?
     */
    public boolean sendVelocityUpdates() {
        return false;
    }

    protected abstract Render getRender(RenderManager renderManager, RenderItem renderItem);
}
