package com.taskagile.web.socket;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ChannelHandler {

  /**
   * Channel patter, alias of value()
   */
  String pattern() default "";

  /**
   * The channel pattern that the handler will be mapped to by {@link WebSocketRequestDispatcher}
   * using Spring's {@link org.springframework.util.AntPathMatcher}
   */
  String value() default "";

}
