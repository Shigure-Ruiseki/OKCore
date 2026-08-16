package ruiseki.okcore.config.configurabletypeaction;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.common.registry.EntityRegistry;
import ruiseki.okcore.config.extendedconfig.EntityConfig;
import ruiseki.okcore.helper.Helpers;

/**
 * The action used for {@link EntityConfig}.
 * 
 * @param <T> The entity type.
 * @author rubensworks
 * @see ConfigurableTypeAction
 */
public class EntityAction<T extends Entity> extends ConfigurableTypeAction<EntityConfig<T>> {

    @Override
    public void preRun(EntityConfig eConfig, Configuration config, boolean startup) {

    }

    @SuppressWarnings("unchecked")
    @Override
    public void postRun(EntityConfig eConfig, Configuration config) {
        // Save the config inside the correct element
        eConfig.save();

        @SuppressWarnings("unchecked")
        Class<? extends T> clazz = (Class<? extends T>) eConfig.getElement();

        // Register
        EntityRegistry.registerModEntity(
            clazz,
            eConfig.getSubUniqueName(),
            Helpers.getNewId(eConfig.getMod(), Helpers.IDType.ENTITY),
            eConfig.getMod(),
            eConfig.getTrackingRange(),
            eConfig.getUpdateFrequency(),
            eConfig.sendVelocityUpdates());
    }

}
