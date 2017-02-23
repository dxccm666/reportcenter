package edu.missouri.cf.projex4.ui.desktop.configuration.securitygroups;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

import edu.missouri.cf.data.OracleBoolean;
import edu.missouri.cf.data.OracleTimestamp;
import edu.missouri.cf.projex4.Projex4UI;
import edu.missouri.cf.projex4.ProjexViewNavigator;
import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.data.system.core.objects.Objects;
import edu.missouri.cf.projex4.data.system.core.securitygroups.SecurityGroups;
import edu.missouri.cf.projex4.ui.desktop.documents.PopupWindow;

@SuppressWarnings("serial")
public class NewSecurityGroupnameWindow extends PopupWindow{

	private Button comfirmButton;
	private Button cancelButton;
	private TextField securityGroupnameField;
	private TextField descriptionField;
	private Label instructions;

	private static Logger logger = LoggerFactory.getLogger(NewSecurityGroupnameWindow.class);
	
	public NewSecurityGroupnameWindow() {
		init();
		layout();
		
	}
	
	public void init() {
		setHeight("300px");
		setCaption("Security Group Name");
		instructions = new Label("<h3>Instructions</h3><p>Please fill out all of the following fields</p>", ContentMode.HTML);
		securityGroupnameField = new TextField() {
			{
				setCaption("Security Group Name");
				setWidth("250px");
				setNullRepresentation("");
			}
		};
		
		descriptionField = new TextField() {
			{
				setCaption("Description");
				setWidth("450px");
				setNullRepresentation("");
			}
		};
		
		comfirmButton = new Button() {
			{
				setCaption("save");
				addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {


						try {
							SecurityGroups query = new SecurityGroups();
							Item item = new PropertysetItem();
							item.addItemProperty("ISACTIVE", new ObjectProperty<OracleBoolean>(OracleBoolean.FALSE));							
							item.addItemProperty("ISSYSTEMSECURITYGROUP", new ObjectProperty<OracleBoolean>(OracleBoolean.FALSE));							
							item.addItemProperty("SECURITYGROUPNAME", new ObjectProperty<String>(securityGroupnameField.getValue()));						
							item.addItemProperty("DESCRIPTION", new ObjectProperty<String>(descriptionField.getValue()));							
							item.addItemProperty("MODIFIED", new ObjectProperty<OracleTimestamp>(OracleTimestamp.now()));
							
							query.storeExternalRow(item);
							
							Object lastId = query.getLastId();
							ObjectData d = Objects.getObjectDataFromID(lastId.toString());
							System.err.println("+++++++++++++++++++++++++++location table last id = " + d + "/" + lastId);
							((ProjexViewNavigator) Projex4UI.get().getNavigator()).navigateTo(ProjexViewProvider.Views.SECURITYGROUPEDITOR, lastId.toString());
							
						} catch (UnsupportedOperationException | SQLException e) {
							logger.error("Could not initialize new item", e);
						}
						close();
				
					}
				});
			}			
			
		};
		
		cancelButton = new Button("cancel");

		cancelButton.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				close();
			}
		});
	}
	
	public void layout() {

		setContent(new VerticalLayout() {
			{
				setMargin(true);
				setSpacing(true);
				addComponent(instructions);
				addComponent(securityGroupnameField);
				addComponent(descriptionField);
				addComponent(new HorizontalLayout() {
					{
						setSpacing(true);
						addComponent(comfirmButton);
						addComponent(cancelButton);
					}
				});
				
			}
		});
	
	}	
	
	public void reset() {
		securityGroupnameField.setValue(null);
		descriptionField.setValue(null);
	}

}
