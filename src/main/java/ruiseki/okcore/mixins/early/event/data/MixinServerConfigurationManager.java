package ruiseki.okcore.mixins.early.event.data;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraftforge.common.MinecraftForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.event.data.OnDatapackSyncEvent;
import ruiseki.okcore.network.packet.PacketUpdateRecipes;
import ruiseki.okcore.network.packet.PacketUpdateTags;
import ruiseki.okcore.recipe.RecipeManager;
import ruiseki.okcore.tag.TagManager;

@Mixin(ServerConfigurationManager.class)
public class MixinServerConfigurationManager {

    @Inject(
        method = "initializeConnectionToPlayer(Lnet/minecraft/network/NetworkManager;Lnet/minecraft/entity/player/EntityPlayerMP;Lnet/minecraft/network/NetHandlerPlayServer;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/NetHandlerPlayServer;sendPacket(Lnet/minecraft/network/Packet;)V",
            shift = At.Shift.AFTER,
            ordinal = 4))
    private void onInitializeConnectionPostHeldItem(NetworkManager netManager, EntityPlayerMP player,
        NetHandlerPlayServer nethandlerplayserver, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new OnDatapackSyncEvent((ServerConfigurationManager) (Object) this, player));
        OKCore.instance.getPacketHandler()
            .sendToPlayer(
                new PacketUpdateRecipes(
                    RecipeManager.getManager()
                        .getRecipes()),
                player);
        OKCore.instance.getPacketHandler()
            .sendToPlayer(
                new PacketUpdateTags(
                    TagManager.getManager()
                        .getTags()),
                player);
    }
}
