package ruiseki.okcore.helper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

public class DirectionHelpers {

    public static ForgeDirection yawToDirection6(EntityLivingBase entity) {
        float pitch = entity.rotationPitch;

        if (pitch > 60) {
            return ForgeDirection.UP;
        }
        if (pitch < -60) {
            return ForgeDirection.DOWN;
        }

        int yaw = MathHelper.floor_double((entity.rotationYaw * 4F / 360F) + 0.5D) & 3;
        return switch (yaw) {
            case 1 -> ForgeDirection.EAST;
            case 2 -> ForgeDirection.SOUTH;
            case 3 -> ForgeDirection.WEST;
            default -> ForgeDirection.NORTH;
        };
    }

    public static ForgeDirection yawToDirection4(EntityLivingBase entity) {
        int yaw = MathHelper.floor_double((entity.rotationYaw * 4F / 360F) + 0.5D) & 3;
        return switch (yaw) {
            case 1 -> ForgeDirection.EAST;
            case 2 -> ForgeDirection.SOUTH;
            case 3 -> ForgeDirection.WEST;
            default -> ForgeDirection.NORTH;
        };
    }

    public static ForgeDirection metaToDirection6(int meta) {
        return ForgeDirection.getOrientation(meta & 7);
    }

    public static int direction6ToMeta(ForgeDirection dir) {
        return dir.ordinal() & 7;
    }

    public static ForgeDirection metaToDirection4(int meta) {
        return ForgeDirection.getOrientation((meta & 3) + 2);
    }

    public static int direction4ToMeta(ForgeDirection dir) {
        if (dir.ordinal() < 2) return 0;
        return dir.ordinal() - 2;
    }
}
