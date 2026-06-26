package ruiseki.okcore.event.handler;

import java.util.Map;

import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import ruiseki.okcore.helper.VersionHelpers;
import ruiseki.okcore.init.ModBase;

/**
 * Handles update notification display when a player logs into the server.
 * This handler is instantiated per-mod to support dynamic multi-mod configurations.
 */
public class UpdateNotificationHandler {

    private final ModBase mod;
    private boolean notified = false;

    /**
     * Constructs a notification handler tied to a specific mod instance.
     * 
     * @param mod The specific mod instance to monitor for updates.
     */
    public UpdateNotificationHandler(ModBase mod) {
        this.mod = mod;
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (mod == null || notified) {
            return;
        }

        // 1. Verify the update status from the centralized ModBase references
        String status = mod.getReferenceValue(ModBase.REFKEY_VERSION_CHECKER_STATUS);
        if (!VersionHelpers.STATUS_OUTDATED.equals(status)) {
            return;
        }

        String currentVersion = mod.getReferenceValue(ModBase.REFKEY_MOD_VERSION);
        String latestVersion = mod.getReferenceValue(ModBase.REFKEY_VERSION_CHECKER_LATEST);

        // Mark as notified to prevent spamming on dimension changes or re-logging
        notified = true;

        // 2. Build the Mod Prefix dynamically: [Mod_Name]
        IChatComponent prefix = new ChatComponentText("[" + mod.getModName() + "]");
        prefix.getChatStyle()
            .setColor(EnumChatFormatting.AQUA)
            .setBold(true);

        // 3. Build the primary notification message (Sửa để truyền đủ 3 tham số: Tên Mod, Phiên bản cũ, Phiên bản mới)
        IChatComponent mainMessage = new ChatComponentTranslation(
            "okcore.update.available",
            mod.getModName(),
            currentVersion,
            latestVersion);
        mainMessage.getChatStyle()
            .setColor(EnumChatFormatting.WHITE)
            .setBold(false);

        // Initialize the consolidated chat component hierarchy
        IChatComponent fullMessage = prefix.appendSibling(new ChatComponentText(" "))
            .appendSibling(mainMessage);

        // 4. Retrieve the download map and handle each link dynamically
        @SuppressWarnings("unchecked")
        Map<String, String> downloads = (Map<String, String>) mod
            .getReferenceValue(ModBase.REFKEY_VERSION_CHECKER_DOWNLOADS);

        if (downloads != null && !downloads.isEmpty()) {
            // Iterate through each entry to append platform-specific clickable buttons
            for (Map.Entry<String, String> entry : downloads.entrySet()) {
                String platformKey = entry.getKey(); // e.g., "github", "curseforge"
                String downloadUrl = entry.getValue(); // e.g., "https://..."

                if (downloadUrl == null || downloadUrl.trim()
                    .isEmpty()) {
                    continue;
                }

                // Append a separator space between buttons
                fullMessage.appendSibling(new ChatComponentText(" "));

                String platformName = platformKey.substring(0, 1)
                    .toUpperCase()
                    + platformKey.substring(1)
                        .toLowerCase();

                IChatComponent linkButton = new ChatComponentTranslation("okcore.update.link", platformName);

                // Apply visual formatting to the actionable link button
                linkButton.getChatStyle()
                    .setColor(EnumChatFormatting.GOLD)
                    .setUnderlined(true);

                // Bind the click action to execute an external browser URL navigation
                linkButton.getChatStyle()
                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, downloadUrl));

                // Bind the hover tooltips dynamically passing the platform name as a parameter
                linkButton.getChatStyle()
                    .setChatHoverEvent(
                        new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            new ChatComponentTranslation("okcore.update.hover", platformName)));

                // Append the processed button to the core chat component
                fullMessage.appendSibling(linkButton);
            }
        }

        // 5. Dispatch the finalized interactive component to the client chat feed
        event.player.addChatMessage(fullMessage);
    }
}
