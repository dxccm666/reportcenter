/**
 * 
 */
package edu.missouri.operations.ui.desktop.filtertable.modulargenerator;

/**
 * @author graumannc
 *
 */
@SuppressWarnings("serial")
public class StandardFilterFieldGenerator extends ModularFilterFieldGenerator {

	/**
	 * @param owner
	 */
	public StandardFilterFieldGenerator(IFilterTable owner) {
		super(owner);
		addHandler(new OracleBooleanFilterFieldHandler());
		addHandler(new OracleNumberFilterFieldHandler());
		addHandler(new OracleDateFilterFieldHandler());
		addHandler(new OracleStringFilterFieldHandler());
		addHandler(new OracleLongStringFilterFieldHandler());
	}

}
