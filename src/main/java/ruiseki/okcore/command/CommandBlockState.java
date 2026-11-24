package ruiseki.okcore.command;

import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import ruiseki.okcore.block.state.IBlockState;
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
                .executes(this::runGet));
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
}
