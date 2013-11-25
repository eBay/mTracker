package com.ebay.build.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailArrayValidator implements
		ConstraintValidator<EmailArray, String[]> {

	@Override
	public void initialize(EmailArray constraintAnnotation) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isValid(String[] emails, ConstraintValidatorContext context) {
		boolean valid = true;
		Pattern pattern;
		Matcher matcher;
	
//		final String EMAIL_PATTERN = 
//				"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
//				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		
		final String EMAIL_PATTERN = 
				"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})"
				+ "(;\\s*[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" 
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,}))*$";
						
		pattern = Pattern.compile(EMAIL_PATTERN);

		for (String email : emails) {
			matcher = pattern.matcher(email);
			valid = matcher.matches();
			if (!valid) {				
				break;
			}
		}
		return valid;
	}

}
