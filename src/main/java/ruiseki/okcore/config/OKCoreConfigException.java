package ruiseki.okcore.config;

/**
 * Exceptions that can occur when configuring this mod.
 * 
 * @author rubensworks
 *
 */
public class OKCoreConfigException extends RuntimeException {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Make a new instance.
     * 
     * @param message The message.
     */
    public OKCoreConfigException(String message) {
        super(message);
    }

}
