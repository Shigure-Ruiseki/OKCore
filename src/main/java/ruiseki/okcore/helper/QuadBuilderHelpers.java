package ruiseki.okcore.helper;

import static com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.properties.ModelQuadFacing.fromForgeDir;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.gtnhlib.client.model.baked.PileOfQuads;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.ModelQuad;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.ModelQuadView;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.ModelQuadViewMutable;
import com.gtnewhorizon.gtnhlib.client.renderer.cel.model.quad.properties.ModelQuadFacing;

public class QuadBuilderHelpers {

    public static ModelQuad buildFaceQuad(@NotNull ForgeDirection side, float minX, float minY, float minZ, float maxX,
        float maxY, float maxZ, @NotNull IIcon icon, float u0, float v0, float u1, float v1) {
        ModelQuad quad = new ModelQuad();
        setupQuadVertices(quad, side, minX, minY, minZ, maxX, maxY, maxZ, icon, u0, v0, u1, v1);
        return quad;
    }

    public static Map<ModelQuadFacing, ArrayList<ModelQuadView>> buildCuboidStore(float minX, float minY, float minZ,
        float maxX, float maxY, float maxZ, @NotNull IIcon icon, @Nullable boolean[] renderFaces) {
        Map<ModelQuadFacing, ArrayList<ModelQuadView>> map = new EnumMap<>(ModelQuadFacing.class);

        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            if (renderFaces == null || renderFaces[side.ordinal()]) {
                float u0 = 0, v0 = 0, u1 = 16, v1 = 16;
                switch (side) {
                    case DOWN, UP -> {
                        u0 = minX * 16.0f;
                        v0 = minZ * 16.0f;
                        u1 = maxX * 16.0f;
                        v1 = maxZ * 16.0f;
                    }
                    case NORTH, SOUTH -> {
                        u0 = minX * 16.0f;
                        v0 = (1.0f - maxY) * 16.0f;
                        u1 = maxX * 16.0f;
                        v1 = (1.0f - minY) * 16.0f;
                    }
                    case WEST, EAST -> {
                        u0 = minZ * 16.0f;
                        v0 = (1.0f - maxY) * 16.0f;
                        u1 = maxZ * 16.0f;
                        v1 = (1.0f - minY) * 16.0f;
                    }
                }

                ModelQuad quad = buildFaceQuad(side, minX, minY, minZ, maxX, maxY, maxZ, icon, u0, v0, u1, v1);
                ModelQuadFacing facing = fromForgeDir(side);
                map.computeIfAbsent(facing, k -> new ArrayList<>())
                    .add(quad);
            }
        }
        return map;
    }

    public static PileOfQuads buildCuboidPile(float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
        @NotNull IIcon icon, @Nullable boolean[] renderFaces) {
        Map<ModelQuadFacing, ArrayList<ModelQuadView>> store = buildCuboidStore(
            minX,
            minY,
            minZ,
            maxX,
            maxY,
            maxZ,
            icon,
            renderFaces);
        return new PileOfQuads(store, null, icon);
    }

    private static void setupQuadVertices(ModelQuadViewMutable quad, ForgeDirection side, float minX, float minY,
        float minZ, float maxX, float maxY, float maxZ, IIcon icon, float u0, float v0, float u1, float v1) {

        float atlasMinU = icon.getMinU();
        float atlasMinV = icon.getMinV();
        float dU = icon.getMaxU() - atlasMinU;
        float dV = icon.getMaxV() - atlasMinV;

        float uMin = atlasMinU + (u0 / 16.0f) * dU;
        float vMin = atlasMinV + (v0 / 16.0f) * dV;
        float uMax = atlasMinU + (u1 / 16.0f) * dU;
        float vMax = atlasMinV + (v1 / 16.0f) * dV;

        switch (side) {
            case DOWN -> {
                setVertex(quad, 0, minX, minY, maxZ, uMin, vMax);
                setVertex(quad, 1, minX, minY, minZ, uMin, vMin);
                setVertex(quad, 2, maxX, minY, minZ, uMax, vMin);
                setVertex(quad, 3, maxX, minY, maxZ, uMax, vMax);
            }
            case UP -> {
                setVertex(quad, 0, minX, maxY, minZ, uMin, vMin);
                setVertex(quad, 1, minX, maxY, maxZ, uMin, vMax);
                setVertex(quad, 2, maxX, maxY, maxZ, uMax, vMax);
                setVertex(quad, 3, maxX, maxY, minZ, uMax, vMin);
            }
            case NORTH -> {
                setVertex(quad, 0, maxX, maxY, minZ, uMin, vMin);
                setVertex(quad, 1, maxX, minY, minZ, uMin, vMax);
                setVertex(quad, 2, minX, minY, minZ, uMax, vMax);
                setVertex(quad, 3, minX, maxY, minZ, uMax, vMin);
            }
            case SOUTH -> {
                setVertex(quad, 0, minX, maxY, maxZ, uMin, vMin);
                setVertex(quad, 1, minX, minY, maxZ, uMin, vMax);
                setVertex(quad, 2, maxX, minY, maxZ, uMax, vMax);
                setVertex(quad, 3, maxX, maxY, maxZ, uMax, vMin);
            }
            case WEST -> {
                setVertex(quad, 0, minX, maxY, minZ, uMin, vMin);
                setVertex(quad, 1, minX, minY, minZ, uMin, vMax);
                setVertex(quad, 2, minX, minY, maxZ, uMax, vMax);
                setVertex(quad, 3, minX, maxY, maxZ, uMax, vMin);
            }
            case EAST -> {
                setVertex(quad, 0, maxX, maxY, maxZ, uMin, vMin);
                setVertex(quad, 1, maxX, minY, maxZ, uMin, vMax);
                setVertex(quad, 2, maxX, minY, minZ, uMax, vMax);
                setVertex(quad, 3, maxX, maxY, minZ, uMax, vMin);
            }
        }

        quad.setSprite(icon);
        quad.setLightFace(fromForgeDir(side));
        quad.setHasAmbientOcclusion(true);
        quad.setDirectionalShading(true);
        quad.setColorIndex(-1);
    }

    private static void setVertex(ModelQuadViewMutable quad, int index, float x, float y, float z, float u, float v) {
        quad.setX(index, x);
        quad.setY(index, y);
        quad.setZ(index, z);
        quad.setTexU(index, u);
        quad.setTexV(index, v);
    }
}
