package ruiseki.okcore.command;

import net.minecraft.command.ICommandSender;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import ruiseki.okcore.init.ModBase;

/**
 * Command for checking the version via Brigadier.
 * Extended from CommandMod.
 *
 * @author ruiseki
 *
 */
public class CommandVersion extends CommandMod {

    public static final String NAME = "version";

    public CommandVersion(ModBase mod) {
        super(mod);
    }

    @Override
    public String getCommandName() {
        return NAME;
    }

    @Override
    public int run(CommandContext<ICommandSender> context) {
        ICommandSender sender = context.getSource();

        String version = getMod().getReferenceValue(ModBase.REFKEY_MOD_VERSION);

        printLineToChat(sender, version);

        return Command.SINGLE_SUCCESS;
    }

    @Override
    public LiteralArgumentBuilder<ICommandSender> make() {
        return LiteralArgumentBuilder.<ICommandSender>literal(getCommandName())
            .requires(sender -> sender.canCommandSenderUseCommand(getRequiredPermissionLevel(), getMod().getModId()))
            .executes(this);
    }
}
