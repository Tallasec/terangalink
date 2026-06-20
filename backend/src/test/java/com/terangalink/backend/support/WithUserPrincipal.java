package com.terangalink.backend.support;

import com.terangalink.backend.enums.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithUserPrincipalSecurityContextFactory.class)
public @interface WithUserPrincipal {

    long id() default 1L;

    Role role() default Role.USER;
}
