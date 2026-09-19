package ruiseki.okcore.world.biome;

import net.minecraft.world.biome.BiomeGenBase;

import ruiseki.okcore.config.extendedconfig.BiomeConfig;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.helper.LangHelpers;

public class BiomeBase extends BiomeGenBase {

    private final ExtendedConfig<BiomeConfig, BiomeGenBase> eConfig;

    public BiomeBase(ExtendedConfig<BiomeConfig, BiomeGenBase> eConfig) {
        super(((BiomeConfig) eConfig).getId());
        this.eConfig = eConfig;
        this.setBiomeName(LangHelpers.localize(eConfig.getUnlocalizedName()));
    }
}
