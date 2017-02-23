package edu.missouri.cf.projex4.ui.desktop;

@SuppressWarnings("serial")
public class ExcelImportException extends Exception {

	private int row;
	private int cell;
	private Object value = null;
	private String cellReference = null;
	
	public ExcelImportException(String message) {
		super(message);
	}

	public ExcelImportException(String message, int row, int cell) {
		super(message);
		this.row = row;
		this.cell = cell;
		this.cellReference = getColName(cell)+row;
	}

	public ExcelImportException(String message, int row, int cell, Object value) {
		super(message);
		this.row = row;
		this.cell = cell;
		this.value = value;
		this.cellReference = getColName(cell)+row;
	}

	private String getColName(int colNum) {

		String res = "";

		int quot = colNum;
		int rem;
		/*
		 * 1. Subtract one from number.2. Save the mod 26 value.3. Divide the
		 * number by 26, save result.4. Convert the remainder to a letter.5.
		 * Repeat until the number is zero.6. Return that bitch...
		 */
		while (quot > 0) {
			quot = quot - 1;
			rem = quot % 26;
			quot = quot / 26;

			// cast to a char and add to the beginning of the string
			// add 97 to convert to the correct ASCII number
			res = (char) (rem + 97) + res;
		}
		return res.toUpperCase();
	}

	public String toString() {
		
		if(value!=null) {
			return "Cell " + getColName(cell) + (row + 1) + " = " + value + " - " + super.getMessage();
		} else {
			return "Cell " + getColName(cell) + (row + 1) + " - " + super.getMessage();
		}

	}

	/**
	 * @return the row
	 */
	public int getRow() {
		return row;
	}

	/**
	 * @param row the row to set
	 */
	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * @return the cell
	 */
	public int getCell() {
		return cell;
	}

	/**
	 * @param cell the cell to set
	 */
	public void setCell(int cell) {
		this.cell = cell;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * @return the cellReference
	 */
	public String getCellReference() {
		return cellReference;
	}

	/**
	 * @param cellReference the cellReference to set
	 */
	public void setCellReference(String cellReference) {
		this.cellReference = cellReference;
	}

}