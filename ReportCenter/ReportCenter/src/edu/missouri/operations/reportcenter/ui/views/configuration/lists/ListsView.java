package edu.missouri.operations.reportcenter.ui.views.configuration.lists;

import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.operations.reportcenter.ui.TopBarView;
import edu.missouri.operations.ui.StandardTable;
import edu.missouri.operations.ui.desktop.buttons.AddButton;
import edu.missouri.operations.ui.desktop.buttons.DeleteButton;
import edu.missouri.operations.ui.desktop.buttons.EditButton;

public class ListsView extends TopBarView {

	private StandardTable properties;
	private StandardTable listvalues;
	private AddButton addButton;
	private EditButton editButton;
	private DeleteButton deleteButton;

	public ListsView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void attach() {
		super.attach();
		init();
	}

	private void init() {

		properties = new StandardTable() {
			{

			}
		};

		listvalues = new StandardTable() {
			{

			}
		};

		addButton = new AddButton() {
			{
			}
		};

		editButton = new EditButton() {
			{
			}
		};

		deleteButton = new DeleteButton() {
			{
			}
		};

		addInnerComponent(new VerticalLayout() {
			{
				setSizeFull();
				setMargin(true);
				addComponent(new Label("System Lists", ContentMode.HTML) {
					{
						addStyleName("maintitle");
					}
				});
				addComponent(new Label("<b>Lists</b>", ContentMode.HTML) {
					{
					}
				});
				addComponent(properties);
				setExpandRatio(properties, 0.25f);
				addComponent(new Label("<b>List Values</b>", ContentMode.HTML) {
					{
					}
				});
				addComponent(new HorizontalLayout() {
					{
						addComponent(addButton);
						addComponent(editButton);
						addComponent(deleteButton);
					}
				});
				addComponent(listvalues);
				setExpandRatio(listvalues, 0.75f);

			}
		});

	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}

}
