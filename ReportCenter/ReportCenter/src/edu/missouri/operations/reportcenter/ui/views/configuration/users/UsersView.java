package edu.missouri.operations.reportcenter.ui.views.configuration.users;

import java.sql.SQLException;

import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

import edu.missouri.operations.reportcenter.ReportcenterUI;
import edu.missouri.operations.reportcenter.data.Users;
import edu.missouri.operations.reportcenter.ui.ReportCenterViewProvider;
import edu.missouri.operations.reportcenter.ui.TopBarView;
import edu.missouri.operations.ui.StandardTable;
import edu.missouri.operations.ui.TableColumn;
import edu.missouri.operations.ui.desktop.buttons.AddButton;
import edu.missouri.operations.ui.desktop.buttons.AddNavigatorButton;
import edu.missouri.operations.ui.desktop.buttons.DeleteButton;
import edu.missouri.operations.ui.desktop.buttons.EditButton;
import edu.missouri.operations.ui.desktop.buttons.EditNavigatorButton;

public class UsersView extends TopBarView {

	private StandardTable table;
	private AddNavigatorButton addButton;
	private EditNavigatorButton editButton;
	private DeleteButton deleteButton;

	public UsersView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void attach() {
		super.attach();
		init();
	}

	private void init() {

		table = new StandardTable() {
			{
				add(new TableColumn("USERLOGIN", "Login"));
				add(new TableColumn("FULLNAME", "Full Name"));
				add(new TableColumn("SORTNAME", "Sort Name"));
				add(new TableColumn("ISACTIVE", "Active?"));
				add(new TableColumn("CREATED", "Created"));
				add(new TableColumn("CREATEDBY", "Created By"));
				setMultiSelect(false);
			}
		};

		addButton = new AddNavigatorButton(ReportcenterUI.get().getViewNavigator(), ReportCenterViewProvider.Views.USEREDITOR);
		
		
		editButton = new EditNavigatorButton(ReportcenterUI.get().getViewNavigator(), ReportCenterViewProvider.Views.USEREDITOR);
		editButton.setClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				Object itemId = table.getValue();
				if (itemId != null) {
					editButton.navigateTo(itemId.toString());
				} else {
					Notification.show("Must select row in table below.");
				}

			}

		});

		deleteButton = new DeleteButton() {
			{
			}
		};

		addInnerComponent(new VerticalLayout() {
			{
				setSizeFull();
				setMargin(true);
				addComponent(new Label("Users", ContentMode.HTML) {
					{
						addStyleName("maintitle");
					}
				});
				addComponent(new HorizontalLayout() {
					{
						addComponent(addButton);
						addComponent(editButton);
						addComponent(deleteButton);
					}
				});
				addComponent(table);
				setExpandRatio(table, 1.0f);

			}
		});

	}

	@Override
	public void enter(ViewChangeEvent event) {
		
		try {
			
			Users query = new Users();
			OracleContainer container = new OracleContainer(query);
			table.setContainerDataSource(container);
			table.configure();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
