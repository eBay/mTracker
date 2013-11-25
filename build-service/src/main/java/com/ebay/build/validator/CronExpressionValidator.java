package com.ebay.build.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CronExpressionValidator implements
        ConstraintValidator<CronExpression, String> {
 
	@Override
    public void initialize(CronExpression cronExpression) {
    }
 
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return org.quartz.CronExpression.isValidExpression(s);
    }

}