package ruiseki.okcore.tracking;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.ForgeVersion;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.Level;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.versioning.ComparableVersion;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.Reference;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.modcompat.versionchecker.VersionCheckerModCompat;

/**
 * Version checking service for JSON response.
 * 
 * @author rubensworks
 */
public class Versions {

    private static final List<Triple<ModBase, IModVersion, String>> versionMods = Lists.newLinkedList();

    private static volatile boolean checked = false;
    private static volatile boolean allDone = false;
    private static volatile boolean displayed = false;

    public static synchronized void registerMod(ModBase mod, IModVersion modVersion, String versionUrl) {
        versionMods.add(Triple.of(mod, modVersion, versionUrl));
    }

    protected static synchronized List<Triple<ModBase, IModVersion, String>> getVersionMods() {
        return Lists.newArrayList(versionMods);
    }

    /**
     * Check the versions for all registered mods using JSON format.
     */
    public static void checkAll() {
        if (!checked) {
            checked = true;
            new Thread(new Runnable() {

                @Override
                public void run() {
                    List<Triple<ModBase, IModVersion, String>> versionMods = getVersionMods();
                    for (Triple<ModBase, IModVersion, String> triple : versionMods) {
                        try {
                            URL url = new URL(triple.getRight());
                            String data = IOUtils.toString(url, StandardCharsets.UTF_8);

                            JsonElement rootElement = new JsonParser().parse(data);
                            if (!rootElement.isJsonObject()) {
                                triple.getLeft()
                                    .log(Level.WARN, "Retrieved invalid JSON version data.");
                                continue;
                            }

                            JsonObject json = rootElement.getAsJsonObject();

                            String version = json.has("version") ? json.get("version")
                                .getAsString() : null;

                            String updateUrl = null;
                            if (json.has("download") && json.get("download")
                                .isJsonObject()) {
                                JsonObject downloadObj = json.getAsJsonObject("download");
                                if (downloadObj.has("github")) {
                                    updateUrl = downloadObj.get("github")
                                        .getAsString();
                                } else if (downloadObj.entrySet()
                                    .iterator()
                                    .hasNext()) {
                                        updateUrl = downloadObj.entrySet()
                                            .iterator()
                                            .next()
                                            .getValue()
                                            .getAsString();
                                    }
                            }

                            String info;
                            if (json.has("info")) {
                                info = json.get("info")
                                    .getAsString();
                            } else if (json.has("changelog")) {
                                info = json.get("changelog")
                                    .getAsString();
                            } else {
                                String mcVer = json.has("minecraft") ? json.get("minecraft")
                                    .getAsString() : Reference.MOD_MC_VERSION;
                                info = "New update " + version + " available for Minecraft " + mcVer + "!";
                            }

                            if (version == null || updateUrl == null) {
                                triple.getLeft()
                                    .log(Level.WARN, "Missing version or download link in JSON.");
                                setVersionInfo(triple.getLeft(), triple.getMiddle(), null, null, null);
                            } else {
                                setVersionInfo(triple.getLeft(), triple.getMiddle(), version, info, updateUrl);
                                if (triple.getMiddle()
                                    .needsUpdate()) {
                                    VersionCheckerModCompat
                                        .sendIMCOutdatedMessage(triple.getLeft(), triple.getMiddle());
                                    triple.getLeft()
                                        .log(
                                            Level.INFO,
                                            String.format(
                                                "%s is outdated, version %s can be found at %s.",
                                                triple.getLeft()
                                                    .getModName(),
                                                version,
                                                updateUrl));
                                } else {
                                    triple.getLeft()
                                        .log(
                                            Level.INFO,
                                            String.format(
                                                "%s is up-to-date!",
                                                triple.getLeft()
                                                    .getModName()));
                                }
                            }

                        } catch (Exception e) {
                            triple.getLeft()
                                .log(Level.WARN, "Could not get version info: " + e.toString());
                            setVersionInfo(triple.getLeft(), triple.getMiddle(), null, null, null);
                        }
                    }
                    allDone = true;
                }
            }).start();
        }
    }

    public static void setVersionInfo(ModBase mod, IModVersion modVersion, String version, String info,
        String updateUrl) {
        modVersion.setVersionInfo(version, info, updateUrl);
        if (version != null && info != null && updateUrl != null) {
            setForgeVersionInfo(mod, modVersion, version, info, updateUrl);
        }
    }

