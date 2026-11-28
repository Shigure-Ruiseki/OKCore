package ruiseki.okcore.client.model.state;

import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhlib.client.model.BakeData;
import com.gtnewhorizon.gtnhlib.client.model.JSONVariant;
import com.gtnewhorizon.gtnhlib.client.model.Weighted;
import com.gtnewhorizon.gtnhlib.client.model.baked.BakedModel;
import com.gtnewhorizon.gtnhlib.client.model.unbaked.UnbakedModel;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectList;
import ruiseki.okcore.block.state.IBlockState;
import ruiseki.okcore.client.model.baked.MultipartModel;
import ruiseki.okcore.client.model.unbaked.MonopartDough;

public class MultipartState implements IStateModelMap, UnbakedModel {

    private final ObjectList<Case> multipart;

    public MultipartState(ObjectList<Case> multipart) {
        this.multipart = multipart;
    }

    @Override
    public @NotNull UnbakedModel selectModel(IBlockState state) {
        return this;
    }

    @Override
    public BakedModel bake(BakeData data) {
        final var bread = new Object2ObjectArrayMap<Case.Condition, BakedModel>(multipart.size());
        for (var c : multipart) {
            bread.put(c.when, new MonopartDough(c.apply).bake());
        }

        return new MultipartModel(bread);
    }

    public static final class Case {

        private final ObjectList<Weighted<JSONVariant>> apply;
        private final Condition when;

        public Case(ObjectList<Weighted<JSONVariant>> apply, Condition when) {
            this.apply = apply;
            this.when = when;
        }

        public ObjectList<Weighted<JSONVariant>> apply() {
            return apply;
        }

        public Condition when() {
            return when;
        }

        public interface Condition {

            Condition TRUE = (Map<String, String> state) -> true;

            boolean matches(Map<String, String> state);
        }

        public static final class MultiCon implements Condition {

            private final boolean requireAll;
            private final ObjectList<Condition> matches;

            public MultiCon(boolean requireAll, ObjectList<Condition> matches) {
                this.requireAll = requireAll;
                this.matches = matches;
            }

            @Override
            public boolean matches(Map<String, String> state) {
                if (requireAll) {
                    for (var m : matches) {
                        if (!m.matches(state)) return false;
                    }
                    return true;
                }

                for (var m : matches) {
                    if (m.matches(state)) return true;
                }
                return false;
            }

            public boolean requireAll() {
                return requireAll;
            }

            public ObjectList<Condition> matches() {
                return matches;
            }
        }

        public static final class StateCon implements Condition {

            private final String key;
            private final ObjectList<String> values;

            public StateCon(String key, ObjectList<String> values) {
                this.key = key;
                this.values = values;
            }

            @Override
            public boolean matches(Map<String, String> state) {
                final var val = state.get(key);
                for (var v : values) {
                    if (v.equals(val)) return true;
                }
                return false;
            }

            public String key() {
                return key;
            }

            public ObjectList<String> values() {
                return values;
            }
        }
    }
}
