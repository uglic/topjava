package ru.javawebinar.topjava.web.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.javawebinar.topjava.AuthorizedUser;
import ru.javawebinar.topjava.HasId;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.UserService;
import ru.javawebinar.topjava.to.UserTo;
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
        if (target instanceof User) {
            validateEmail((User) target, ((User) target).getEmail(), errors);
        } else if (target instanceof UserTo) {
            validateEmail((UserTo) target, ((UserTo) target).getEmail(), errors);
        }
    }

    private <T extends HasId> void validateEmail(T user, String email, Errors errors) {
        if (errors.getFieldErrors("email").size() == 0) {
            if (email != null) {
                try {
                    User existing = userService.getByEmail(email);
                    if (existing != null) {
                        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                        Integer userId = (auth == null) ? null : ((AuthorizedUser) auth.getPrincipal()).getUserTo().getId();
                        if ((!user.isNew() && !user.getId().equals(existing.getId()))
                                ||
                                (
                                        user.isNew()
                                                && ((auth == null) || (!userId.equals(existing.getId())))
                                )
                        ) {
                            errors.rejectValue("email", "user.validation.duplicateEmail", "User with this email already exists");
                        }
                    }
                } catch (NotFoundException ignored) {
                }
            }
        }
    }
}