    public static void setForgeVersionInfo(ModBase mod, IModVersion modVersion, String version, String info,
        String updateUrl) {
        try {
            Field resultsField = ForgeVersion.class.getDeclaredField("status");
            resultsField.setAccessible(true);
            Map<ModContainer, ForgeVersion.Status> results = (Map<ModContainer, ForgeVersion.Status>) resultsField
                .get(null);

            Constructor<ForgeVersion.Status> constructor = ForgeVersion.Status.class
                .getDeclaredConstructor(ForgeVersion.Status.class, ComparableVersion.class, Map.class, String.class);
            constructor.setAccessible(true);
            ForgeVersion.Status status = modVersion.needsUpdate() ? ForgeVersion.Status.OUTDATED
                : ForgeVersion.Status.UP_TO_DATE;
            ComparableVersion comparableVersion = new ComparableVersion(version);
            ForgeVersion.Status checkResult = constructor
                .newInstance(status, comparableVersion, ImmutableMap.of(comparableVersion, info), updateUrl);
            ModContainer modContainer = Loader.instance()
                .getIndexedModList()
                .get(mod.getModId());
            results.put(modContainer, checkResult);
        } catch (NoSuchFieldException | InvocationTargetException | NoSuchMethodException | IllegalAccessException
            | InstantiationException e) {
            mod.log(
                Level.ERROR,
                String.format("Failed to set Forge version information for %s-%s.", mod.getModName(), version));
            e.printStackTrace();
        }
    }

    /**
     * When a player tick event is received.
     * 
     * @param event The received event.
     */
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public synchronized void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && allDone && !displayed) {
            List<Triple<ModBase, IModVersion, String>> versionMods = getVersionMods();
            for (Triple<ModBase, IModVersion, String> triple : versionMods) {
                if (triple.getMiddle()
                    .needsUpdate()) {
                    // Chat formatting inspired by CoFH
                    EntityPlayer player = event.player;
                    IChatComponent chat = new ChatComponentText("");

                    ChatStyle modNameStyle = new ChatStyle();
                    modNameStyle.setColor(EnumChatFormatting.AQUA);

                    ChatStyle versionStyle = new ChatStyle();
                    versionStyle.setColor(EnumChatFormatting.AQUA);

                    ChatStyle downloadStyle = new ChatStyle();
                    downloadStyle.setColor(EnumChatFormatting.BLUE);

                    String currentVersion = Reference.MOD_MC_VERSION + "-"
                        + triple.getLeft()
                            .getReferenceValue(ModBase.REFKEY_MOD_VERSION);
                    String newVersion = Reference.MOD_MC_VERSION + "-"
                        + triple.getMiddle()
                            .getVersion();
                    IChatComponent versionTransition = new ChatComponentText(
                        String.format("%s -> %s", currentVersion, newVersion)).setChatStyle(versionStyle);
                    modNameStyle.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, versionTransition));
                    IChatComponent modNameComponent = new ChatComponentText(
                        String.format(
                            "[%s]",
                            triple.getLeft()
                                .getModName())).setChatStyle(modNameStyle);

                    IChatComponent downloadComponent = new ChatComponentText(
                        String.format("[%s]", LangHelpers.localize("general.okcore.version.download")))
                            .setChatStyle(downloadStyle);
                    downloadStyle.setChatHoverEvent(
                        new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            new ChatComponentTranslation("general.okcore.version.clickToDownload")));
                    downloadStyle.setChatClickEvent(
                        new ClickEvent(
                            ClickEvent.Action.OPEN_URL,
                            triple.getMiddle()
                                .getUpdateUrl()));

                    chat.appendSibling(modNameComponent);
                    chat.appendText(" ");
                    chat.appendSibling(
                        new ChatComponentTranslation("general.okcore.version.updateAvailable")
                            .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.WHITE)));
                    chat.appendText(
                        String.format(
                            ": %s ",
                            triple.getMiddle()
                                .getVersion()));
                    chat.appendSibling(downloadComponent);

                    player.addChatComponentMessage(chat);

                    chat = new ChatComponentText("");
                    chat.appendSibling(modNameComponent);
                    chat.appendText(EnumChatFormatting.WHITE + " ");
                    chat.appendText(
                        triple.getMiddle()
                            .getInfo());
                    player.addChatComponentMessage(chat);
                }
            }
            displayed = true;
            try {
                FMLCommonHandler.instance()
                    .bus()
                    .unregister(this);
            } catch (Exception e) {}
        }
    }

}
