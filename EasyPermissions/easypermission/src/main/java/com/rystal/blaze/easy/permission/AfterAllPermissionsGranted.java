package com.rystal.blaze.easy.permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * com.rystal.blaze.easy.permission
 * Time: 2019/3/19 13:49
 * Description:After all permissions granted,we can do something.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AfterAllPermissionsGranted {
    int value();
}
