package edu.missouri.cf.projex4.ui.desktop;

import org.tepi.filtertable.FilterTable;
import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import edu.missouri.cf.projex4.ui.common.system.DefaultFormEditControls;

@SuppressWarnings("serial")
public class MultiRowEditControls extends DefaultFormEditControls {

	protected AddButton addButton;

	protected FilterTable table;

	public void setTable(FilterTable table) {
		this.table = table;
	}
	
	public AddButton getAddButton() {
		return addButton;
	}

	public void setEditingState(EditingState state) {

		if (addButton != null) {

			switch (state) {

			case ADDING:
			case EDITING:
				addButton.setVisible(true);
				addButton.setEnabled(true);
				if (table != null) {
					table.setEditable(true);
				}
				break;

			case READONLY:
			default:
				addButton.setVisible(false);
				addButton.setEnabled(false);
				if (table != null) {
					table.setEditable(false);
				}
				break;

			}
		}

		super.setEditingState(state);

	}

	public MultiRowEditControls() {
		super();
		addButton = new AddButton();
		addComponent(addButton, 0);
		setEditingState(EditingState.READONLY);
	}

	public class AddButton extends PopupButton {

		public AddButton() {
			setCaption("add ...");
			setDescription("add n rows to table");
			setIcon(new ThemeResource("icons/chalkwork/basic/add_16x16.png"));
			addStyleName("borderless");
			setClickShortcut(KeyCode.A, ModifierKey.ALT);
			
			final Button addOneButton = new Button() {
				{
					setCaption("add 1 row");
					addStyleName("borderless");

					addClickListener(new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {
							addRows(1);
							setPopupVisible(false);
						}
					});
				}
			};

			final Button addTenButton = new Button() {
				{
					setCaption("add 10 rows");
					addStyleName("borderless");

					addClickListener(new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {

							addRows(10);
							setPopupVisible(false);
						}
					});
				}
			};

			final Button addFiftyButton = new Button() {
				{
					setCaption("add 50 rows");
					addStyleName("borderless");

					addClickListener(new Button.ClickListener() {

						@Override
						public void buttonClick(ClickEvent event) {
							addRows(50);
							setPopupVisible(false);
						}
					});
				}
			};

			setContent(new VerticalLayout() {
				{
					addStyleName("buttonopener");
					addComponent(addOneButton);
					addComponent(addTenButton);
					addComponent(addFiftyButton);
				}
			});
		}
	}

	public void addRows(int num) {

	};

}
