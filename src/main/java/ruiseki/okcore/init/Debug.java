package ruiseki.okcore.init;

import java.util.Set;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Sets;

import ruiseki.okcore.config.extendedconfig.ExtendedConfig;

/**
 * Helps with code debugging.
 *
 * @author rubensworks
 *
 */
public class Debug {

    private static final String CONFIGCHECKER_PREFIX = "[CONFIGCHECKER] ";
    private final Set<ExtendedConfig<?, ?>> savedConfigs = Sets.newHashSet();
    private final ModBase mod;

    private boolean ok = true;

    public Debug(ModBase mod) {
        this.mod = mod;
    }

    /**
     * Loops over the list of configs and checks their correctness.
     *
     * @param configs List of configs
     */
    public void checkPreConfigurables(Set<ExtendedConfig<?, ?>> configs) {
        for (ExtendedConfig<?, ?> config : configs) {
            // _instance field on ExtendedConfig
            try {
                config.getClass()
                    .getField("_instance");
            } catch (NoSuchFieldException e) {
                log(config + " has no static '_instance' field.");
            } catch (SecurityException e) {
                log(config + " has a non-public static '_instance' field, make it public.");
            }
        }

        // Save for Post call
        savedConfigs.addAll(configs);
    }

    /**
     * Loops over the list of configs (was saved from the Pre call) and checks their correctness.
     */
    public void checkPostConfigurables() {
        for (ExtendedConfig<?, ?> config : savedConfigs) {
            if (config.getHolderType()
                .hasUniqueInstance() && config.isEnabled()) {
                if (config.getInstance() == null) {
                    log(
                        config.getNamedId() + " ("
                            + config.getClass()
                                .getSimpleName()
                            + ") has no instance registered, even though it is enabled.");
                }
            }
        }

        if (ok) {
            log("Everything is just fine!");
        }
    }

    private void log(String message) {
        ok = false;
        mod.log(Level.INFO, CONFIGCHECKER_PREFIX + message);
    }

}
