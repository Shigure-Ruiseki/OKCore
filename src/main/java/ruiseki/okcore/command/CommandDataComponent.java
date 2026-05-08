package ruiseki.okcore.command;

import static net.minecraft.command.CommandBase.getListOfStringsMatchingLastWord;

import java.util.List;
import java.util.Map;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.apache.logging.log4j.Level;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.datacomponent.core.DataComponentType;
import ruiseki.okcore.datacomponent.registry.DataComponentRegistry;
import ruiseki.okcore.init.ModBase;

public class CommandDataComponent extends CommandMod {

    public static final String NAME = "datacomponent";

    public CommandDataComponent(ModBase mod) {
        super(mod, NAME);
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "datacomponent <get|set> [component name] [component value]";
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "get", "set");
        }
        return super.addTabCompletionOptions(sender, args);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayer player)) return;

        ItemStack stack = player.inventory.getCurrentItem();
        if (stack == null) {
            printErrorToChat(sender, "You must be holding an item to use this command.");
            return;
        }

        String action = DataComponentType.getIndexSafe(args, 0);
        String name = DataComponentType.getIndexSafe(args, 1);
        String value = DataComponentType.getIndexSafe(args, 2);

        if (action == null || ("set".equals(action) && (name == null || value == null))) {
            printErrorToChat(sender, getCommandUsage(sender));
            return;
        }

        Map<String, DataComponentType<?>> components = new Object2ObjectOpenHashMap<>();
        DataComponentRegistry.getComponents(stack, components);

        if ("get".equals(action)) {
            if (name != null) {
                @SuppressWarnings("unchecked")
                DataComponentType<Object> comp = (DataComponentType<Object>) components.get(name);

                if (comp != null) {
                    Object v = comp.getValue(stack);
                    printLineToChat(
                        sender,
                        EnumChatFormatting.GOLD + comp.getName() + ": " + EnumChatFormatting.WHITE + comp.stringify(v));
                    return;
                }
                printErrorToChat(sender, "Component '" + name + "' not found on this item.");
            } else {
                printLineToChat(player, EnumChatFormatting.AQUA + "Components for [" + stack.getDisplayName() + "]:");
                if (components.isEmpty()) {
                    printLineToChat(player, EnumChatFormatting.GRAY + "None");
                    return;
                }

                for (var entry : components.entrySet()) {
                    @SuppressWarnings("unchecked")
                    DataComponentType<Object> comp = (DataComponentType<Object>) entry.getValue();
                    Object v = comp.getValue(stack);
                    printLineToChat(
                        player,
                        "- " + EnumChatFormatting.YELLOW
                            + comp.getName()
                            + ": "
                            + EnumChatFormatting.WHITE
                            + comp.stringify(v));
                }
            }
        } else if ("set".equals(action)) {

            @SuppressWarnings("unchecked")
            DataComponentType<Object> comp = (DataComponentType<Object>) components.get(name);

            if (comp != null) {
                try {
                    Object v = comp.parse(value);
                    comp.setValue(stack, v);

                    printLineToChat(sender, EnumChatFormatting.GREEN + "Set " + name + " to " + value);
                } catch (Throwable t) {
                    OKCore.okLog(Level.ERROR, "Error setting component {}", name, t);
                    printErrorToChat(player, "Error setting component: " + t.getMessage());
                }
                return;
            }
            printErrorToChat(sender, "Component '" + name + "' not found on this item.");
        }
    }
}
