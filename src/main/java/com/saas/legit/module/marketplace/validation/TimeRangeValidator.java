package com.saas.legit.module.marketplace.validation;

import com.saas.legit.module.marketplace.dto.ScheduleRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class TimeRangeValidator implements ConstraintValidator<ValidTimeRange, ScheduleRequest> {

    @Override
    public boolean isValid(ScheduleRequest request, ConstraintValidatorContext context) {
        if (request.startTime() == null || request.endTime() == null) {
            return true;
        }

        try {
            LocalTime start = LocalTime.parse(request.startTime());
            LocalTime end   = LocalTime.parse(request.endTime());
            return start.isBefore(end);
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}