package br.com.fiap.mscustomers.entity.validation;

import br.com.fiap.mscustomers.entity.Customer;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZipCodeValidation implements ConstraintValidator<ValidZipCode, Customer> {
	@Override
	public boolean isValid(Customer customer, ConstraintValidatorContext context) {
		//00000-000
		if(customer.getZipCode().length() != 9) return Boolean.FALSE;

		Pattern regex = Pattern.compile("^\\d{5}-\\d{3}$"); // 00000-000
		Matcher matcher = regex.matcher(customer.getZipCode());

		return matcher.matches();
	}
}
