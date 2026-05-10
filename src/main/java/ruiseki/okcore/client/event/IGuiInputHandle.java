package ruiseki.okcore.client.event;

public interface IGuiInputHandle {

    void setMouseHandled(boolean handled);

    boolean isMouseHandled();

    void setKeyHandled(boolean handled);

    boolean isKeyHandled();
}
