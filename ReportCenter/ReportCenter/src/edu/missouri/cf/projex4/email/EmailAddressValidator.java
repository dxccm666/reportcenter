package edu.missouri.cf.projex4.email;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.vaadin.data.validator.AbstractValidator;

@SuppressWarnings("serial")
public class EmailAddressValidator extends AbstractValidator<String> {

	public EmailAddressValidator(String errorMessage) {
		super(errorMessage);
	}

	@Override
	protected boolean isValidValue(String value) {

		boolean result = true;
		if (value != null && !"".equals(value)) {
			String[] emailAddresses = value.trim().split(";");
			for (String address : emailAddresses) {

				String[] parts = address.trim().split("@");
				if (parts.length != 2) {
					return false;
				} else {
					try {
						InternetAddress emailAddr = new InternetAddress(address);
					} catch (AddressException ex) {
						return false;
					}
				}
			}
		}
		return result;
	}

	@Override
	public Class<String> getType() {
		return String.class;
	}

}
