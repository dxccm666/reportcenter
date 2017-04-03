package edu.missouri.operations.reportcenter.ui.views.configuration.securitygroups;

import java.sql.SQLException;

import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.operations.reportcenter.data.SecurityGroups;
import edu.missouri.operations.reportcenter.ui.TopBarView;
import edu.missouri.operations.ui.StandardTable;
import edu.missouri.operations.ui.TableColumn;
import edu.missouri.operations.ui.desktop.buttons.AddButton;
import edu.missouri.operations.ui.desktop.buttons.DeleteButton;
import edu.missouri.operations.ui.desktop.buttons.EditButton;

public class SecurityGroupsView extends TopBarView {

	private StandardTable properties;
	private AddButton addButton;
	private EditButton editButton;
	private DeleteButton deleteButton;

	public SecurityGroupsView() {
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
				add(new TableColumn("SECURITYGROUPNAME", "Name").setExpandRatio(0.30f));
				add(new TableColumn("DESCRIPTION", "Description").setExpandRatio(0.70f));
				add(new TableColumn("ISACTIVE", "Active?").setWidth(100));
				add(new TableColumn("MODIFIED", "Modified").setWidth(150));
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
				addComponent(new Label("Security Groups", ContentMode.HTML) {
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
				addComponent(properties);
				setExpandRatio(properties, 1.0f);

			}
		});

	}

	@Override
	public void enter(ViewChangeEvent event) {

		try {

			SecurityGroups query = new SecurityGroups();
			OracleContainer container = new OracleContainer(query);
			properties.setContainerDataSource(container);
			properties.configure();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
