package edu.missouri.operations.reportcenter.ui.views.configuration.email;

import java.sql.SQLException;

import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.cf.projex4.data.system.core.email.EmailTemplates;
import edu.missouri.operations.reportcenter.ui.TopBarView;
import edu.missouri.operations.ui.StandardTable;
import edu.missouri.operations.ui.TableColumn;
import edu.missouri.operations.ui.desktop.buttons.AddButton;
import edu.missouri.operations.ui.desktop.buttons.DeleteButton;
import edu.missouri.operations.ui.desktop.buttons.EditButton;

public class EmailTemplatesView extends TopBarView {

	private StandardTable table;
	private AddButton addButton;
	private EditButton editButton;
	private DeleteButton deleteButton;

	public EmailTemplatesView() {
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
				add(new TableColumn("EMAILNAME", "Name"));
				add(new TableColumn("EMAILSUBJECT", "Subject"));
				add(new TableColumn("HELPTEXT","Help Text"));
				add(new TableColumn("ISACTIVE","Active?"));
				add(new TableColumn("CREATEDBY","Created By"));
				add(new TableColumn("CREATED","Created"));
				add(new TableColumn("MODIFIEDBY","Created By"));
				add(new TableColumn("MODIFIED","Created"));
				
			}
		};
		
		addButton = new AddButton() {{}};
		editButton = new EditButton() {{}};
		deleteButton = new DeleteButton() {{}};

		addInnerComponent(new VerticalLayout() {
			{
				setSizeFull();
				setMargin(true);
				addComponent(new Label("Email Templates", ContentMode.HTML) {
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
			
			EmailTemplates query = new EmailTemplates();
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
