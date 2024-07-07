package com.attica.athens.support.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.core.annotation.AliasFor;
import org.springframework.security.test.context.support.WithUserDetails;

@Retention(RetentionPolicy.RUNTIME)
@WithUserDetails(userDetailsServiceBeanName = "testCustomUserDetailsService")
public @interface WithMockCustomUser {
    @AliasFor(annotation = WithUserDetails.class, attribute = "value")
    String value() default "EnvironmentalActivist";
}
