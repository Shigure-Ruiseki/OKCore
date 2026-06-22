package ruiseki.okcore.capabilities;

/**
 * Inspired by {@link com.google.common.reflect.TypeToken TypeToken}, use a subclass to capture
 * generic types. Then uses {@link CapabilityTokenSubclass a transformer}
 * to convert that generic into a string returned by {@link #getType}
 * This allows us to know the generic type, without having a hard reference to the
 * class.
 *
 * Example usage:
 * 
 * <pre>
 * 
 * {
 *     &#64;code
 *     public static Capability<IDataHolder> DATA_HOLDER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
 * }
 * </pre>
 *
 */
public abstract class CapabilityToken<T> {

    protected final String getType() {
        throw new RuntimeException("This will be implemented by a transformer");
    }

    @Override
    public String toString() {
        return "CapabilityToken[" + getType() + "]";
    }
}
