package ruiseki.okcore.mixins;

import javax.annotation.Nonnull;

import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;

public enum Mixins implements IMixins {

    ITEM_CAPABILITIES(new MixinBuilder("Add capabilities to Item").addCommonMixins("capabilities.MixinItemStackCap")
        .setPhase(Phase.EARLY)),

    ITEM_SHARED_NBT(
        new MixinBuilder("Add shared nbt to Item").addCommonMixins("itemSharedNBT.MixinC0EPacketClickWindowNBT")
            .addCommonMixins("itemSharedNBT.MixinC10PacketCreativeInventoryActionNBT")
            .addCommonMixins("itemSharedNBT.MixinNetHandlerPlayServerNBT")
            .addCommonMixins("itemSharedNBT.MixinPacketBufferNBT")
            .setPhase(Phase.EARLY)),

    ENTITY_CAPABILITIES(new MixinBuilder("Add capabilities to Entity").addCommonMixins("capabilities.MixinEntityCap")
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
