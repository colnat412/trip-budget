package com.tripbudget.tripbudget_core.common.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE}) // allow put in param controller
@Retention(RetentionPolicy.RUNTIME) // make sure annotation will be keep when app runs
@Documented
@AuthenticationPrincipal(
    expression = "new com.tripbudget.tripbudget_core.common.dtos.CurrentUserDto(#this)"
)
public @interface CurrentUser {
}