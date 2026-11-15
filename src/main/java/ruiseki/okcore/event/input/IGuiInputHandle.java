package ruiseki.okcore.event.input;

public interface IGuiInputHandle {

    void okcore$setMouseHandled(boolean handled);

    boolean okcore$isMouseHandled();

    void okcore$setKeyHandled(boolean handled);

    boolean okcore$isKeyHandled();
}
