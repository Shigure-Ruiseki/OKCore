package ruiseki.okcore.proxy;

import java.io.File;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLCommonHandler;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.client.key.IKeyRegistry;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.event.handler.UpdateNotificationHandler;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.network.PacketHandler;
import ruiseki.okcore.network.packet.PacketSound;
import ruiseki.okcore.world.gen.IRetroGenRegistry;

/**
 * Base proxy for server and client side.
 *
 * @author rubensworks
 *
 */
public abstract class CommonProxyComponent implements ICommonProxy {

    protected static final String DEFAULT_RESOURCELOCATION_MOD = "minecraft";

    @Override
    public void registerRenderer(Class<? extends Entity> clazz, Render renderer) {
        throw new IllegalArgumentException("Registration of renderers should not be called server side!");
    }

    @Override
    public void registerRenderer(Class<? extends TileEntity> clazz, TileEntitySpecialRenderer renderer) {
        throw new IllegalArgumentException("Registration of renderers should not be called server side!");
    }

    @Override
    public void registerRenderers() {
        // Nothing here as the server doesn't render graphics!
    }

    @Override
    public void registerKeyBindings(IKeyRegistry keyRegistry) {

    }

    @Override
    public void registerPacketHandlers(PacketHandler packetHandler) {

    }

    @Override
    public void registerTickHandlers() {

    }

    @Override
    public void registerEventHooks() {
        IRetroGenRegistry retroGenRegistry = getMod().getRegistryManager()
            .getRegistry(IRetroGenRegistry.class);
        if (retroGenRegistry != null) {
            MinecraftForge.EVENT_BUS.register(retroGenRegistry);
        }
        // TODO: add bucketRegistry
        // IBucketRegistry bucketRegistry = getMod().getRegistryManager()
        // .getRegistry(IBucketRegistry.class);
        // if (bucketRegistry != null) {
        // MinecraftForge.EVENT_BUS.register(bucketRegistry);
        // }
        if (!getMod().getReferenceValue(ModBase.REFKEY_VERSION_CHECKER_URL)
            .isEmpty()) {
            UpdateNotificationHandler handler = new UpdateNotificationHandler(getMod());
            FMLCommonHandler.instance()
                .bus()
                .register(handler);
        }
    }

    @Override
    public void playSoundMinecraft(BlockPos pos, String sound, float volume, float frequency) {
        playSoundMinecraft(pos.getX(), pos.getY(), pos.getZ(), sound, volume, frequency);
    }

    @Override
    public void playSoundMinecraft(double x, double y, double z, String sound, float volume, float frequency) {
        playSound(x, y, z, sound, volume, frequency, DEFAULT_RESOURCELOCATION_MOD);
    }

    @Override
    public void playSound(double x, double y, double z, String sound, float volume, float frequency, String mod) {
        // No implementation server-side.
    }

    @Override
    public void playSound(double x, double y, double z, String sound, float volume, float frequency) {
        playSound(x, y, z, sound, volume, frequency, getMod().getModId());
    }

    @Override
    public void sendSoundMinecraft(BlockPos pos, String sound, float volume, float frequency) {
        sendSound(pos.getX(), pos.getY(), pos.getZ(), sound, volume, frequency, DEFAULT_RESOURCELOCATION_MOD);
    }

    @Override
    public void sendSoundMinecraft(double x, double y, double z, String sound, float volume, float frequency) {
        sendSound(x, y, z, sound, volume, frequency, DEFAULT_RESOURCELOCATION_MOD);
    }

    @Override
    public void sendSound(double x, double y, double z, String sound, float volume, float frequency, String mod) {
        PacketSound packet = new PacketSound(x, y, z, sound, volume, frequency, mod);
        if (!MinecraftHelpers.isClientSide()) {
            OKCore.instance.getPacketHandler()
                .sendToAll(packet); // Yes, all sounds go through.
        } else {
            OKCore.instance.getPacketHandler()
                .sendToServer(packet); // Yes, all sounds go through.
        }
    }

    @Override
    public void sendSound(double x, double y, double z, String sound, float volume, float frequency) {
        sendSound(x, y, z, sound, volume, frequency, getMod().getModId());
    }

    @Override
    public void sendSound(EntityPlayer player, SoundEvent.SoundSourceEvent event) {
        float vol = event.sound.getVolume();
        float pitch = event.sound.getPitch();
        sendSound(player.posX, player.posY, player.posZ, event.name, vol, pitch);
    }

    @Override
    public String getEntityTexturePath(Class<? extends Entity> clazz, Entity entity) {
        return null;
    }

    @Override
    public void dumpTexture(File baseDir, String texturePath) {
        // No-op server side
    }
}
