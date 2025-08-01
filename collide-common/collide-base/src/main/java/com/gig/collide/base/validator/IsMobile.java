package com.gig.collide.base.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 是否手机号校验注解
 *
 * @author GIGOpenTeam
 */
@Constraint(validatedBy = MobileValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsMobile {
    String message() default "格式不正确"; // 默认错误信息

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
