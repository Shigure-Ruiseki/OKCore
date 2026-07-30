package ruiseki.okcore.mixins;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;

public enum Mixins implements IMixins {

    GENERIC_EVENT(new MixinBuilder("Hook Generic Event").addCommonMixins("event.generic.MixinASMEventHandler")
        .setPhase(Phase.EARLY)),

    DATA_EVENT(new MixinBuilder("Hook Data Event").addCommonMixins("event.data.MixinServerConfigurationManager")
        .setPhase(Phase.EARLY)),

    GUI_INPUT_EVENT(new MixinBuilder("Add GUI input event")
        .addClientMixins(
            "event.gui.MixinGuiScreen",
            "event.gui.MixinGuiContainer",
            "event.gui.MixinInventoryEffectRenderer")
        .setPhase(Phase.EARLY)),

    SLOT_BACKGROUND(new MixinBuilder("Add Slot BackGround").addClientMixins("client.gui.MixinGuiContainer")
        .setPhase(Phase.EARLY)),

    ITEM_SHARED_NBT(
        new MixinBuilder("Add shared nbt to Item").addCommonMixins("itemSharedNBT.MixinC0EPacketClickWindowNBT")
            .addCommonMixins(
                "itemSharedNBT.MixinC10PacketCreativeInventoryActionNBT",
                "itemSharedNBT.MixinNetHandlerPlayServerNBT",
                "itemSharedNBT.MixinPacketBufferNBT")
            .setPhase(Phase.EARLY)),

    COOLDOWN(new MixinBuilder("Add cooldown to EntityPlayer").addCommonMixins("cooldown.MixinEntityPlayer")
        .addClientMixins("cooldown.MixinRenderItem")
        .setPhase(Phase.EARLY)),

    ITEM_CAPABILITIES(new MixinBuilder("Add capabilities to Item").addCommonMixins("capabilities.MixinItemStackCap")
        .setPhase(Phase.EARLY)),

    ENTITY_CAPABILITIES(new MixinBuilder("Add capabilities to Entity").addCommonMixins("capabilities.MixinEntityCap")
        .setPhase(Phase.EARLY)),

    TILE_ENTITY_CAPABILITIES(new MixinBuilder("Add capabilities to Tile Entity")
        .addCommonMixins("capabilities.MixinTileEntity", "capabilities.MixinTileEntityChest")
        .setPhase(Phase.EARLY)),

    CHUNK_CAPABILITIES(new MixinBuilder("Add capabilities to Chunk")
        .addCommonMixins("capabilities.MixinChunk", "capabilities.MixinAnvilChunkLoader")
        .setPhase(Phase.EARLY)),

    NEI_JSON_RECIPE(new MixinBuilder("Hook Json Recipe to NEI")
        .addCommonMixins("recipe.MixinShapedRecipeHandler", "recipe.MixinShapelessRecipeHandler")
        .addRequiredMod(TargetMods.NotEnoughItems)
        .setPhase(Phase.LATE)),

    GTNHLIB(new MixinBuilder("GTNHLib Mixin").addClientMixins("gtnhlib.JSONModelAccessor")
        .setPhase(Phase.EARLY));

    private final MixinBuilder builder;

    Mixins(MixinBuilder builder) {
        this.builder = builder;
    }

    @NotNull
    @Override
    public MixinBuilder getBuilder() {
        return this.builder;
    }
}
