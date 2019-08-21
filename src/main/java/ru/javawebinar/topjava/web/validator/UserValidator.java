package ru.javawebinar.topjava.web.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.UserService;
import ru.javawebinar.topjava.to.UserTo;
import ru.javawebinar.topjava.util.UserUtil;
import ru.javawebinar.topjava.util.exception.NotFoundException;

@Component
public class UserValidator implements Validator {

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(User.class) || aClass.equals(UserTo.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (target == null) {
            return;
        }
        User user;
        if (target instanceof User) {
            user = (User) target;
        } else if (target instanceof UserTo) {
            user = UserUtil.createNewFromTo((UserTo) target);
        } else {
            return;
        }
        if (errors.getFieldErrors("email").size() == 0) {
            String email = user.getEmail();
            if (email != null) {
                try {
                    User existing = userService.getByEmail(email);
                    if (existing != null) {
                        if (!existing.equals(user) || (
                                (target instanceof UserTo) && ((UserTo) target).getId().equals(existing.getId())
                        )) {
                            errors.rejectValue("email", "user.validation.duplicateEmail", "User with this email already exists");
                        }
                    }
                } catch (NotFoundException ignored) {
                }
            }
        }
    }
}
