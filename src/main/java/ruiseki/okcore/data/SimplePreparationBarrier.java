package ruiseki.okcore.data;

import java.util.concurrent.CompletableFuture;

public class SimplePreparationBarrier implements PreparableReloadListener.PreparationBarrier {

    @Override
    public <T> CompletableFuture<T> wait(T object) {
        return CompletableFuture.completedFuture(object);
    }
}
