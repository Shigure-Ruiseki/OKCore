package ruiseki.okcore.datastructure;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import ruiseki.okcore.OKCore;

public class ServerGameExecutor implements Executor {

    private static final Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();

    @Override
    public void execute(Runnable command) {
        tasks.add(command);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Runnable task;
            while ((task = tasks.poll()) != null) {
                try {
                    task.run();
                } catch (Exception e) {
                    OKCore.okLog(Level.ERROR, "Error executing scheduled server task", e);
                }
            }
        }
    }
}
