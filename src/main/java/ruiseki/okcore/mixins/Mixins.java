package ruiseki.okcore.mixins;

import javax.annotation.Nonnull;

import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;

public enum Mixins implements IMixins {

    ITEM_CAPABILITIES(new MixinBuilder("Add capabilities to Item").addCommonMixins("MixinItemStackCap")
        .setPhase(Phase.EARLY)),

    ENTITY_CAPABILITIES(new MixinBuilder("Add capabilities to Entity").addCommonMixins("MixinEntityCap")
        .setPhase(Phase.EARLY)),

    ;

    private final MixinBuilder builder;

    Mixins(MixinBuilder builder) {
        this.builder = builder;
    }

    @Nonnull
    @Override
    public MixinBuilder getBuilder() {
        return this.builder;
    }
}
