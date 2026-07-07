package ruiseki.okcore.helper;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.okcore.entity.cooldown.ICooldownHandler;
import ruiseki.okcore.entity.cooldown.ItemCooldowns;

public class EntityHelpers {

    public static ItemCooldowns getItemCooldowns(EntityPlayer player) {
        if (player == null) return null;
        try {
            ICooldownHandler provider = (ICooldownHandler) (Object) player;

            return provider.getItemCooldowns();

        } catch (ClassCastException ignored) {
            return null;
        }
    }

    public static float getYawFromFacing(ForgeDirection facing) {
        switch (facing) {
            case NORTH:
                return 180F;
            case WEST:
                return 90F;
            case EAST:
                return 270F;
            case SOUTH:
            default:
                return 0F;
        }
    }

    public static float getPitchFromFacing(ForgeDirection facing) {
        switch (facing) {
            case DOWN:
                return 90F;
            case UP:
                return -90F;
            default:
                return 0F;
        }
    }

    public static void setEntityFacing(EntityLivingBase entity, ForgeDirection currentFacing) {
        float yaw = getYawFromFacing(currentFacing);
        float pitch = getPitchFromFacing(currentFacing);

        entity.rotationYaw = yaw;
        entity.rotationPitch = pitch;

        entity.prevRotationYaw = yaw;
        entity.prevRotationPitch = pitch;

        entity.rotationYawHead = yaw;
        entity.prevRotationYawHead = yaw;
    }
}
