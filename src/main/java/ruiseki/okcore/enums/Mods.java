package ruiseki.okcore.enums;

import java.util.Locale;

import com.gtnewhorizon.gtnhlib.util.data.IMod;

import cpw.mods.fml.common.Loader;

public enum Mods implements IMod {

    ActuallyAdditions("ActuallyAdditions"),
    AppliedEnergistics2("appliedenergistics2"),
    AE2FluidCrafting("ae2fc"),
    Baubles("Baubles"),
    BaublesExpanded("Baubles|Expanded"),
    BlockRenderer6343("blockrenderer6343"),
    BigReactors("BigReactors"),
    BogoSorter("bogosorter"),
    Botania("Botania"),
    BuildCraftEnergy("BuildCraft|Energy"),
    CoFHLib("CoFHLib"),
    CoFHCore("CoFHCore"),
    CraftingTweaks("craftingtweaks"),
    CraftTweaker("MineTweaker3"),
    DraconicEvolution("DraconicEvolution"),
    EtFuturum("etfuturum"),
    EnderIO("EnderIO"),
    IC2("IC2"),
    JAOPCA("jaopca"),
    Mekanism("Mekanism"),
    MinefactoryReloaded("MinefactoryReloaded"),
    NotEnoughItems("NotEnoughItems"),
    Thaumcraft("Thaumcraft"),
    ThaumcraftNEIPlugin("thaumcraftneiplugin"),
    ThaumicEnergistics("thaumicenergistics"),
    TConstruct("TConstruct"),
    ThermalFoundation("ThermalFoundation"),
    Waila("Waila"),;

    public final String modid;
    public final String resourceDomain;
    private Boolean loaded;

    Mods(String modid) {
        this.modid = modid;
        this.resourceDomain = modid != null ? modid.toLowerCase(Locale.ENGLISH) : null;
    }

    @Deprecated
    public boolean isLoaded() {
        return isModLoaded();
    }

    @Override
    public boolean isModLoaded() {
        if (loaded == null) {
            if (modid != null) {
                loaded = Loader.isModLoaded(modid);
            } else loaded = false;
        }
        return loaded;
    }

    @Override
    public String getID() {
        return modid;
    }

    @Override
    public String getResourceLocation() {
        return resourceDomain;
    }
}
