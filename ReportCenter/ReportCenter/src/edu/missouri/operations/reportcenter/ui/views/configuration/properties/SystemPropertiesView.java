package edu.missouri.operations.reportcenter.ui.views.configuration.properties;

import java.sql.SQLException;

import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.operations.reportcenter.data.SystemProperties;
import edu.missouri.operations.reportcenter.ui.TopBarView;
import edu.missouri.operations.ui.StandardTable;
import edu.missouri.operations.ui.TableColumn;
import edu.missouri.operations.ui.desktop.buttons.AddButton;
import edu.missouri.operations.ui.desktop.buttons.DeleteButton;
import edu.missouri.operations.ui.desktop.buttons.EditButton;

public class SystemPropertiesView extends TopBarView {

	private StandardTable properties;
	private AddButton addButton;
	private EditButton editButton;
	private DeleteButton deleteButton;

	public SystemPropertiesView() {
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
				
				add(new TableColumn("PROPERTY", "Property"));
				add(new TableColumn("VALUE","Value"));

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
				addComponent(new Label("System Properties", ContentMode.HTML) {
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
			
			SystemProperties query = new SystemProperties();
			OracleContainer container = new OracleContainer(query);
			properties.setContainerDataSource(container);
			properties.configure();
			
		} catch (SQLException e) {
			
			if(logger.isDebugEnabled()) {
				logger.debug("Error occurred", e);
			}
			
		}

	}

}
