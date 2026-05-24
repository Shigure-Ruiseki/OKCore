package ruiseki.okcore.command;

import java.io.File;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.Level;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.event.data.DataEvent;
import ruiseki.okcore.init.ModBase;

/**
 * Command for reload reload data.
 */
public class CommandReload extends CommandMod {

    public static final String NAME = "reload";

    public CommandReload(ModBase mod) {
        super(mod, NAME);
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        return super.addTabCompletionOptions(sender, args);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        MinecraftServer server = MinecraftServer.getServer();
        if (server == null) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Server is not running!"));
            return;
        }

        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Reloading OKCore world data..."));
        try {

            File worldDir = new File(server.getFolderName());
            MinecraftForge.EVENT_BUS.post(new DataEvent.Reload(server, worldDir));

            sender
                .addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "OKCore data reloaded successfully!"));

        } catch (Exception e) {
            sender
                .addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Critical error while reloading data!"));
            OKCore.okLog(Level.ERROR, "Failed to reload data via command", e);
        }
    }
}
