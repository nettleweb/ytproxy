package com.nettleweb.runtime;

import java.lang.annotation.*;

/**
 * Indicates that a specific API requires the JNI library to be loaded.
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RequiresNative
public @interface RequiresNative {
}