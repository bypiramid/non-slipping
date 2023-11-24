package net.bypiramid.commandmanager.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {
    /**
     * Root command names
     */
    String[] names();

    /**
     * Command's permission
     */
    String permission() default "";

    /**
     * Weather or not the command is ran asynchronously
     */
    boolean async() default false;

    /**
     * Gets the description of the command
     */
    String description() default "No description provided for this command.";

    /**
     * Checks if command is player only
     */
    boolean playerOnly() default false;
}
