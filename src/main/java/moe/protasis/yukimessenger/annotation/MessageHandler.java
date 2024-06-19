package moe.protasis.yukimessenger.annotation;

import moe.protasis.yukimessenger.message.MessageDestination;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageHandler {
    String value();
    MessageDestination destination() default MessageDestination.BOTH;
}
