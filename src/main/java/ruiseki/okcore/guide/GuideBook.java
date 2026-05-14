package ruiseki.okcore.guide;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cpw.mods.fml.common.eventhandler.EventPriority;

/**
 * Used to mark a class to be handled for Guide registration.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GuideBook {

    EventPriority priority() default EventPriority.NORMAL;
}
