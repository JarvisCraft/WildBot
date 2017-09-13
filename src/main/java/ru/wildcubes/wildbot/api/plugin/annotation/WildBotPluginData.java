package ru.wildcubes.wildbot.api.plugin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface WildBotPluginData {
    ///////////////////////////////////////////////////////////////////////////
    // Main Plugin Data
    ///////////////////////////////////////////////////////////////////////////
    String name();
    String version();
    String[] authors();

    ///////////////////////////////////////////////////////////////////////////
    // Plugin loading
    ///////////////////////////////////////////////////////////////////////////
    String[] dependencies() default {};
    String[] softDependencies() default {};
    String[] loadBefore() default {};
    String[] commands() default {};

    ///////////////////////////////////////////////////////////////////////////
    // Plugin Additional Info
    ///////////////////////////////////////////////////////////////////////////
    String website() default "";
    String license() default "";
}