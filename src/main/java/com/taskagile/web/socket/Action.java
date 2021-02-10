package com.taskagile.web.socket;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Action {

  /**
   * The action pattern. It needs to be an exact match.
   * <p>For example, "subscribe"
   */
  String value() default "";
}
