package edu.missouri.operations.data;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public final class Formatter {

	/*
	 * These are not thread safe and not localized properly -- need to be
	 * replaced
	 */

	// public final static DecimalFormat CURRENCY = new DecimalFormat("$###,###,###,###,##0.00");
	// public final static DecimalFormat DECIMAL = new DecimalFormat("###,###,###,###,##0.00");
	// public final static DecimalFormat INTEGER = new DecimalFormat("##############0");
	// public final static DecimalFormat SHORTPERCENT = new DecimalFormat("##0.00");
	// public final static DecimalFormat LONGPERCENT = new DecimalFormat("##0.000000");
	
	public final static SimpleDateFormat DATE = new SimpleDateFormat("MM/dd/yyyy");
	public final static SimpleDateFormat TIMESTAMP = new SimpleDateFormat("MM/dd/yyyy hh:mm aaa");
	public final static FilesizeFormat FILESIZE = new FilesizeFormat(false);

	public static NumberFormat getCurrencyFormat() {

		NumberFormat nf = null;
		nf = NumberFormat.getCurrencyInstance(Locale.US);
		nf.setMinimumIntegerDigits(1);
		nf.setMaximumIntegerDigits(15);
		nf.setMaximumFractionDigits(2);
		((java.text.DecimalFormat) nf).setParseBigDecimal(true);
		return nf;

	}

	public static NumberFormat getDecimalFormat() {
		NumberFormat nf = null;
		nf = NumberFormat.getInstance(Locale.US);
		nf.setMinimumIntegerDigits(1);
		nf.setMaximumIntegerDigits(15);
		nf.setMaximumFractionDigits(2);
		return nf;
	}

	public static NumberFormat getIntegerFormat() {
		
		NumberFormat nf = null;
		nf = NumberFormat.getIntegerInstance(Locale.US);
		nf.setMaximumIntegerDigits(15);
		return nf;

	}

	public static NumberFormat getLongPercentFormat() {
		
		NumberFormat nf = null;
		nf = NumberFormat.getPercentInstance(Locale.US);
		nf.setMaximumFractionDigits(6);
		nf.setMinimumFractionDigits(2);
		nf.setMinimumIntegerDigits(1);
		return nf;

	}

	public static NumberFormat getShortPercentFormat() {
		NumberFormat nf = null;
		nf = NumberFormat.getPercentInstance(Locale.US);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);
		nf.setMinimumIntegerDigits(1);
		return nf;
	}

}
