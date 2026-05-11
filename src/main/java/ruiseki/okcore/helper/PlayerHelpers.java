package ruiseki.okcore.helper;

import java.lang.ref.WeakReference;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.FMLCommonHandler;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.Reference;

public class PlayerHelpers {

    public static boolean doesPlayerExist(World world, UUID player) {
        if (world != null && player != null) {
            if (world.playerEntities == null) {
                return false;
            } else {
                for (EntityPlayer p : world.playerEntities) {
                    if (p != null && player.equals(
                        p.getGameProfile()
                            .getId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static EntityPlayer getPlayerFromWorld(World world, UUID player) {
        if (world != null && player != null) {
            for (EntityPlayer p : world.playerEntities) {
                if (p != null && player.equals(
                    p.getGameProfile()
                        .getId())) {
                    return p;
                }
            }
        }
        return null;
    }

    public static boolean doesPlayerExistClient(World world, UUID player) {
        if (player == null) {
            return false;
        } else {
            for (EntityPlayer entityPlayer : world.playerEntities) {
                if (entityPlayer.getUniqueID()
                    .compareTo(player) == 0) {
                    return true;
                }
            }

            return false;
        }
    }

    public static EntityPlayer getPlayerFromWorldClient(World world, UUID player) {
        if (player == null) {
            return null;
        } else {
            for (EntityPlayer entityPlayer : world.playerEntities) {
                if (entityPlayer.getUniqueID()
                    .compareTo(player) == 0) {
                    return entityPlayer;
                }
            }

            return null;
        }
    }

    public static NBTTagCompound proifleToNBT(GameProfile profile) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("Name", profile.getName());
        UUID id = profile.getId();
        if (id != null) {
            tag.setLong("UUIDL", id.getLeastSignificantBits());
            tag.setLong("UUIDU", id.getMostSignificantBits());
        }

        return tag;
    }

    public static void writeProfileToNBT(GameProfile profile, NBTTagCompound tag) {
        tag.setString("Name", profile.getName());
        UUID id = profile.getId();
        if (id != null) {
            tag.setLong("UUIDL", id.getLeastSignificantBits());
            tag.setLong("UUIDU", id.getMostSignificantBits());
        }

    }

    public static GameProfile profileFromNBT(NBTTagCompound tag) {
        String name = tag.getString("Name");
        UUID uuid = null;
        if (tag.hasKey("UUIDL")) {
            uuid = new UUID(tag.getLong("UUIDU"), tag.getLong("UUIDL"));
        } else if (StringUtils.isBlank(name)) {
            return null;
        }

        return new GameProfile(uuid, name);
    }

    public static WeakReference<FakePlayer> initFakePlayer(WorldServer ws, UUID uname, String blockName) {
        GameProfile breakerProfile = new GameProfile(uname, Reference.MOD_ID + ".fake_player." + blockName);
        WeakReference<FakePlayer> fakePlayer;
        try {
            fakePlayer = new WeakReference<>(FakePlayerFactory.get(ws, breakerProfile));
        } catch (Exception e) {
            OKCore.okLog(Level.ERROR, "Exception thrown trying to create fake player : ", e);
            return null;
        }

        if (fakePlayer.get() == null) return null;

        FakePlayer player = fakePlayer.get();
        if (player == null) return null;

        player.onGround = true;

        try {
            player.playerNetServerHandler = new NetHandlerPlayServer(
                FMLCommonHandler.instance()
                    .getMinecraftServerInstance(),
                new NetworkManager(false),
                player) {

                @Override
                public void sendPacket(Packet packetIn) {}
            };
        } catch (Exception ignore) {}

        return fakePlayer;
    }
}
