package ruiseki.okcore.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.init.ModBase;

/**
 * The mod base command for Brigadier integration.
 * Designed to be extended by other sub-modules or mods.
 *
 * @author ruiseki
 *
 */
public class CommandMod implements Command<ICommandSender> {

    private final ModBase mod;
    private final String commandName;

    public CommandMod(ModBase mod) {
        this.mod = mod;
        this.commandName = mod.getModId();
    }

    public CommandMod(CommandMod parent) {
        this(parent.getMod());
    }

    public CommandMod(ModBase mod, String commandName) {
        this.mod = mod;
        this.commandName = commandName;
    }

    protected ModBase getMod() {
        return this.mod;
    }

    public String getCommandName() {
        return this.commandName;
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public int run(CommandContext<ICommandSender> context) {
        ICommandSender sender = context.getSource();
        printLineToChat(
            sender,
            EnumChatFormatting.GREEN + "["
                + mod.getModName()
                + "] "
                + EnumChatFormatting.RESET
                + "Use subcommands to continue.");
        return Command.SINGLE_SUCCESS;
    }

    public LiteralArgumentBuilder<ICommandSender> make() {
        return LiteralArgumentBuilder.<ICommandSender>literal(getCommandName())
            .requires(sender -> sender.canCommandSenderUseCommand(getRequiredPermissionLevel(), getMod().getModId()))
            .executes(this);
    }

    protected void printLineToChat(ICommandSender sender, String line) {
        sender.addChatMessage(new ChatComponentText(line));
    }

    protected void printErrorToChat(ICommandSender sender, String line) {
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + line));
    }

    protected void sendLocalizedMessage(ICommandSender sender, String key, Object... params) {
        printLineToChat(sender, LangHelpers.localize(key, params));
    }
}
