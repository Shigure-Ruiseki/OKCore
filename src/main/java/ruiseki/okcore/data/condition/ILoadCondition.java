package ruiseki.okcore.data.condition;

import com.google.gson.JsonObject;

public interface ILoadCondition {

    String getID();

    boolean test(JsonObject json);

    default boolean shouldRegisterType() {
        return true;
    }
}
