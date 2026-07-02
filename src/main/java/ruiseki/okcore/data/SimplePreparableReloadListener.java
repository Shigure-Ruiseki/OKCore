package ruiseki.okcore.data;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public abstract class SimplePreparableReloadListener<T> implements PreparableReloadListener {

    @Override
    public final CompletableFuture<Void> reload(PreparationBarrier barrier, DataManager manager, Executor bgExecutor,
        Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() -> { return this.prepare(manager); }, bgExecutor)
            .thenCompose(barrier::wait)
            .thenAcceptAsync((data) -> { this.apply(data, manager); }, gameExecutor);
    }

    protected abstract T prepare(DataManager manager);

    protected abstract void apply(T data, DataManager manager);
}
