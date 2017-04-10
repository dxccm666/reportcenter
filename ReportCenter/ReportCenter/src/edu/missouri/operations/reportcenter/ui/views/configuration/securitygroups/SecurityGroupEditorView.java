package edu.missouri.operations.reportcenter.ui.views.configuration.securitygroups;

import java.sql.SQLException;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.operations.reportcenter.data.SecurityGroups;
import edu.missouri.operations.reportcenter.ui.TopBarView;
import edu.missouri.operations.ui.OracleBooleanCheckBox;
import edu.missouri.operations.ui.OracleTimestampField;

public class SecurityGroupEditorView extends TopBarView {

	@PropertyId("ID")
	private TextField id;
	
	@PropertyId("SECURITYGROUPNAME")
	private TextField securityGroupName;

	@PropertyId("DESCRIPTION")
	private TextField description;
	
	@PropertyId("ISACTIVE")
	private OracleBooleanCheckBox isActive;
	
	@PropertyId("MODIFIED")
	private OracleTimestampField modified;
	
	private FieldGroup binder;
	private OracleContainer container;
	private Item item;

	public SecurityGroupEditorView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void attach() {
		super.attach();

		id = new TextField() {
			{
			}
		};

		securityGroupName = new TextField() {
			{
			}
		};
		
		description = new TextField() {
			{
				setCaption("Description");
			}
		};
		
		isActive = new OracleBooleanCheckBox() {
			{
				setCaption("Active?");
			}
		};

		modified = new OracleTimestampField() {
			{
				setCaption("Date Modified");
			}
		};

		VerticalLayout layout = new VerticalLayout() {
			{
				setSizeFull();
				addComponent(new HorizontalLayout() {
					{
						addComponent(id);
						addComponent(securityGroupName);
					}
				});
				addComponent(description);
				addComponent(new HorizontalLayout() {
					{
						addComponent(isActive);
						addComponent(modified);
					}
				});
				
				// addComponent(table);

			}
		};
		
		addInnerComponent(layout);

	}

	@Override
	public void enter(ViewChangeEvent event) {
		
		// parameters should be securitygroup id value.
		String parameters = event.getParameters();

		SecurityGroups query = new SecurityGroups();
		query.setMandatoryFilters(new Compare.Equal("ID",parameters));
		try {
			
			container = new OracleContainer(query);
			item = container.getItem(container.getIdByIndex(1));
			
			binder = new FieldGroup();
			binder.bindMemberFields(item);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		

	}

}
