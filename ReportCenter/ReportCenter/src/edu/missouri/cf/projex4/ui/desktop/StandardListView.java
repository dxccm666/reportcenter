package edu.missouri.cf.projex4.ui.desktop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;

/**
 * StandardListView - all screens which just list should extend this one.
 * 
 * @author graumannc
 * 
 */
@SuppressWarnings("serial")
public abstract class StandardListView extends TopBarView {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	boolean allowswitch = true;
	
	public StandardListView() {
		super();
	}
	
	private ObjectData objectData;
	
	public ObjectData getObjectData() {
		return objectData;
	}
	
	public void setObjectData(ObjectData objectData) {
		this.objectData = objectData;
	}

	@Override
	public void enter(ViewChangeEvent event) {
		reportsLink.resetResource();
		helpLink.resetResource();
		setScreenData(event.getParameters());
	}

	@Override
	public abstract void setScreenData(String parameters);

}
