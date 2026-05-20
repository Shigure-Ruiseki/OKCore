package ruiseki.okcore.data.loader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a custom data loader for the OKCore data system.
 * <p>
 * Classes annotated with {@code @DataLoader} are automatically discovered
 * during the initialization phase (via ASM/Classpath scanning). Once discovered,
 * these loaders are instantiated and registered into the system, allowing them
 * to process their respective JSON data categories defined by {@link IDataLoader#getTargetFolder()}.
 * <p>
 * Ensure that any class using this annotation implements the {@link IDataLoader} interface.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataLoader {}
