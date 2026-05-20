package ruiseki.okcore.command;

import java.util.Map;

import net.minecraft.command.ICommand;

import ruiseki.okcore.init.ModBase;

/**
 * Main command handler for /okcore
 * Delegates to subcommand handlers for extensibility.
 */
public class CommandOKCore extends CommandMod {

    public CommandOKCore(ModBase mod, Map<String, ICommand> subCommands) {
        super(mod, subCommands);
        addSubcommands(CommandDataComponent.NAME, new CommandDataComponent(mod));
        addSubcommands(CommandReload.NAME, new CommandReload(mod));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2; // OP required
    }
}
