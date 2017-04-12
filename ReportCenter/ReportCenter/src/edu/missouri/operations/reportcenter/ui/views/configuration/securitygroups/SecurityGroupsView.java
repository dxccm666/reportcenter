package edu.missouri.operations.reportcenter.ui.views.configuration.securitygroups;

import java.sql.SQLException;

import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

import edu.missouri.operations.data.OracleBoolean;
import edu.missouri.operations.data.OracleTimestamp;
import edu.missouri.operations.reportcenter.ReportcenterUI;
import edu.missouri.operations.reportcenter.data.SecurityGroups;
import edu.missouri.operations.reportcenter.ui.ReportCenterViewProvider;
import edu.missouri.operations.reportcenter.ui.TopBarView;
import edu.missouri.operations.ui.StandardTable;
import edu.missouri.operations.ui.TableColumn;
import edu.missouri.operations.ui.desktop.buttons.AddButton;
import edu.missouri.operations.ui.desktop.buttons.AddNavigatorButton;
import edu.missouri.operations.ui.desktop.buttons.DeleteButton;
import edu.missouri.operations.ui.desktop.buttons.EditButton;
import edu.missouri.operations.ui.desktop.buttons.EditNavigatorButton;

public class SecurityGroupsView extends TopBarView {

	private StandardTable table;
	private AddNavigatorButton addButton;
	private EditNavigatorButton editButton;
	private DeleteButton deleteButton;
	private SecurityGroups query;
	private OracleContainer container;

	public SecurityGroupsView() {
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
				add(new TableColumn("SECURITYGROUPNAME", "Name").setExpandRatio(0.30f));
				add(new TableColumn("DESCRIPTION", "Description").setExpandRatio(0.70f));
				add(new TableColumn("ISACTIVE", "Active?").setWidth(100));
				add(new TableColumn("MODIFIED", "Modified").setWidth(150));
				setMultiSelect(false);
			}
		};

		addButton = new AddNavigatorButton(ReportcenterUI.get().getViewNavigator(), ReportCenterViewProvider.Views.SECURITYGROUPEDITOR);
		addButton.setClickListener(new Button.ClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {

				Item newItem = container.getItem(container.addItem());
				newItem.getItemProperty("ROWSTAMP").setValue(new String("AAAA"));
				newItem.getItemProperty("ISACTIVE").setValue(OracleBoolean.FALSE);
				newItem.getItemProperty("MODIFIED").setValue(OracleTimestamp.now());

				try {
					
					container.commit();
					addButton.navigateTo(query.getLastId().toString());
					
				} catch (UnsupportedOperationException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		});

		editButton = new EditNavigatorButton(ReportcenterUI.get().getViewNavigator(), ReportCenterViewProvider.Views.SECURITYGROUPEDITOR);
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
				addComponent(table);
				setExpandRatio(table, 1.0f);

			}
		});

	}

	@Override
	public void enter(ViewChangeEvent event) {

		try {

			query = new SecurityGroups();
			container = new OracleContainer(query);
			table.setContainerDataSource(container);
			table.configure();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
