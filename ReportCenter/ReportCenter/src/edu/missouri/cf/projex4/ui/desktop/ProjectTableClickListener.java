package edu.missouri.cf.projex4.ui.desktop;

import com.vaadin.data.Item;

@SuppressWarnings("serial")
public class ProjectTableClickListener extends DefaultTableClickListener {
	@Override
	public void setUriFragments(Item selectedItem) {
		String uuid = selectedItem.getItemProperty("UUID").getValue().toString();
		String projectuuid = selectedItem.getItemProperty("PROJECTUUID").getValue().toString();
		navigatorButton.setUriFragment(uuid, projectuuid);
		logger.debug("fragments = {}", navigatorButton.getUriFragment());
	}
}
