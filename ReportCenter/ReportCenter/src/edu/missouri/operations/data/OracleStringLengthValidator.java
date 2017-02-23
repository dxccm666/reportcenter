package edu.missouri.operations.data;

@SuppressWarnings("serial")
public class OracleStringLengthValidator extends AbstractOracleStringValidator {

	public OracleStringLengthValidator(String errorMessage) {
		super(errorMessage);
		// TODO Auto-generated constructor stub
	}

	private Integer minLength = null;

	private Integer maxLength = null;

	private boolean allowNull = true;

	/**
	 * Creates a new StringLengthValidator with a given error message and
	 * minimum and maximum length limits.
	 * 
	 * @param errorMessage
	 *            the message to display in case the value does not validate.
	 * @param minLength
	 *            the minimum permissible length of the string or null for no
	 *            limit. A negative value for no limit is also supported for
	 *            backwards compatibility.
	 * @param maxLength
	 *            the maximum permissible length of the string or null for no
	 *            limit. A negative value for no limit is also supported for
	 *            backwards compatibility.
	 * @param allowNull
	 *            Are null strings permissible? This can be handled better by
	 *            setting a field as required or not.
	 */
	public OracleStringLengthValidator(String errorMessage, Integer minLength, Integer maxLength, boolean allowNull) {
		this(errorMessage);
		setMinLength(minLength);
		setMaxLength(maxLength);
		setNullAllowed(allowNull);

	}

	public OracleStringLengthValidator(String errorMessage, Integer maxLength) {
		this(errorMessage);
		setMinLength(new Integer(0));
		setMaxLength(maxLength);
		setNullAllowed(true);
	}

	public OracleStringLengthValidator(String errorMessage, Integer minLength, Integer maxLength) {
		this(errorMessage);
		setMinLength(minLength);
		setMaxLength(maxLength);
		setNullAllowed(true);
	}

	/**
	 * Checks if the given value is valid.
	 * 
	 * @param value
	 *            the value to validate.
	 * @return <code>true</code> for valid value, otherwise <code>false</code>.
	 */
	@Override
	protected boolean isValidValue(OracleString value) {

		if (value == null || value.isEmpty()) {
			return allowNull;
		}

		String v = value.getValue().trim();

		final int len = v.length();
		if ((minLength != null && minLength > -1 && len < minLength) || (maxLength != null && maxLength > -1 && len > maxLength)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns <code>true</code> if null strings are allowed.
	 * 
	 * @return <code>true</code> if allows null string, otherwise
	 *         <code>false</code>.
	 */
	@Deprecated
	public final boolean isNullAllowed() {
		return allowNull;
	}

	/**
	 * Gets the maximum permissible length of the string.
	 * 
	 * @return the maximum length of the string or null if there is no limit
	 */
	public Integer getMaxLength() {
		return maxLength;
	}

	/**
	 * Gets the minimum permissible length of the string.
	 * 
	 * @return the minimum length of the string or null if there is no limit
	 */
	public Integer getMinLength() {
		return minLength;
	}

	/**
	 * Sets whether null-strings are to be allowed. This can be better handled
	 * by setting a field as required or not.
	 */
	@Deprecated
	public void setNullAllowed(boolean allowNull) {
		this.allowNull = allowNull;
	}

	/**
	 * Sets the maximum permissible length of the string.
	 * 
	 * @param maxLength
	 *            the maximum length to accept or null for no limit
	 */
	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}

	/**
	 * Sets the minimum permissible length.
	 * 
	 * @param minLength
	 *            the minimum length to accept or null for no limit
	 */
	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}
}
