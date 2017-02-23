/**
 * 
 */
package edu.missouri.cf.projex4.ui.desktop;

/**
 * @author graumannc
 *
 */
public interface EditingStateManipulator {
	
	public enum EditingState { READONLY, EDITING, ADDING }
	
	public EditingState getEditingState();
	public void setEditingState(EditingState mode);
	
}
