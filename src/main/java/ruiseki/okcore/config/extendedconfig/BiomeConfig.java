package ruiseki.okcore.config.extendedconfig;

import java.util.function.Function;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

import ruiseki.okcore.config.ConfigurableType;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.init.ModBase;

/**
 * Config for biomes.
 *
 * @author rubensworks
 * @see ExtendedConfig
 */
public abstract class BiomeConfig extends ExtendedConfig<BiomeConfig, BiomeGenBase> {

    private int id;

    /**
     * Make a new instance.
     *
     * @param mod            The mod instance.
     * @param defaultId      The default ID for the configurable.
     * @param namedId        The unique name ID for the configurable.
     * @param comment        The comment to add in the config file for this configurable.
     * @param elementFactory Function factory to create the Biome instance.
     */
    public BiomeConfig(ModBase mod, int defaultId, String namedId, String comment,
        Function<BiomeConfig, BiomeGenBase> elementFactory) {
        super(mod, defaultId > 0, namedId, comment, elementFactory);
        this.id = defaultId;
    }

    /**
     * @return The ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Set the ID.
     *
     * @param id The new ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getUnlocalizedName() {
        return "biomes." + getMod().getModId() + "." + getNamedId();
    }

    @Override
    public ConfigurableType getHolderType() {
        return ConfigurableType.BIOME;
    }

    /**
     * Register the biome instance into the biome dictionary.
     *
     * @see BiomeDictionary
     */
    public void registerBiomeDictionary() {
        getInstance().setBiomeName(LangHelpers.localize(this.getUnlocalizedName()));
        BiomeDictionary.makeBestGuess(getInstance());
    }

    @Override
    public boolean isEnabled() {
        return this.getId() != 0;
    }

}
