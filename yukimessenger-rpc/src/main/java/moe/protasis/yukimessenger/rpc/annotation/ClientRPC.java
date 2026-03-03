package moe.protasis.yukimessenger.rpc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ClientRPC {
    /**
     * Whether to allow the method to be called locally. If false, the method can only be called remotely; local attempts to call the method will throw <code>LocalCallNotPermittedException</code>.
     * @return Whether to allow the method to be called locally.
     * @see moe.protasis.yukimessenger.rpc.exception.LocalCallNotPermittedException
     */
    boolean allowLocalCall() default false;
}
