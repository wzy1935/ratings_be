package com.sc.ratings.utils.authaop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target (ElementType.METHOD)
@Retention (RetentionPolicy.RUNTIME)
public @interface Auth {
    Type type() default Type.BASE;

    enum Type {
        BASE,
        USER,
        ADMIN
    }
}