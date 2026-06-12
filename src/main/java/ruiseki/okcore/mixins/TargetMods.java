package ruiseki.okcore.mixins;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhmixins.builders.ITargetMod;
import com.gtnewhorizon.gtnhmixins.builders.TargetModBuilder;

import ruiseki.okcore.lib.LibMods;

public enum TargetMods implements ITargetMod {

    NotEnoughItems(LibMods.NotEnoughItems);

    private final TargetModBuilder builder;

    TargetMods(@NotNull LibMods libMod) {
        this.builder = new TargetModBuilder().setModId(libMod.modid);
    }

    TargetMods(String coreModClass, @NotNull LibMods libMod) {
        this.builder = new TargetModBuilder().setCoreModClass(coreModClass)
            .setModId(libMod.modid);
    }

    TargetMods(String coreModClass) {
        this.builder = new TargetModBuilder().setCoreModClass(coreModClass);
    }

    TargetMods(String coreModClass, String modId) {
        this.builder = new TargetModBuilder().setCoreModClass(coreModClass)
            .setModId(modId);
    }

    @NotNull
    @Override
    public TargetModBuilder getBuilder() {
        return builder;
    }
}
