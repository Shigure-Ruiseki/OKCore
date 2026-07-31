package ruiseki.okcore.config.configurabletypeaction;

import net.minecraft.entity.EntityLiving;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.common.registry.EntityRegistry;
import ruiseki.okcore.config.extendedconfig.MobConfig;
import ruiseki.okcore.helper.Helpers;

/**
 * The action used for {@link MobConfig}.
 * 
 * @author rubensworks
 * @see ConfigurableTypeAction
 */
public class MobAction extends ConfigurableTypeAction<MobConfig> {

    @Override
    public void preRun(MobConfig eConfig, Configuration config, boolean startup) {

    }

    @SuppressWarnings("unchecked")
    @Override
    public void postRun(MobConfig eConfig, Configuration config) {
        // Save the config inside the correct element
        eConfig.save();

        // Register mob
        Class<? extends EntityLiving> clazz = (Class<? extends EntityLiving>) eConfig.getElement();
        int modEntityId = Helpers.getNewId(eConfig.getMod(), Helpers.IDType.ENTITY);

        EntityRegistry.registerModEntity(clazz, eConfig.getNamedId(), modEntityId, eConfig.getMod(), 80, 3, true);

        if (eConfig.hasSpawnEgg()) {
            int globalId = EntityRegistry.findGlobalUniqueEntityId();
            EntityRegistry.registerGlobalEntityID(
                clazz,
                eConfig.getNamedId(),
                globalId,
                eConfig.getBackgroundEggColor(),
                eConfig.getForegroundEggColor());
        }
    }

}
