package ruiseki.okcore.helper;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.mojang.authlib.GameProfile;

import ruiseki.okcore.datastructure.BlockPos;

public class PlaceBlockHelpers {

    public static class BuildPlayer extends EntityPlayer {

        private ItemStack holding;
        private ForgeDirection facing;

        public BuildPlayer(World worldIn, GameProfile gameProfileIn, ItemStack holding, ForgeDirection facing) {
            super(worldIn, gameProfileIn);
            this.holding = holding;
            this.facing = facing;
        }

        // Phương thức tương đương để lấy hướng nhìn ngang trong 1.7.10 nếu cần
        public ForgeDirection getPlayerFacing() {
            return facing == null ? ForgeDirection.NORTH : facing;
        }

        @Override
        public ItemStack getHeldItem() {
            return holding;
        }

        @Override
        public void addChatMessage(net.minecraft.util.IChatComponent chat) {}

        @Override
        public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
            return false;
        }

        @Override
        public ChunkCoordinates getPlayerCoordinates() {
            return new ChunkCoordinates((int) this.posX, (int) this.posY, (int) this.posZ);
        }
    }

    public static boolean buildStackAsPlayer(World world, EntityPlayer player, BlockPos pos, ItemStack stack) {
        return buildStackAsPlayer(
            world,
            player,
            pos.getX(),
            pos.getY(),
            pos.getZ(),
            stack,
            ForgeDirection.UP,
            0.0F,
            0.0F,
            0.0F,
            ForgeDirection.NORTH);
    }

    public static boolean buildStackAsPlayer(World world, EntityPlayer player, int x, int y, int z, ItemStack stack) {
        return buildStackAsPlayer(
            world,
            player,
            x,
            y,
            z,
            stack,
            ForgeDirection.UP,
            0.0F,
            0.0F,
            0.0F,
            ForgeDirection.NORTH);
    }

    public static boolean buildStackAsPlayer(World world, EntityPlayer player, BlockPos pos, float hitX, float hitY,
        float hitZ, ItemStack stack) {
        return buildStackAsPlayer(
            world,
            player,
            pos.getX(),
            pos.getY(),
            pos.getZ(),
            stack,
            ForgeDirection.UP,
            hitX,
            hitY,
            hitZ,
            ForgeDirection.NORTH);
    }

    public static boolean buildStackAsPlayer(World world, EntityPlayer player, int x, int y, int z, ItemStack stack,
        ForgeDirection sideMouseover, float hitX, float hitY, float hitZ, ForgeDirection playerFacing) {
        if (sideMouseover == null) {
            sideMouseover = ForgeDirection.UP;
        }
        if (playerFacing == null) {
            playerFacing = ForgeDirection.NORTH;
        }

        BuildPlayer builder = new BuildPlayer(world, player.getGameProfile(), stack, playerFacing);

        builder.posX = x;
        builder.posY = y;
        builder.posZ = z;

        if (stack.getItem() == null) return false;

        return stack.getItem()
            .onItemUse(stack, builder, world, x, y, z, sideMouseover.ordinal(), hitX, hitY, hitZ);
    }

    public static boolean placeStateSafe(World world, EntityPlayer player, int x, int y, int z, Block placeBlock,
        int placeMeta) {
        return placeStateSafe(world, player, x, y, z, placeBlock, placeMeta, false);
    }

    public static boolean placeStateSafe(World world, EntityPlayer player, int x, int y, int z, Block placeBlock,
        int placeMeta, boolean playSound) {
        if (placeBlock == null) {
            return false;
        }

        if (!world.isAirBlock(x, y, z)) {
            Block blockHere = world.getBlock(x, y, z);
            if (blockHere != null) {
                if (!blockHere.isReplaceable(world, x, y, z)) {
                    return false;
                }
                if (!blockHere.getMaterial()
                    .isLiquid()) {
                    if (!world.isRemote) {
                        world.func_147480_a(x, y, z, true);
                    }
                }
            }
        }

        if (placeBlock instanceof BlockLeaves || placeBlock instanceof BlockOldLeaf
            || placeBlock instanceof BlockNewLeaf) {
            placeMeta = placeMeta | 4;
        }

        boolean success = false;
        try {
            if (!world.isRemote) {
                success = world.setBlock(x, y, z, placeBlock, placeMeta, 3);
            }
        } catch (Exception e) {
            System.err.println("Error attempting to place block at " + x + "," + y + "," + z + " : " + e.getMessage());
        }

        if (success && playSound) {
            world.playSoundEffect(
                x + 0.5D,
                y + 0.5D,
                z + 0.5D,
                placeBlock.stepSound.getBreakSound(),
                (placeBlock.stepSound.getVolume() + 1.0F) / 2.0F,
                placeBlock.stepSound.getPitch() * 0.8F);
        }
        return success;
    }

    public static ArrayList<Block> ignoreList = new ArrayList<Block>();

    private static void translateCSV() {
        if (ignoreList.isEmpty()) {
            ignoreList.add(Blocks.end_portal_frame);
            ignoreList.add(Blocks.end_portal);
            ignoreList.add(Blocks.portal);
            ignoreList.add(Blocks.bed);
            ignoreList.add(Blocks.log);
            ignoreList.add(Blocks.log2);
            ignoreList.add(Blocks.iron_door);
            ignoreList.add(Blocks.skull);
            ignoreList.add(Blocks.double_plant);
        }
    }

    public static boolean moveBlockTo(World world, EntityPlayer player, int x, int y, int z, int moveToX, int moveToY,
        int moveToZ) {
        Block blockToMove = world.getBlock(x, y, z);
        int metaToMove = world.getBlockMetadata(x, y, z);
        translateCSV();

        if (blockToMove == Blocks.air || ignoreList.contains(blockToMove)) {
            return false;
        }
        if (blockToMove.getBlockHardness(world, x, y, z) == -1.0F) {
            return false;
        }

        boolean moved = false;
        if (world.isAirBlock(moveToX, moveToY, moveToZ) && player.canPlayerEdit(x, y, z, 0, null)) {
            TileEntity tile = world.getTileEntity(x, y, z);
            NBTTagCompound tileData = null;
            if (tile != null) {
                tileData = new NBTTagCompound();
                tile.writeToNBT(tileData);
            }

            destroyBlock(world, x, y, z);

            moved = PlaceBlockHelpers.placeStateSafe(world, player, moveToX, moveToY, moveToZ, blockToMove, metaToMove);
            if (moved) {
                if (tileData != null) {
                    TileEntity newTile = world.getTileEntity(moveToX, moveToY, moveToZ);
                    if (newTile != null) {
                        tileData.setInteger("x", moveToX);
                        tileData.setInteger("y", moveToY);
                        tileData.setInteger("z", moveToZ);
                        newTile.readFromNBT(tileData);
                        newTile.markDirty();
                        world.markBlockForUpdate(moveToX, moveToY, moveToZ);
                    }
                }
            }
        }
        return moved;
    }

    public static boolean destroyBlock(World world, int x, int y, int z) {
        if (world.getTileEntity(x, y, z) != null) {
            world.removeTileEntity(x, y, z);
        }
        try {
            boolean setToAirSuccess = world.setBlockToAir(x, y, z);
            if (!setToAirSuccess) {
                setToAirSuccess = world.func_147480_a(x, y, z, false);
            }
        } catch (Exception e) {
            System.err.println("Error thrown by a tile entity when removing the block: " + e.getMessage());
            return false;
        }

        world.markBlockForUpdate(x, y, z);

        tryUpdateNeighbour(world, x, y, z - 1);
        tryUpdateNeighbour(world, x, y, z + 1);
        tryUpdateNeighbour(world, x + 1, y, z);
        tryUpdateNeighbour(world, x - 1, y, z);
        return true;
    }

    public static void tryUpdateNeighbour(World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile != null) {
            tile.updateContainingBlockInfo();
            tile.markDirty();
        }
    }
}
