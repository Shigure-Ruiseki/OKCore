package ruiseki.okcore.command;

import java.io.File;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import ruiseki.okcore.data.DatapackLoader;
import ruiseki.okcore.data.DatapackManager;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.init.ModBase;

public class CommandDatapack extends CommandMod {

    private final MinecraftServer server;
    private final File datapackDir;

    public CommandDatapack(ModBase mod, MinecraftServer server) {
        super(mod, "datapack");
        this.server = server;
        this.datapackDir = Helpers.getServerFolder(server);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public LiteralArgumentBuilder<ICommandSender> make() {
        return LiteralArgumentBuilder.<ICommandSender>literal(getCommandName())
            .requires(sender -> sender.canCommandSenderUseCommand(getRequiredPermissionLevel(), getMod().getModId()))
            .executes(this)
            // /okcore datapack reload
            .then(
                LiteralArgumentBuilder.<ICommandSender>literal("reload")
                    .executes(this::reloadDatapacks))
            // /okcore datapack list
            .then(
                LiteralArgumentBuilder.<ICommandSender>literal("list")
                    .executes(ctx -> listDatapacks(ctx, "all"))
                    .then(
                        LiteralArgumentBuilder.<ICommandSender>literal("enabled")
                            .executes(ctx -> listDatapacks(ctx, "enabled")))
                    .then(
                        LiteralArgumentBuilder.<ICommandSender>literal("available")
                            .executes(ctx -> listDatapacks(ctx, "available"))))
            // /okcore datapack enable <name>
            .then(
                LiteralArgumentBuilder.<ICommandSender>literal("enable")
                    .then(
                        RequiredArgumentBuilder.<ICommandSender, String>argument("name", StringArgumentType.string())
                            .suggests(this::suggestDisabledPacks)
                            .executes(this::enablePack)))
            // /okcore datapack disable <name>
            .then(
                LiteralArgumentBuilder.<ICommandSender>literal("disable")
                    .then(
                        RequiredArgumentBuilder.<ICommandSender, String>argument("name", StringArgumentType.string())
                            .suggests(this::suggestEnabledPacks)
                            .executes(this::disablePack)));
    }

    private int reloadDatapacks(CommandContext<ICommandSender> context) {
        ICommandSender sender = context.getSource();

        printLineToChat(sender, EnumChatFormatting.YELLOW + "Reloading datapacks, please wait...");

        try {
            long startTime = System.currentTimeMillis();

            DatapackLoader.loadAllDataAtServerStart(this.server);

            long endTime = System.currentTimeMillis() - startTime;
            printLineToChat(
                sender,
                EnumChatFormatting.GREEN + "Successfully reloaded all datapacks in " + endTime + " ms!");
        } catch (Exception e) {
            printErrorToChat(sender, "Critical error occurred during data reload! Check server logs.");
        }

        return Command.SINGLE_SUCCESS;
    }

    private int listDatapacks(CommandContext<ICommandSender> context, String filter) {
        ICommandSender sender = context.getSource();
        Map<String, DatapackManager.Datapack> registry = DatapackManager.INSTANCE.getDatapackRegistry();

        if (registry.isEmpty()) {
            printLineToChat(sender, EnumChatFormatting.GRAY + "No datapacks registered.");
            return Command.SINGLE_SUCCESS;
        }

        printLineToChat(sender, EnumChatFormatting.YELLOW + "Datapack List (" + filter + ")");

        for (DatapackManager.Datapack pack : registry.values()) {
            boolean isEnabled = pack.isEnabled();

            if (filter.equals("enabled") && !isEnabled) continue;
            if (filter.equals("available") && isEnabled) continue;

            String status = isEnabled ? EnumChatFormatting.GREEN + "[Enabled]" : EnumChatFormatting.RED + "[Disabled]";
            String type = pack.isModJar() ? EnumChatFormatting.AQUA + " (Mod Jar)"
                : EnumChatFormatting.LIGHT_PURPLE + " (Folder/Zip)";

            printLineToChat(sender, " - " + EnumChatFormatting.GOLD + pack.getName() + " " + status + type);
        }

        return Command.SINGLE_SUCCESS;
    }

    private int enablePack(CommandContext<ICommandSender> context) {
        ICommandSender sender = context.getSource();
        String packName = StringArgumentType.getString(context, "name");

        boolean success = DatapackManager.INSTANCE.enableDatapack(packName);
        if (success) {
            saveConfig(sender);
            printLineToChat(
                sender,
                EnumChatFormatting.GREEN + "Datapack '" + packName + "' has been enabled! Reload may be required.");
        } else {
            printErrorToChat(sender, "Failed to enable datapack. It might already be enabled or does not exist.");
        }
        return Command.SINGLE_SUCCESS;
    }

    private int disablePack(CommandContext<ICommandSender> context) {
        ICommandSender sender = context.getSource();
        String packName = StringArgumentType.getString(context, "name");

        DatapackManager.Datapack pack = DatapackManager.INSTANCE.getDatapackRegistry()
            .get(packName.toLowerCase());
        if (pack != null && pack.isModJar()) {
            printErrorToChat(sender, "Cannot disable mod jar datapacks!");
            return Command.SINGLE_SUCCESS;
        }

        boolean success = DatapackManager.INSTANCE.disableDatapack(packName);
        if (success) {
            saveConfig(sender);
            printLineToChat(
                sender,
                EnumChatFormatting.GREEN + "Datapack '" + packName + "' has been disabled! Reload may be required.");
        } else {
            printErrorToChat(sender, "Failed to disable datapack. It might already be disabled or does not exist.");
        }
        return Command.SINGLE_SUCCESS;
    }

    private void saveConfig(ICommandSender sender) {
        if (sender.getEntityWorld() != null) {
            DatapackManager.INSTANCE.saveDisabledPacksConfig(this.datapackDir);
        }
    }

    private CompletableFuture<Suggestions> suggestEnabledPacks(CommandContext<ICommandSender> context,
        SuggestionsBuilder builder) {
        DatapackManager.INSTANCE.getDatapackRegistry()
            .values()
            .stream()
            .filter(pack -> pack.isEnabled() && !pack.isModJar())
            .forEach(pack -> builder.suggest(pack.getName()));
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestDisabledPacks(CommandContext<ICommandSender> context,
        SuggestionsBuilder builder) {
        DatapackManager.INSTANCE.getDatapackRegistry()
            .values()
            .stream()
            .filter(pack -> !pack.isEnabled())
            .forEach(pack -> builder.suggest(pack.getName()));
        return builder.buildFuture();
    }
}
