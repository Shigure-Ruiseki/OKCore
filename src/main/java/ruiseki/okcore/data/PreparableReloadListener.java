package ruiseki.okcore.data;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface PreparableReloadListener {

    /**
     * @param barrier      Used to synchronize the execution between preparation and application stages.
     * @param bgExecutor   Executor to perform heavy tasks (e.g., I/O, data parsing).
     * @param gameExecutor Executor to apply data to the game (usually the main thread).
     */
    CompletableFuture<Void> reload(PreparationBarrier barrier, DataManager manager, Executor bgExecutor,
        Executor gameExecutor);

    default String getName() {
        return this.getClass()
            .getSimpleName();
    }

    interface PreparationBarrier {

        <T> CompletableFuture<T> wait(T object);
    }
}
