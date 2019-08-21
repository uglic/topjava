package ru.javawebinar.topjava.web.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import javax.validation.Validation;

@Component
public class SpringHibernateValidatorAdapter extends SpringValidatorAdapter {
    public SpringHibernateValidatorAdapter() {
        super(Validation.buildDefaultValidatorFactory().getValidator());
    }
}
