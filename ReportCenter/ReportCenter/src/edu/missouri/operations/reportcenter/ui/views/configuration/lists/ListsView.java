package edu.missouri.operations.reportcenter.ui.views.configuration.lists;

import java.sql.SQLException;

import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

import edu.missouri.operations.reportcenter.data.ListItems;
import edu.missouri.operations.reportcenter.data.Lists;
import edu.missouri.operations.reportcenter.ui.TopBarView;
import edu.missouri.operations.ui.StandardTable;
import edu.missouri.operations.ui.TableColumn;
import edu.missouri.operations.ui.desktop.buttons.AddButton;
import edu.missouri.operations.ui.desktop.buttons.DeleteButton;
import edu.missouri.operations.ui.desktop.buttons.EditButton;

public class ListsView extends TopBarView {

	private StandardTable listtable;
	private StandardTable listvalues;
	private AddButton addItemButton;
	private EditButton editItemButton;
	private DeleteButton deleteItemButton;
	private ListItems listItems;
	private AddButton addListButton;
	private EditButton editListButton;
	private DeleteButton deleteListButton;

	public ListsView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void attach() {
		super.attach();
		init();
	}

	private void init() {

		listtable = new StandardTable() {
			{
				add(new TableColumn("LISTNAME", "Name").setExpandRatio(0.25f));
				add(new TableColumn("DESCRIPTION", "Description").setExpandRatio(0.75f));
				add(new TableColumn("ISACTIVE", "Active?").setWidth(100));

			}
		};

		listvalues = new StandardTable() {
			{
				add(new TableColumn("DISPLAYORDER","Display Order").setWidth(120));
				add(new TableColumn("SYSTEMVALUE","System Value").setExpandRatio(0.25f));
				add(new TableColumn("VALUE","Value").setExpandRatio(0.25f));
				add(new TableColumn("DESCRIPTION", "Description").setExpandRatio(0.50f));
				add(new TableColumn("ISDEFAULT", "Default?").setWidth(100));

			}
		};
		
		addListButton = new AddButton() {
			{
				
				addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						// TODO Auto-generated method stub
						
					}
				});
				
			}
		};

		editListButton = new EditButton() {
			{
				addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						// TODO Auto-generated method stub
						
					}
				});
			}
		};

		deleteListButton = new DeleteButton() {
			{
				addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						// TODO Auto-generated method stub
						
					}
				});
			}
		};

		addItemButton = new AddButton() {
			{
				
				addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						// TODO Auto-generated method stub
						
					}
				});
				
			}
		};

		editItemButton = new EditButton() {
			{
				addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						// TODO Auto-generated method stub
						
					}
				});
			}
		};

		deleteItemButton = new DeleteButton() {
			{
				addClickListener(new Button.ClickListener() {
					
					@Override
					public void buttonClick(ClickEvent event) {
						// TODO Auto-generated method stub
						
					}
				});
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
				addComponent(new HorizontalLayout() {
					{
						addComponent(addListButton);
						addComponent(editListButton);
						addComponent(deleteListButton);
					}
				});
				addComponent(listtable);
				setExpandRatio(listtable, 0.25f);
				addComponent(new Label("<b>List Values</b>", ContentMode.HTML) {
					{
					}
				});
				addComponent(new HorizontalLayout() {
					{
						addComponent(addItemButton);
						addComponent(editItemButton);
						addComponent(deleteItemButton);
					}
				});
				addComponent(listvalues);
				setExpandRatio(listvalues, 0.75f);

			}
		});

	}

	@Override
	public void enter(ViewChangeEvent event) {

		try {

			OracleContainer query = new OracleContainer(new Lists());
			listtable.setContainerDataSource(query);
			listtable.configure();
		
			listItems = new ListItems();
			listItems.setMandatoryFilters(new Equal("LISTID","-1"));
			OracleContainer itemQuery = new OracleContainer(listItems);
			listvalues.setContainerDataSource(itemQuery);
			listvalues.configure();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
