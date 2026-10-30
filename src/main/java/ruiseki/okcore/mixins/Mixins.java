package ruiseki.okcore.mixins;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;

public enum Mixins implements IMixins {

    // EARLY
    // CAPABILITIES
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

    // GUI
    SLOT_BACKGROUND(new MixinBuilder("Add Slot BackGround").addClientMixins("client.gui.MixinGuiContainer")
        .setPhase(Phase.EARLY)),

    // COOLDOWN
    COOLDOWN(new MixinBuilder("Add cooldown to EntityPlayer").addCommonMixins("cooldown.MixinEntityPlayer")
        .addClientMixins("cooldown.MixinRenderItem")
        .setPhase(Phase.EARLY)),

    // EVENT
    DATA_EVENT(new MixinBuilder("Hook Data Event").addCommonMixins("event.data.MixinServerConfigurationManager")
        .setPhase(Phase.EARLY)),
    GENERIC_EVENT(new MixinBuilder("Hook Generic Event").addCommonMixins("event.generic.MixinASMEventHandler")
        .setPhase(Phase.EARLY)),
    GUI_INPUT_EVENT(new MixinBuilder("Add GUI input event").addCommonMixins("event.gui.MixinNetHandlerPlayServer")
        .addClientMixins(
            "event.gui.MixinGuiScreen",
            "event.gui.MixinGuiContainer",
            "event.gui.MixinInventoryEffectRenderer",
            "event.gui.MixinPlayerControllerMP")
        .setPhase(Phase.EARLY)),
    INTERACTION_EVENT(new MixinBuilder("Hook Interaction Event")
        .addClientMixins("event.interaction.MixinMinecraft", "event.interaction.MixinPlayerControllerMP")
        .setPhase(Phase.EARLY)),

    // GTNHLIB
    GTNHLIB(new MixinBuilder("GTNHLib Mixin").addClientMixins("gtnhlib.JSONModelAccessor")
        .setPhase(Phase.EARLY)),

    // SHARED_NBT
    ITEM_SHARED_NBT(new MixinBuilder("Add shared nbt to Item")
        .addCommonMixins(
            "itemSharedNBT.MixinC0EPacketClickWindow",
            "itemSharedNBT.MixinC10PacketCreativeInventoryAction",
            "itemSharedNBT.MixinNetHandlerPlayServer",
            "itemSharedNBT.MixinPacketBuffer")
        .setPhase(Phase.EARLY)),

    // GTNHLIB
    MUI2(new MixinBuilder("MUI2 Mixin").addClientMixins("mui.MixinItemSlot")
        .addRequiredMod(TargetMods.MUI2)
        .setPhase(Phase.EARLY)),

    // NETWORK
    NETWORK(new MixinBuilder("Allow registering more than one handler of the same type")
        .addCommonMixins("network.MixinSimpleNetworkWrapper")
        .addExcludedMod(TargetMods.MUI2)
        .setPhase(Phase.EARLY)),

    // Tag
    TAG(new MixinBuilder("Add tweaks for Tag system").addCommonMixins("tag.MixinItem", "tag.MixinBlock")
        .setPhase(Phase.EARLY)),

    // LATE
    // RECIPE
    NEI_JSON_RECIPE(new MixinBuilder("Hook Json Recipe to NEI")
        .addCommonMixins("recipe.MixinShapedRecipeHandler", "recipe.MixinShapelessRecipeHandler")
        .addRequiredMod(TargetMods.NotEnoughItems)
        .setPhase(Phase.LATE)),;

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
