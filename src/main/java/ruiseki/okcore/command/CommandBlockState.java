
package ruiseki.okcore.command;

import java.util.Map;
import java.util.Optional;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import ruiseki.okcore.block.state.IBlockState;
import ruiseki.okcore.block.state.property.IListedProperty;
import ruiseki.okcore.block.state.property.IProperty;
import ruiseki.okcore.block.state.property.IUnlistedProperty;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.BlockStateHelpers;
import ruiseki.okcore.init.ModBase;

public class CommandBlockState extends CommandMod {

    public CommandBlockState(ModBase mod) {
        super(mod, "blockstate");
    }

    @Override
    public LiteralArgumentBuilder<ICommandSender> make() {
        return super.make().then(
            LiteralArgumentBuilder.<ICommandSender>literal("get")
                .executes(this::runGet))
            .then(
                LiteralArgumentBuilder.<ICommandSender>literal("set")
                    .then(
                        RequiredArgumentBuilder.<ICommandSender, String>argument("property", StringArgumentType.word())
                            .then(
                                RequiredArgumentBuilder
                                    .<ICommandSender, String>argument("value", StringArgumentType.word())
                                    .executes(this::runSet))));
    }

    private int runGet(CommandContext<ICommandSender> context) {
        ICommandSender sender = context.getSource();

        World world = sender.getEntityWorld();
        ChunkCoordinates coordinates = sender.getPlayerCoordinates();

        BlockPos pos = new BlockPos(coordinates.posX, coordinates.posY - 1, coordinates.posZ);

        IBlockState state = BlockStateHelpers.getBlockState(world, pos);

        if (state == null) {
            printErrorToChat(sender, "Block does not have a native BlockState.");
            return 0;
        }

        printLineToChat(sender, EnumChatFormatting.GOLD + "BlockState");
        printPosition(sender, pos);

        printLineToChat(
            sender,
            "Block: " + state.getBlock()
                .getUnlocalizedName());

        for (Map.Entry<?, ?> entry : state.getProperties()
            .entrySet()) {
            printLineToChat(
                sender,
                EnumChatFormatting.YELLOW + String.valueOf(entry.getKey())
                    + EnumChatFormatting.RESET
                    + ": "
                    + EnumChatFormatting.GREEN
                    + String.valueOf(entry.getValue()));
        }

        return Command.SINGLE_SUCCESS;
    }

    private int runSet(CommandContext<ICommandSender> context) {
        ICommandSender sender = context.getSource();

        String propertyName = StringArgumentType.getString(context, "property");
        String valueName = StringArgumentType.getString(context, "value");

        World world = sender.getEntityWorld();
        ChunkCoordinates coordinates = sender.getPlayerCoordinates();

        BlockPos pos = new BlockPos(coordinates.posX, coordinates.posY - 1, coordinates.posZ);

        IBlockState state = BlockStateHelpers.getBlockState(world, pos);

        if (state == null) {
            printErrorToChat(sender, "Block does not have a native BlockState.");
            return 0;
        }

        IProperty<?> targetProperty = null;

        for (IProperty<?> property : state.getPropertyKeys()) {
            if (property.getName()
                .equals(propertyName)) {
                targetProperty = property;
                break;
            }
        }

        if (targetProperty == null) {
            for (IUnlistedProperty<?> property : state.getUnlistedProperties()) {
                if (property.getName()
                    .equals(propertyName)) {
                    targetProperty = property;
                    break;
                }
            }
        }

        if (targetProperty == null) {
            printErrorToChat(sender, "Unknown property: " + propertyName);
            return 0;
        }

        if (targetProperty instanceof IListedProperty<?>listedProperty) {
            return setListedProperty(sender, world, pos, state, listedProperty, valueName);
        }

        if (targetProperty instanceof IUnlistedProperty<?>unlistedProperty) {
            return setUnlistedProperty(sender, world, pos, state, unlistedProperty, valueName);
        }

        printErrorToChat(sender, "Unsupported property: " + propertyName);
        return 0;
    }

    private <T extends Comparable<T>> int setListedProperty(ICommandSender sender, World world, BlockPos pos,
        IBlockState state, IListedProperty<T> property, String valueName) {
        Optional<T> parsedValue = property.parseValue(valueName);

        if (parsedValue.isEmpty()) {
            printErrorToChat(sender, "Invalid value '" + valueName + "' for property '" + property.getName() + "'.");
            return 0;
        }

        IBlockState newState = state.withProperty(property, parsedValue.get());

        if (!newState.setBlockState(world, pos)) {
            printErrorToChat(sender, "Failed to set BlockState.");
            return 0;
        }

        printPosition(sender, pos);
        printLineToChat(
            sender,
            EnumChatFormatting.GREEN + "Updated "
                + EnumChatFormatting.YELLOW
                + property.getName()
                + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GREEN
                + property.valueToString(parsedValue.get()));

        return Command.SINGLE_SUCCESS;
    }

    private <T extends Comparable<T>> int setUnlistedProperty(ICommandSender sender, World world, BlockPos pos,
        IBlockState state, IUnlistedProperty<T> property, String valueName) {
        Optional<T> parsedValue = property.parseValue(valueName);

        if (parsedValue.isEmpty()) {
            printErrorToChat(sender, "Invalid value '" + valueName + "' for property '" + property.getName() + "'.");
            return 0;
        }

        IBlockState newState = state.withProperty(property, parsedValue.get());

        if (!newState.setBlockState(world, pos)) {
            printErrorToChat(sender, "Failed to set BlockState.");
            return 0;
        }

        printPosition(sender, pos);
        printLineToChat(
            sender,
            EnumChatFormatting.GREEN + "Updated "
                + EnumChatFormatting.YELLOW
                + property.getName()
                + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GREEN
                + property.valueToString(parsedValue.get()));

        return Command.SINGLE_SUCCESS;
    }

    private void printPosition(ICommandSender sender, BlockPos pos) {
        printLineToChat(
            sender,
            EnumChatFormatting.GRAY + "Position: "
                + EnumChatFormatting.YELLOW
                + "X="
                + pos.getX()
                + ", Y="
                + pos.getY()
                + ", Z="
                + pos.getZ());
    }
}
