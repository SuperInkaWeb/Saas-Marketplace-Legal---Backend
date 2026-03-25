package com.saas.legit.module.marketplace.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TimeRangeValidator.class)
public @interface ValidTimeRange {
    String message() default "La hora de inicio debe ser anterior a la hora de fin";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}