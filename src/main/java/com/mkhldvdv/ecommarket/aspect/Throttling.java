package com.mkhldvdv.ecommarket.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Throttling {

    /**
     * throttling requests count
     * @return requests count
     */
    int requestCount() default -1;

    /**
     * throttling period in minutes
     * @return period in minutes
     */
    int retentionPeriodInMinutes() default -1;

}
