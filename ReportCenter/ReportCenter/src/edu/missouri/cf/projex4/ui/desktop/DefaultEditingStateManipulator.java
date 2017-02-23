/**
 * 
 */
package edu.missouri.cf.projex4.ui.desktop;

/**
 * @author graumannc
 *
 */
public class DefaultEditingStateManipulator implements EditingStateManipulator {

	EditingState editingState;
	
	/**
	 * 
	 */
	public DefaultEditingStateManipulator() {
	}

	/* (non-Javadoc)
	 * @see edu.missouri.cf.projex4.ui.desktop.EditingStateManipulator#getEditingState()
	 */
	@Override
	public EditingState getEditingState() {
		return editingState;
	}

	/* (non-Javadoc)
	 * @see edu.missouri.cf.projex4.ui.desktop.EditingStateManipulator#setEditingState(edu.missouri.cf.projex4.ui.desktop.EditingStateManipulator.EditingState)
	 */
	@Override
	public void setEditingState(EditingState mode) {
		this.editingState = mode;
	}

}
