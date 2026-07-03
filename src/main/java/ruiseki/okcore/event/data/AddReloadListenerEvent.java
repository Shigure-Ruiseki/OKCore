package ruiseki.okcore.event.data;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import cpw.mods.fml.common.eventhandler.Event;
import ruiseki.okcore.data.PreparableReloadListener;

public class AddReloadListenerEvent extends Event {

    private final List<PreparableReloadListener> listeners = new ArrayList<>();

    public AddReloadListenerEvent() {}

    public void addListener(PreparableReloadListener listener) {
        listeners.add(listener);
    }

    public List<PreparableReloadListener> getListeners() {
        return ImmutableList.copyOf(listeners);
    }
}
