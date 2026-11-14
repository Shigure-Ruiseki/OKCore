package ruiseki.okcore.helper;

import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class VectorHelpers {

    protected Vector3d atCenterOf(Vector3i vector) {
        return new Vector3d(vector.x() + 0.5f, vector.y() + 0.5f, vector.z() + 0.5f);
    }

    protected Vector3d atCenterOf(Vector3f vector) {
        return new Vector3d(vector.x() + 0.5f, vector.y() + 0.5f, vector.z() + 0.5f);
    }

    protected Vector3d atCenterOf(Vector3d vector) {
        return new Vector3d(vector.x() + 0.5f, vector.y() + 0.5f, vector.z() + 0.5f);
    }
}
