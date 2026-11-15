package ruiseki.okcore.mixins.early.entity.permission;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.helper.PlayerHelpers;
import ruiseki.okcore.network.packet.PacketPermissionLevel;

@Mixin(ServerConfigurationManager.class)
public class MixinServerConfigurationManager {

    @Shadow
    @Final
    private MinecraftServer mcServer;

    @Inject(
        method = "initializeConnectionToPlayer(Lnet/minecraft/network/NetworkManager;Lnet/minecraft/entity/player/EntityPlayerMP;Lnet/minecraft/network/NetHandlerPlayServer;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/NetHandlerPlayServer;sendPacket(Lnet/minecraft/network/Packet;)V",
            shift = At.Shift.AFTER,
            ordinal = 4))
    private void okcore$onInitializeConnectionPostHeldItem(NetworkManager netManager, EntityPlayerMP player,
        NetHandlerPlayServer nethandlerplayserver, CallbackInfo ci) {
        this.okcore$sendPlayerPermissionLevel(player);
    }

    @Inject(
        method = "respawnPlayer",
        at = @At(value = "NEW", target = "net/minecraft/network/play/server/S1FPacketSetExperience"))
    private void okcore$afterSetExperience(EntityPlayerMP player, int dimension, boolean conqueredEnd,
        CallbackInfoReturnable<EntityPlayerMP> cir, @Local(name = "entityplayermp1") EntityPlayerMP respawnedPlayer) {
        this.okcore$sendPlayerPermissionLevel(respawnedPlayer);
    }

    @Unique
    private void okcore$sendPlayerPermissionLevel(EntityPlayerMP player) {
        GameProfile gameprofile = player.getGameProfile();
        int i = PlayerHelpers.getPermissionLevel(gameprofile);
        this.okcore$sendPlayerPermissionLevel(player, i);
    }

    @Unique
    private void okcore$sendPlayerPermissionLevel(EntityPlayerMP player, int level) {
        OKCore._instance.getPacketHandler()
            .sendToPlayer(new PacketPermissionLevel(level), player);
    }

    // OP
    @Inject(method = "func_152605_a(Lcom/mojang/authlib/GameProfile;)V", at = @At("RETURN"))
    private void okcore$onAddOp(GameProfile profile, CallbackInfo ci) {
        this.okcore$updatePlayerPermissionLevel(profile);
    }

    // DEOP
    @Inject(method = "func_152610_b(Lcom/mojang/authlib/GameProfile;)V", at = @At("RETURN"))
    private void okcore$onRemoveOp(GameProfile profile, CallbackInfo ci) {
        this.okcore$updatePlayerPermissionLevel(profile);
    }

    @Unique
    private void okcore$updatePlayerPermissionLevel(GameProfile profile) {
        EntityPlayerMP playerMP = PlayerHelpers.getPlayerByUUID(this.mcServer, profile.getId());
        if (playerMP != null) this.okcore$sendPlayerPermissionLevel(playerMP);
    }
}
