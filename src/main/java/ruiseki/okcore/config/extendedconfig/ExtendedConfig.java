package ruiseki.okcore.config.extendedconfig;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Lists;

import lombok.Getter;
import ruiseki.okcore.config.ConfigProperty;
import ruiseki.okcore.config.ConfigPropertyCallback;
import ruiseki.okcore.config.ConfigurableProperty;
import ruiseki.okcore.config.ConfigurableType;
import ruiseki.okcore.config.IChangedCallback;
import ruiseki.okcore.config.OKCoreConfigException;
import ruiseki.okcore.init.IInitListener;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.registries.IForgeRegistry;

/**
 * A config that refers to an instance of {@link I}. Every unique entry must have one
 * unique extension of this class.
 *
 * @author rubensworks
 * @param <C> Class of the extension of ExtendedConfig
 * @param <I> Class of the instance that is being configured
 */
public abstract class ExtendedConfig<C extends ExtendedConfig<C, I>, I>
    implements Comparable<ExtendedConfig<C, I>>, IInitListener {

    @Getter
    private final ModBase mod;
    private boolean enabled;
    @Getter
    private final String namedId;
    @Getter
    private final String comment;

    @Getter
    @Nullable
    private final Function<C, I> factory;

    private I instance;

    /**
     * A list of {@link ConfigProperty} that can contain additional settings for this configurable.
     */
    public List<ConfigProperty> configProperties = Lists.newLinkedList();

    /**
     * Create a new config using a Function Factory
     */
    public ExtendedConfig(ModBase mod, boolean enabled, String namedId, String comment, Function<C, I> factory) {
        this.mod = mod;
        this.enabled = enabled;
        this.namedId = namedId.toLowerCase(Locale.ROOT);
        this.comment = comment;
        this.factory = factory;
        try {
            generateConfigProperties();
        } catch (IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
    }

    private void generateConfigProperties() throws IllegalArgumentException, IllegalAccessException {
        for (Field field : this.getClass()
            .getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigurableProperty.class)) {
                ConfigurableProperty annotation = field.getAnnotation(ConfigurableProperty.class);
                IChangedCallback changedCallback = null;
                if (annotation.changedCallback() != IChangedCallback.class) {
                    try {
                        changedCallback = annotation.changedCallback()
                            .newInstance();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    }
                }
                String category = annotation.category();
                ConfigProperty configProperty = new ConfigProperty(
                    getMod(),
                    category,
                    getConfigPropertyPrefix() + "." + field.getName(),
                    field.get(null),
                    annotation.comment(),
                    new ConfigPropertyCallback(changedCallback),
                    annotation.isCommandable(),
                    annotation.configLocation(),
                    field);
                configProperty.setRequiresWorldRestart(annotation.requiresWorldRestart());
                configProperty.setRequiresMcRestart(annotation.requiresMcRestart());
                configProperty.setShowInGui(annotation.showInGui());
                configProperty.setMinValue(annotation.minimalValue());
                configProperty.setMaxValue(annotation.maximalValue());
                configProperties.add(configProperty);
            }
        }
    }

    /**
     * @return The prefix that will be used inside the config file for {@link ConfigurableProperty}'s.
     */
    protected String getConfigPropertyPrefix() {
        return this.getNamedId();
    }

    /**
     * Save this config inside the correct element and inside the implementation of itself.
     */
    public void save() {
        try {
            // Save inside the self-implementation
            try {
                this.getClass()
                    .getField("_instance")
                    .set(null, this);
            } catch (NoSuchFieldError e) {
                throw new OKCoreConfigException(
                    String.format("The config file for %s requires a static field _instance.", this.getNamedId()));
            }

            if (this.factory != null) {
                this.instance = this.factory.apply(downCast());
            }
        } catch (IllegalAccessException | NoSuchFieldException | RuntimeException e) {
            mod.getLoggerHelper()
                .getLogger()
                .error(String.format("Registering %s caused an issue. ", getNamedId()), e);
            throw new OKCoreConfigException(
                String.format("Registering %s caused the issue: %s", this.getNamedId(), e.getMessage()));
        }
    }

    /**
     * Return the configurable type for which this config holds data
     *
     * @return the type of the configurable to where the config belongs
     */
    public abstract ConfigurableType getHolderType();

    /**
     * Get the unlocalized name (must be unique!) for this configurable.
     *
     * @return The unlocalized name.
     */
    public abstract String getUnlocalizedName();

    /**
     * Get the full unlocalized name for this configurable.
     *
     * @return The unlocalized name.
     */
    public String getFullUnlocalizedName() {
        return getUnlocalizedName();
    }

    /**
     * Get the instance created by this config.
     */

    public I getInstance() {
        return this.instance;
    }

    /**
     * Will return the unique name of the object this config refers to
     *
     * @return unique name of sub object
     */
    public String getSubUniqueName() {
        return getNamedId();
    }

    /**
     * Overridable method that is immediately called after the element of this config is registered.
     */
    public void onRegistered() {

    }

    /**
     * Overridable method that is immediately called after this element has been registered into a Forge registry.
     */
    public void onForgeRegistered() {

    }

    @Override
    public void onInit(IInitListener.Step step) {

    }

    @Override
    public int compareTo(ExtendedConfig<C, I> o) {
        return getNamedId().compareTo(o.getNamedId());
    }

    /**
     * Checks if the eConfig refers to a target that should be enabled.
     *
     * @return if the target should be enabled.
     */
    public boolean isEnabled() {
        return this.enabled && !isHardDisabled();
    }

    /**
     * Set the enabling of the target.
     *
     * @param enabled If the target should be enabled.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * If the target should be hard-disabled, this means no occurence in the config file,
     * total ignorance.
     *
     * @return if the target should run trough the config handler.
     */
    public boolean isHardDisabled() {
        return false;
    }

    /**
     * Override this method to prevent configs to be disabled from the config file. (non-zero id's that is)
     *
     * @return if the target can be disabled.
     */
    public boolean isDisableable() {
        return true;
    }

    /**
     * Call this method in the initInstance method of Configurables if the instance was already set.
     */
    public void showDoubleInitError() {
        String message = this.getClass() + " caused a double registration of "
            + getInstance()
            + ". This is an error in the mod code.";
        mod.log(Level.FATAL, message);
        throw new OKCoreConfigException(message);
    }

    /**
     * Get the lowest castable config.
     *
     * @return The downcasted config.
     */
    @SuppressWarnings("unchecked")
    public C downCast() {
        return (C) this;
    }

    /**
     * @return The optional registry in which this should be registered.
     */
    @Nullable
    public IForgeRegistry<?> getRegistry() {
        return null;
    }
}
