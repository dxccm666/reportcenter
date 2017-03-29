package edu.missouri.cf.projex4.ui.desktop;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Button.ClickEvent;

import edu.missouri.operations.ui.desktop.buttons.NavigatorButton;

/**
 * This is implemented on the navigator opener button. This is the default click
 * listener on the opener button if no TableClickListener is set. If you would
 * like to specify which TableClickListener an individual NavigatorButton uses,
 * you can do so on the
 * {@link NavigatorOpenerButton#addComponent(Enum, String, DefaultTableClickListener)}
 * method. The {@link #setUriFragments(Item)} function should be the only
 * function that needs to be overridden.
 * 
 * @author reynoldsjj
 * 
 */
@SuppressWarnings("serial")
public class DefaultTableClickListener implements TableClickListener {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	protected NavigatorButton navigatorButton;

	public DefaultTableClickListener() {

	}

	public DefaultTableClickListener(NavigatorButton navigatorButton) {
		this.navigatorButton = navigatorButton;
	}

	public void setNavigatorButton(NavigatorButton navigatorButton) {
		this.navigatorButton = navigatorButton;
	}

	/**
	 * Handles what happens when a NavigatorButton is clicked. Such as
	 * displaying a notification, "select a value", if the
	 * {@link com.vaadin.ui.Table#getValue()} is null
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public void buttonClick(ClickEvent event) {

		if (logger.isDebugEnabled()) {
			logger.debug("button clicked Alt {} Ctrl {} Meta {} Shift {}", event.isAltKey(), event.isCtrlKey(), event.isMetaKey(),
					event.isShiftKey());
		}

		if (navigatorButton.getView() != null) {

			String caption = navigatorButton.getTable().getCaption();

			if (navigatorButton.getTable() != null) {

				if (!event.isCtrlKey()) {

					String id = null;

					if (navigatorButton.getTable().isMultiSelect()) {

						if (navigatorButton.getTable().getValue() instanceof Collection<?>) {

							Collection<?> col = (Collection<?>) navigatorButton.getTable().getValue();

							if (col.isEmpty()) {
								Notification.show("Select a value in the table!");
								return;
							}

							id = col.iterator().next().toString();
						}

					} else {

						if (navigatorButton.getTable().getValue() != null) {
							id = navigatorButton.getTable().getValue().toString();
						} else {
							Notification.show("Select a value in the table!");
							return;
						}

					}

					Item selectedItem = navigatorButton.getTable().getItem(new RowId(new Object[] { id }));

					if (selectedItem == null) {

						Notification.show("Selected value is no longer in the table!");
						return;

					}

					Collection<?> itemProperties = selectedItem.getItemPropertyIds();

					String itemid = null;
					if (itemProperties.contains("UUID")) {
						itemid = selectedItem.getItemProperty("UUID").getValue().toString();
					} else if (caption != null && caption.equals("objectClasses") && itemProperties.contains("OBJECTCLASS")) {
						itemid = selectedItem.getItemProperty("OBJECTCLASS").getValue().toString();
					} else if (itemProperties.contains("APPLICATIONRIGHTID")) {
						itemid = selectedItem.getItemProperty("APPLICATIONRIGHTID").getValue().toString();
					} else if (itemProperties.contains("ID")) {
						itemid = selectedItem.getItemProperty("ID").getValue().toString();
					}
					navigatorButton.setUriFragment(itemid);
					logger.debug("itemid = " + itemid);
					navigatorButton.navigateTo(navigatorButton.getUriFragment());

				} else {

					Collection<?> col = null;
					if (navigatorButton.getTable().isMultiSelect()) {

						if (navigatorButton.getTable().getValue() instanceof Collection<?>) {

							col = (Collection<?>) navigatorButton.getTable().getValue();
							if (col.isEmpty()) {
								Notification.show("Select a value in the table!");
								return;
							}

						}

					} else {

						if (navigatorButton.getTable().getValue() != null) {
							col = new ArrayList<String>();
							((ArrayList<String>) col).add(navigatorButton.getTable().getValue().toString());
						} else {
							Notification.show("Select a value in the table!");
							return;
						}

					}

					for (Object id : col) {

						Item selectedItem = navigatorButton.getTable().getItem(id);
						if (selectedItem != null) {

							Collection<?> itemProperties = selectedItem.getItemPropertyIds();

							String itemid = null;
							if (itemProperties.contains("UUID")) {
								itemid = selectedItem.getItemProperty("UUID").getValue().toString();
							} else if (caption != null && caption.equals("objectClasses") && itemProperties.contains("OBJECTCLASS")) {
								itemid = selectedItem.getItemProperty("OBJECTCLASS").getValue().toString();
							} else if (itemProperties.contains("APPLICATIONRIGHTID")) {
								itemid = selectedItem.getItemProperty("APPLICATIONRIGHTID").getValue().toString();
							} else if (itemProperties.contains("ID")) {
								itemid = selectedItem.getItemProperty("ID").getValue().toString();
							}
							navigatorButton.setUriFragment(itemid);
							logger.debug("itemid = " + itemid);
							Page.getCurrent().open(new ExternalResource(navigatorButton.getUrl(navigatorButton.getUriFragment())),
									"_blank", false);

						}

					}

				}

			}

		}
	}

	/**
	 * Handles how a uriFragment is set when the NavigatorButton is clicked.
	 * 
	 * @param selectedItem
	 */
	@Deprecated
	public void setUriFragments(Item selectedItem) {
		String uuid = selectedItem.getItemProperty("UUID").getValue().toString();
		navigatorButton.setUriFragment(uuid);
		logger.debug("uuid = " + uuid);
	}
};
