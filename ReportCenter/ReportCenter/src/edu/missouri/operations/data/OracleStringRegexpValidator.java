package edu.missouri.operations.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("serial")
public class OracleStringRegexpValidator extends AbstractOracleStringValidator {

	private Pattern pattern;
	private boolean complete = true;
	private transient Matcher matcher = null;

	public OracleStringRegexpValidator(String regexp, boolean complete, String errorMessage) {
		super(errorMessage);
		pattern = Pattern.compile(regexp);
		this.complete = complete;
	}

	public OracleStringRegexpValidator(String regexp, String errorMessage) {
		this(regexp, true, errorMessage);
	}

	@Override
	protected boolean isValidValue(OracleString value) {
		if (value == null || value.isEmpty()) {
			return true;
		}
		if (complete) {
			return getMatcher(value.toString()).matches();
		} else {
			return getMatcher(value.toString()).find();
		}
	}
	
	private Matcher getMatcher(String value) {
        if (matcher == null) {
            matcher = pattern.matcher(value);
        } else {
            matcher.reset(value);
        }
        return matcher;
    }

}
