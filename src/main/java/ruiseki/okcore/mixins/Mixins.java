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

    COOLDOWN(new MixinBuilder("Add cooldown to EntityPlayer").addCommonMixins("cooldown.MixinEntityPlayer")
        .addClientMixins("cooldown.MixinRenderItem")
        .setPhase(Phase.EARLY)),

    ENTITY_CAPABILITIES(new MixinBuilder("Add capabilities to Entity").addCommonMixins("capabilities.MixinEntityCap")
        .setPhase(Phase.EARLY)),

    TILE_ENTITY_CAPABILITIES(
        new MixinBuilder("Add capabilities to Tile Entity").addCommonMixins("capabilities.MixinTileEntity")
            .addCommonMixins("capabilities.MixinTileEntityChest")
            .setPhase(Phase.EARLY)),

    GUI_INPUT_EVENT(new MixinBuilder("Add GUI input event").addClientMixins("event.gui.MixinGuiScreen")
        .setPhase(Phase.EARLY)),

    CHUNK_CAPABILITIES(new MixinBuilder("Add capabilities to Chunk").addCommonMixins("capabilities.MixinChunk")
        .addCommonMixins("capabilities.MixinAnvilChunkLoader")
        .setPhase(Phase.EARLY)),;

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
