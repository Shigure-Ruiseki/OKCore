package ruiseki.okcore.event.input;

public interface IGuiInputHandle {

    void setMouseHandled(boolean handled);

    boolean isMouseHandled();

    void setKeyHandled(boolean handled);

    boolean isKeyHandled();
}
