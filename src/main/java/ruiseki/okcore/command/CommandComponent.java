package ruiseki.okcore.command;

import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import org.apache.logging.log4j.Level;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.datacomponent.core.DataComponentType;
import ruiseki.okcore.datacomponent.registry.DataComponentRegistry;
import ruiseki.okcore.init.ModBase;

public class CommandComponent extends CommandMod {

    public static final String NAME = "component";

    public CommandComponent(ModBase mod) {
        super(mod);
    }

    @Override
    public String getCommandName() {
        return NAME;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public LiteralArgumentBuilder<ICommandSender> make() {
        // /okcore component
        // ├── get <name>
        // └── set <name> <value>

        return LiteralArgumentBuilder.<ICommandSender>literal(getCommandName())
            .requires(sender -> sender.canCommandSenderUseCommand(getRequiredPermissionLevel(), getMod().getModId()))
            .then(
                LiteralArgumentBuilder.<ICommandSender>literal("get")
                    .executes(this::executeGetAll)
                    .then(
                        RequiredArgumentBuilder.<ICommandSender, String>argument("name", StringArgumentType.word())
                            .executes(this::executeGetOne)))
            .then(
                LiteralArgumentBuilder.<ICommandSender>literal("set")
                    .then(
                        RequiredArgumentBuilder.<ICommandSender, String>argument("name", StringArgumentType.word())
                            .then(
                                RequiredArgumentBuilder
                                    .<ICommandSender, String>argument("value", StringArgumentType.greedyString())
                                    .executes(this::executeSet))));
    }

    private int executeGetAll(CommandContext<ICommandSender> context) {
        EntityPlayer player = getPlayerOrError(context.getSource());
        if (player == null) return 0;

        ItemStack stack = getHeldItemOrError(player);
        if (stack == null) return 0;

        Map<String, DataComponentType<?>> components = new Object2ObjectOpenHashMap<>();
        DataComponentRegistry.getComponents(stack, components);

        printLineToChat(player, EnumChatFormatting.AQUA + "Components for [" + stack.getDisplayName() + "]:");

        if (components.isEmpty()) {
            printLineToChat(player, EnumChatFormatting.GRAY + "None");
            return Command.SINGLE_SUCCESS;
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

        return Command.SINGLE_SUCCESS;
    }

    private int executeGetOne(CommandContext<ICommandSender> context) {
        EntityPlayer player = getPlayerOrError(context.getSource());
        if (player == null) return 0;

        ItemStack stack = getHeldItemOrError(player);
        if (stack == null) return 0;

        String name = StringArgumentType.getString(context, "name");

        Map<String, DataComponentType<?>> components = new Object2ObjectOpenHashMap<>();
        DataComponentRegistry.getComponents(stack, components);

        @SuppressWarnings("unchecked")
        DataComponentType<Object> comp = (DataComponentType<Object>) components.get(name);

        if (comp != null) {
            Object v = comp.getValue(stack);
            printLineToChat(
                player,
                EnumChatFormatting.GOLD + comp.getName() + ": " + EnumChatFormatting.WHITE + comp.stringify(v));
        } else {
            printErrorToChat(player, "Component '" + name + "' not found on this item.");
        }

        return Command.SINGLE_SUCCESS;
    }

    private int executeSet(CommandContext<ICommandSender> context) {
        EntityPlayer player = getPlayerOrError(context.getSource());
        if (player == null) return 0;

        ItemStack stack = getHeldItemOrError(player);
        if (stack == null) return 0;

        String name = StringArgumentType.getString(context, "name");
        String value = StringArgumentType.getString(context, "value");

        Map<String, DataComponentType<?>> components = new Object2ObjectOpenHashMap<>();
        DataComponentRegistry.getComponents(stack, components);

        @SuppressWarnings("unchecked")
        DataComponentType<Object> comp = (DataComponentType<Object>) components.get(name);

        if (comp != null) {
            try {
                Object v = comp.parse(value);
                comp.setValue(stack, v);
                printLineToChat(player, EnumChatFormatting.GREEN + "Set " + name + " to " + value);
            } catch (Throwable t) {
                OKCore.okLog(Level.ERROR, "Error setting component {}", name, t);
                printErrorToChat(player, "Error setting component: " + t.getMessage());
            }
        } else {
            printErrorToChat(player, "Component '" + name + "' not found on this item.");
        }

        return Command.SINGLE_SUCCESS;
    }

    private EntityPlayer getPlayerOrError(ICommandSender sender) {
        if (sender instanceof EntityPlayer) {
            return (EntityPlayer) sender;
        }
        printErrorToChat(sender, "Only players can use this command.");
        return null;
    }

    private ItemStack getHeldItemOrError(EntityPlayer player) {
        ItemStack stack = player.inventory.getCurrentItem();
        if (stack == null) {
            printErrorToChat(player, "You must be holding an item to use this command.");
        }
        return stack;
    }
}
