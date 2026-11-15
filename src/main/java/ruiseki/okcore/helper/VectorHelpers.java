package ruiseki.okcore.helper;

import net.minecraft.client.audio.ISound;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;

import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class VectorHelpers {

    public static Vector3d atCenterOf(Vector3i vector) {
        return new Vector3d(vector.x() + 0.5f, vector.y() + 0.5f, vector.z() + 0.5f);
    }

    public static Vector3d atCenterOf(Vector3f vector) {
        return new Vector3d(vector.x() + 0.5f, vector.y() + 0.5f, vector.z() + 0.5f);
    }

    public static Vector3d atCenterOf(Vector3d vector) {
        return new Vector3d(vector.x() + 0.5f, vector.y() + 0.5f, vector.z() + 0.5f);
    }

    public static Vector3d atCenterOf(Vec3 vector) {
        return new Vector3d(vector.xCoord + 0.5f, vector.yCoord + 0.5f, vector.zCoord + 0.5f);
    }

    public static Vector3d createVector3d(Entity entity) {
        return new Vector3d(entity.posX, entity.posY, entity.posZ);
    }

    public static Vector3d createVector3d(TileEntity tile) {
        return new Vector3d(tile.xCoord, tile.yCoord, tile.zCoord);
    }

    public static Vector3d createVector3d(Vector3i pos) {
        return new Vector3d(pos.x(), pos.y(), pos.z());
    }

    public static Vector3d createVector3d(ChunkPosition pos) {
        return new Vector3d(pos.chunkPosX, pos.chunkPosY, pos.chunkPosZ);
    }

    public static Vector3d createVector3d(ChunkCoordinates pos) {
        return new Vector3d(pos.posX, pos.posY, pos.posZ);
    }

    public static Vector3d createVector3d(MovingObjectPosition pos) {
        return new Vector3d(pos.blockX, pos.blockY, pos.blockZ);
    }

    public static Vector3d createVector3d(ISound pos) {
        return new Vector3d(pos.getXPosF(), pos.getYPosF(), pos.getZPosF());
    }

    public static Vector3d createVector3d(Vec3 vector) {
        return new Vector3d(vector.xCoord, vector.yCoord, vector.zCoord);
    }
}
