/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package ruiseki.okcore.capabilities;

import java.util.concurrent.Callable;

import org.jetbrains.annotations.Nullable;

/**
 * This is the core holder object Capabilities.
 * Each capability will have ONE instance of this class,
 * and it will the the one passed into the ICapabilityProvider functions.
 *
 * The CapabilityManager is in charge of creating this class.
 */
public class Capability<T> {

    /**
     * @return The unique name of this capability, typically this is
     *         the fully qualified class name for the target interface.
     */
    public String getName() {
        return name;
    }

    /**
     * A NEW instance of the default implementation.
     *
     * If it important to note that if you want to use the default storage
     * you may be required to use this exact implementation.
     * Refer to the owning API of the Capability in question.
     *
     * @return A NEW instance of the default implementation.
     */
    @Nullable
    public T getDefaultInstance() {
        try {
            return this.factory.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Use this inside ICapabilityProvider.getCapability to avoid unchecked cast warnings.
     * Example: return SOME_CAPABILITY.cast(instance);
     * Use with caution;
     */
    @SuppressWarnings("unchecked")
    public <R> R cast(T instance) {
        return (R) instance;
    }

    // INTERNAL
    private final String name;
    private final Callable<? extends T> factory;

    Capability(String name, Callable<? extends T> factory) {
        this.name = name;
        this.factory = factory;
    }

}
