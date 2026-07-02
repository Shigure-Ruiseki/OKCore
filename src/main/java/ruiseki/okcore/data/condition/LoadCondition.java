package ruiseki.okcore.data.condition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a conditional logic provider for the OKCore data loading system.
 * <p>
 * Any class annotated with {@code @LoadCondition} **must** implements {@link ILoadCondition}.
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

}
