package ruiseki.okcore.block;

import java.util.Random;

import net.minecraft.world.World;

/**
 * Interface for the {@link ParticleDropBlockComponent}.
 *
 * @author rubensworks
 *
 */
public interface IEntityDropParticleFXBlock {

    /**
     * Called when a random display tick occurs.
     *
     * @param world The world.
     * @param x,    y, z The position.
     * @param rand  Random object.
     */
    public void randomDisplayTick(World world, int x, int y, int z, Random rand);
}
