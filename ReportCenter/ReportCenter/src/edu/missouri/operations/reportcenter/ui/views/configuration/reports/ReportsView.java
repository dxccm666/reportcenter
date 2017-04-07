package edu.missouri.operations.reportcenter.ui.views.configuration.reports;

import java.sql.SQLException;

import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.operations.reportcenter.data.Reports;
import edu.missouri.operations.reportcenter.ui.TopBarView;
import edu.missouri.operations.ui.StandardTable;
import edu.missouri.operations.ui.TableColumn;
import edu.missouri.operations.ui.desktop.buttons.AddButton;
import edu.missouri.operations.ui.desktop.buttons.EditButton;

public class ReportsView extends TopBarView {

	private StandardTable table;
	private AddButton addButton;
	private EditButton editButton;

	public ReportsView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void attach() {
		super.attach();
		init();
	}

	private void init() {

		addButton = new AddButton() {
			{
			}
		};

		editButton = new EditButton() {
			{
			}
		};

		table = new StandardTable() {
			{
				add(new TableColumn("REPORTNAME","Name"));
				add(new TableColumn("DESCRIPTION","Description"));
				add(new TableColumn("ISACTIVE","Active?"));
				add(new TableColumn("REQUESTED","Requested"));
				add(new TableColumn("REQUESTEDBY","Requested By"));
				add(new TableColumn("REGISTERED","Registered"));

			}
		};

		addInnerComponent(new VerticalLayout() {
			{
				setSizeFull();
				setMargin(true);
				addComponent(new Label("Report Registration", ContentMode.HTML) {
					{
						addStyleName("maintitle");
					}
				});
				addComponent(new HorizontalLayout() {
					{
						addComponent(addButton);
						addComponent(editButton);
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
			
			Reports query = new Reports();
			OracleContainer container = new OracleContainer(query);
			table.setContainerDataSource(container);
			table.configure();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
