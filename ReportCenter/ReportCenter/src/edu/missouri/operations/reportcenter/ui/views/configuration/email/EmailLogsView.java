package edu.missouri.operations.reportcenter.ui.views.configuration.email;

import java.sql.SQLException;

import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.cf.projex4.data.system.core.email.EmailLogs;
import edu.missouri.operations.reportcenter.data.SystemProperties;
import edu.missouri.operations.reportcenter.ui.TopBarView;
import edu.missouri.operations.ui.StandardTable;
import edu.missouri.operations.ui.TableColumn;
import edu.missouri.operations.ui.desktop.buttons.AddButton;
import edu.missouri.operations.ui.desktop.buttons.DeleteButton;
import edu.missouri.operations.ui.desktop.buttons.EditButton;

public class EmailLogsView extends TopBarView {

	private StandardTable table;
	private DeleteButton deleteButton;

	public EmailLogsView() {
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
				
				add(new TableColumn("EMAILADDRESS","Email Address"));
				add(new TableColumn("SENDDATE","Sent"));
				add(new TableColumn("STATUSMESSAGE","Status"));

			}
		};
		
		deleteButton = new DeleteButton() {{}};

		addInnerComponent(new VerticalLayout() {
			{
				setSizeFull();
				setMargin(true);
				addComponent(new Label("Email Log", ContentMode.HTML) {
					{
						addStyleName("maintitle");
					}
				});
				addComponent(new HorizontalLayout() {
					{
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
			
			EmailLogs query = new EmailLogs();
			OracleContainer container = new OracleContainer(query);
			table.setContainerDataSource(container);
			table.configure();
			
		} catch (SQLException e) {
			
			if(logger.isDebugEnabled()) {
				logger.debug("Error occurred", e);
			}
			
		}

	}

}
