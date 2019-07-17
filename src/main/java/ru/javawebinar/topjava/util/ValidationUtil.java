package ru.javawebinar.topjava.util;


import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import ru.javawebinar.topjava.model.AbstractBaseEntity;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import javax.validation.ConstraintViolation;
import java.util.Set;

public class ValidationUtil {

    private ValidationUtil() {
    }

    public static <T> T checkNotFoundWithId(T object, int id) {
        return checkNotFound(object, "id=" + id);
    }

    public static void checkNotFoundWithId(boolean found, int id) {
        checkNotFound(found, "id=" + id);
    }

    public static <T> T checkNotFound(T object, String msg) {
        checkNotFound(object != null, msg);
        return object;
    }

    public static void checkNotFound(boolean found, String msg) {
        if (!found) {
            throw new NotFoundException("Not found entity with " + msg);
        }
    }

    public static void checkNew(AbstractBaseEntity entity) {
        if (!entity.isNew()) {
            throw new IllegalArgumentException(entity + " must be new (id=null)");
        }
    }

    public static void assureIdConsistent(AbstractBaseEntity entity, int id) {
//      conservative when you reply, but accept liberally (http://stackoverflow.com/a/32728226/548473)
        if (entity.isNew()) {
            entity.setId(id);
        } else if (entity.getId() != id) {
            throw new IllegalArgumentException(entity + " must be with id=" + id);
        }
    }

    //  http://stackoverflow.com/a/28565320/548473
    public static Throwable getRootCause(Throwable t) {
        Throwable result = t;
        Throwable cause;

        while (null != (cause = result.getCause()) && (result != cause)) {
            result = cause;
        }
        return result;
    }

    public static void addIfViolateNonBlank(String value, String field, Set<ConstraintViolation<?>> violations) {
        if (value == null || value.isBlank()) {
            violations.add(getSimpleViolation(field + " must not be blank", value));
        }
    }

    public static void addIfViolateMinLength(String value, String field, Set<ConstraintViolation<?>> violations, int minLength) {
        if (value != null && value.length() < minLength) {
            violations.add(getSimpleViolation(field + " length must be not less then " + minLength, value));
        }
    }

    public static void addIfViolateMaxLength(String value, String field, Set<ConstraintViolation<?>> violations, int maxLength) {
        if (value != null && value.length() > maxLength) {
            violations.add(getSimpleViolation(field + " length must be not greater then " + maxLength, value));
        }
    }

    public static void addIfViolateMinValue(int value, String field, Set<ConstraintViolation<?>> violations, int minValue) {
        if (value < minValue) {
            violations.add(getSimpleViolation(field + " must be not less then " + minValue, value));
        }
    }

    public static void addIfViolateMaxValue(int value, String field, Set<ConstraintViolation<?>> violations, Integer maxValue) {
        if (value > maxValue) {
            violations.add(getSimpleViolation(field + " must be not greater then " + maxValue, value));
        }
    }

    public static void addIfViolateNotNull(Object value, String field, Set<ConstraintViolation<?>> violations) {
        if (value == null) {
            violations.add(getSimpleViolation(field + " must not be null", value));
        }
    }

    private static ConstraintViolation<User> getSimpleViolation(String message, Object value) {
        return ConstraintViolationImpl.forParameterValidation(
                message,
                null, null, null,
                null, null, null, value,
                null, null, null, null,
                null);
    }
}