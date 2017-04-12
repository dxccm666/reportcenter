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
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.operations.reportcenter.data.SecurityGroups;
import edu.missouri.operations.reportcenter.ui.TopBarView;
import edu.missouri.operations.ui.OracleBooleanCheckBox;
import edu.missouri.operations.ui.OracleStringTextArea;
import edu.missouri.operations.ui.OracleTimestampField;
import edu.missouri.operations.ui.StandardTable;
import edu.missouri.operations.ui.desktop.buttons.CancelButton;
import edu.missouri.operations.ui.desktop.buttons.SaveButton;

public class SecurityGroupEditorView extends TopBarView {

	@PropertyId("ID")
	private TextField id;

	@PropertyId("SECURITYGROUPNAME")
	private TextField securityGroupName;

	@PropertyId("DESCRIPTION")
	private OracleStringTextArea description;

	@PropertyId("ISACTIVE")
	private OracleBooleanCheckBox isActive;

	@PropertyId("MODIFIED")
	private OracleTimestampField modified;

	private FieldGroup binder;
	private OracleContainer container;
	private Item item;

	private StandardTable table;

	private SaveButton saveButton;

	private CancelButton cancelButton;

	public SecurityGroupEditorView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void attach() {
		super.attach();

		id = new TextField() {
			{
				setCaption("#");
				setReadOnly(true);
			}
		};

		securityGroupName = new TextField() {
			{
				setCaption("Security Group Name");
				setRequired(true);
			}
		};

		description = new OracleStringTextArea() {
			{
				setCaption("Description");
				setRequired(true);
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
				setReadOnly(true);
			}
		};

		table = new StandardTable() {
			{

			}
		};

		saveButton = new SaveButton() {
			{
			}
		};

		cancelButton = new CancelButton() {
			{
			}
		};

		VerticalLayout layout = new VerticalLayout() {
			{
				setSizeFull();
				setMargin(true);
				setSpacing(true);
				addComponent(new HorizontalLayout() {
					{
						addComponent(saveButton);
						addComponent(cancelButton);
					}
				});
				addComponent(new HorizontalLayout() {
					{
						setSpacing(true);
						setWidth("100%");
						addComponent(id);
						addComponent(securityGroupName);
						securityGroupName.setWidth("100%");
						setExpandRatio(securityGroupName, 1.0f);
						addComponent(modified);
					}
				});
				addComponent(isActive);
				addComponent(description);
				description.setWidth("100%");

				addComponent(new Label("Security Group Users"));
				addComponent(table);
				setExpandRatio(table, 1.0f);

			}
		};

		addInnerComponent(layout);

	}

	@Override
	public void enter(ViewChangeEvent event) {

		try {
			// parameters should be securitygroup id value.
			String parameters = event.getParameters();
			logger.debug("Parameters inside enter = {}", parameters);

			SecurityGroups query = new SecurityGroups();
			query.setMandatoryFilters(new Compare.Equal("ID", parameters));

			container = new OracleContainer(query);

			if (container.size() == 1) {

				item = container.getItem(container.getIdByIndex(0));
				logger.debug("item = {}", item);

				binder = new FieldGroup(item);
				binder.bindMemberFields(this);
				
				id.setReadOnly(true);
				modified.setReadOnly(true);

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {

		}

	}

}
