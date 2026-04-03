package com.Destinex.app.validation;

import com.Destinex.app.dto.input.SignupRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesConstraintVaildator implements ConstraintValidator<PasswordMatches, SignupRequest> {
    @Override
    public boolean isValid(SignupRequest SignedUser, ConstraintValidatorContext constraintValidatorContext) {
        return SignedUser.getPassword().equals(SignedUser.getConfirmPassword());
    }
}
