package project.board.controller.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import project.board.annotation.validation.PasswordPattern;

public class PasswordPatternValidator implements ConstraintValidator<PasswordPattern, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return false;
        }
        return value.matches("(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{12,20}");
    }
}
