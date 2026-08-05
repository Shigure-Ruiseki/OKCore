package ruiseki.commoncapabilities.api.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

/**
 * Default implementation of a {@link IWrench} that only applies to blocks.
 * 
 * @author rubensworks
 */
public class DefaultWrench implements IWrench {

    @Override
    public boolean canUse(EntityPlayer player, WrenchTarget target) {
        return target.getType() == MovingObjectPosition.MovingObjectType.BLOCK;
    }

    @Override
    public void beforeUse(EntityPlayer player, WrenchTarget target) {

    }

    @Override
    public void afterUse(EntityPlayer player, WrenchTarget target) {

    }
}
