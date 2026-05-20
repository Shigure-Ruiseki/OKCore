package ruiseki.okcore.data.loader.conditional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ruiseki.okcore.json.AbstractJsonMaterial;

/**
 * Marks a class as a conditional logic provider for the OKCore data loading system.
 * <p>
 * Any class annotated with {@code @LoadCondition} **must** extend {@link AbstractJsonMaterial}.
 * This ensures that the class can participate in the JSON parsing lifecycle,
 * property capturing, and validation flow of the system.
 * <p>
 * This annotation is used by the {@code LoadConditionHandler} to register and
 * instantiate the condition during runtime when encountering the condition key
 * in a JSON file.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoadCondition {

    /**
     * The unique identifier for this condition (e.g., "okcore:mod_loaded").
     * This key serves as the type discriminator in the JSON configuration.
     *
     * @return The condition key string.
     */
    String value();
}
