package ruiseki.okcore.helper;

import static org.junit.Assert.*;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;

import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.junit.Test;

public class VectorHelpersTest {

    private void assertVector(double x, double y, double z, Vector3d actual) {
        assertEquals(x, actual.x, 0);
        assertEquals(y, actual.y, 0);
        assertEquals(z, actual.z, 0);
    }

    @Test
    public void centerOverloadsOffsetAllAxesWithoutChangingInputs() {
        Vector3i ints = new Vector3i(-2, 0, 3);
        Vector3f floats = new Vector3f(-2.25f, 0, 3.25f);
        Vector3d doubles = new Vector3d(-2.25, 0, 3.25);
        Vec3 vanilla = Vec3.createVectorHelper(-2.25, 0, 3.25);
        assertVector(-1.5, 0.5, 3.5, VectorHelpers.atCenterOf(ints));
        assertVector(-1.75, 0.5, 3.75, VectorHelpers.atCenterOf(floats));
        assertVector(-1.75, 0.5, 3.75, VectorHelpers.atCenterOf(doubles));
        assertVector(-1.75, 0.5, 3.75, VectorHelpers.atCenterOf(vanilla));
        assertEquals(new Vector3i(-2, 0, 3), ints);
        assertEquals(new Vector3f(-2.25f, 0, 3.25f), floats);
        assertEquals(new Vector3d(-2.25, 0, 3.25), doubles);
        assertEquals(-2.25, vanilla.xCoord, 0);
    }

    @Test
    public void coordinateConversionsPreserveNegativeAndLargeCoordinates() {
        int x = -30000000;
        int y = 255;
        int z = 30000000;
        assertVector(x, y, z, VectorHelpers.createVector3d(new Vector3i(x, y, z)));
        assertVector(x, y, z, VectorHelpers.createVector3d(new ChunkPosition(x, y, z)));
        assertVector(x, y, z, VectorHelpers.createVector3d(new ChunkCoordinates(x, y, z)));
        TileEntity tile = new TileEntity();
        tile.xCoord = x;
        tile.yCoord = y;
        tile.zCoord = z;
        assertVector(x, y, z, VectorHelpers.createVector3d(tile));
        MovingObjectPosition hit = new MovingObjectPosition(x, y, z, 1, Vec3.createVectorHelper(0, 0, 0));
        assertVector(x, y, z, VectorHelpers.createVector3d(hit));
    }

    @Test
    public void vanillaVectorConversionPreservesDoublePrecisionAndCreatesIndependentValue() {
        Vec3 source = Vec3.createVectorHelper(-1234567.123456789, 0.000000001, 42.125);
        Vector3d copy = VectorHelpers.createVector3d(source);
        assertVector(source.xCoord, source.yCoord, source.zCoord, copy);
        copy.x = 0;
        assertEquals(-1234567.123456789, source.xCoord, 0);
    }
}
