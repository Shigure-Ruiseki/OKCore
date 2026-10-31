package ruiseki.okcore.datastructure;

/**
 * Provides a unique runtime identifier for an object, used together with
 * metadata to construct a packed {@code long} key for efficient lookups.
 *
 * <p>
 * The unique ID identifies the object itself, while the metadata
 * distinguishes individual variants of that object. Both values can be
 * packed into a single {@code long} to avoid object allocation and enable
 * efficient primitive-key lookups in collections.
 *
 * <p>
 * Unique IDs are only guaranteed to be unique within the current runtime.
 * They are not guaranteed to remain stable across game restarts and must
 * not be used for persistent storage or network synchronization.
 */
public interface IUniqueIndex {

    /**
     * Returns the unique runtime identifier assigned to this object.
     *
     * @return the unique runtime identifier used for packed {@code long} keys
     */
    int getUniqueID();
}
