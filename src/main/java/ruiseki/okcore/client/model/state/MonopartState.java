package ruiseki.okcore.client.model.state;

import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonParseException;
import com.gtnewhorizon.gtnhlib.client.model.JSONVariant;
import com.gtnewhorizon.gtnhlib.client.model.Weighted;
import com.gtnewhorizon.gtnhlib.client.model.unbaked.MissingModel;
import com.gtnewhorizon.gtnhlib.client.model.unbaked.UnbakedModel;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import ruiseki.okcore.block.state.IBlockState;
import ruiseki.okcore.client.model.unbaked.MonopartDough;

public class MonopartState implements IStateModelMap {

    private final Object2ObjectMap<StateMatch, ObjectList<Weighted<JSONVariant>>> variants;

    public MonopartState(Object2ObjectMap<StateMatch, ObjectList<Weighted<JSONVariant>>> variants) {
        this.variants = variants;
    }

    @Override
    public @NotNull UnbakedModel selectModel(IBlockState state) {
        Map<String, String> properties = state.toMap();

        final var iter = Object2ObjectMaps.fastIterator(variants);
        while (iter.hasNext()) {
            final var e = iter.next();
            final var match = e.getKey();
            if (match.matches(properties)) {
                return new MonopartDough(e.getValue());
            }
        }
        return MissingModel.MISSING_MODEL;
    }

    public static class StateMatch {

        private final String variantName;
        private final boolean matchAll;
        private final Object2ObjectMap<String, String> states;

        public StateMatch(String s) {
            variantName = s;

            // "normal" is the vanilla key for a block with no properties
            if (s.isEmpty() || "normal".equals(s)) {
                matchAll = true;
                states = null;
                return;
            }

            matchAll = false;
            states = new Object2ObjectOpenHashMap<>();

            for (String c : s.split(",")) {
                final int eqIndex = c.indexOf("=");

                if (eqIndex == -1) throw new JsonParseException(
                    "Model variant predicate is missing an equals; expected it to be of the format 'property name=property value': '"
                        + c
                        + "'!");

                states.put(c.substring(0, eqIndex), c.substring(eqIndex + 1));
            }
        }

        public String getVariantName() {
            return variantName;
        }

        public boolean matches(Map<String, String> properties) {
            if (matchAll) return true;

            final var iter = Object2ObjectMaps.fastIterator(states);
            while (iter.hasNext()) {
                final var e = iter.next();
                if (!Objects.equals(properties.get(e.getKey()), e.getValue())) return false;
            }

            return true;
        }
    }
}
