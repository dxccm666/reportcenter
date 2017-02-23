package edu.missouri.cf.projex4.ui.desktop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.vaadin.ui.Component;

public class ExtendedEditingStateManipulator implements EditingStateManipulator {

	EditingState editingState;

	protected ArrayList<Component> nonEditableComponents = new ArrayList<Component>();
	protected ArrayList<Component> editableComponents = new ArrayList<Component>();
	private ArrayList<EditorComponent> dependentEditorComponents = new ArrayList<EditorComponent>();

	public ExtendedEditingStateManipulator() {
	}

	protected boolean useThreading = false;

	@Override
	public void setEditingState(final EditingState state) {

		java.util.Date start = new java.util.Date();

		editingState = state;

		if (!nonEditableComponents.isEmpty()) {
			for (Component c : nonEditableComponents) {
				if (c != null)
					c.setReadOnly(true);
			}
			System.err.println("Setting nonEditable Components took " + (new java.util.Date().getTime() - start.getTime()) + "ms");
		}

		if (!editableComponents.isEmpty()) {
			for (Component c : editableComponents) {
				switch (state) {
				case ADDING:
				case EDITING:
					c.setReadOnly(false);
					break;

				case READONLY:
				default:
					c.setReadOnly(true);
					break;
				}
			}
			System.err.println("Setting Editable Components took " + (new java.util.Date().getTime() - start.getTime()) + "ms");
		}

		int x = 0;

		if (!dependentEditorComponents.isEmpty()) {

			Collection<Callable<Boolean>> runningThreads = new ArrayList<Callable<Boolean>>();
			ExecutorService executor = Executors.newFixedThreadPool(20);
			
			if (!useThreading) {

				for (EditorComponent e : dependentEditorComponents) {
					e.setEditingState(state);
					System.err.println("Setting dependentEditorComponent " + x++ + "took "
							+ (new java.util.Date().getTime() - start.getTime()) + "ms");
				}

			} else {

				for (final EditorComponent e : dependentEditorComponents) {

					Callable<Boolean> callable = new Callable<Boolean>() {

						@Override
						public Boolean call() throws Exception {
							e.setEditingState(state);
							return null;
						}

					};

					runningThreads.add(callable);
				}

				try {

					List<Future<Boolean>> results = executor.invokeAll(runningThreads);
					executor.shutdown();

				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

			}

			System.err
					.println("Setting dependentEditorComponents took " + (new java.util.Date().getTime() - start.getTime()) + "ms");

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.missouri.cf.projex4.ui.desktop.EditingStateManipulator#
	 * getEditingState ()
	 */
	@Override
	public EditingState getEditingState() {
		return editingState;
	}

	public void addEditableComponent(Component c) {
		editableComponents.add(c);
	}

	public void addNonEditableComponent(Component c) {
		nonEditableComponents.add(c);
	}

	public void addDependentEditorComponent(EditorComponent c) {
		dependentEditorComponents.add(c);
	}

	public ArrayList<EditorComponent> getDependentEditorComponents() {
		return dependentEditorComponents;
	}
	
	public ArrayList<Component> getEditableComponents() {
		return editableComponents;
	}
	
	public ArrayList<Component> getNonEditableComponents() {
		return nonEditableComponents;
	}

	public void clearComponents() {
		editableComponents.clear();
		nonEditableComponents.clear();
		dependentEditorComponents.clear();
	}

}
