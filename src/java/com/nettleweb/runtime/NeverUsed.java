package com.nettleweb.runtime;

import java.lang.annotation.*;

/**
 * Indicates that a reference that is never used within this project.
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RequiresNative
public @interface NeverUsed {
}