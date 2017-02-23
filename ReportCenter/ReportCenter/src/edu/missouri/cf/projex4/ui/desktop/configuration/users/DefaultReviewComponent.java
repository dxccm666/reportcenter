package edu.missouri.cf.projex4.ui.desktop.configuration.users;

import java.sql.SQLException;

import org.slf4j.Logger;

import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

import edu.missouri.cf.projex4.data.common.DefaultReviewers;
import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.ui.common.TableColumn;
import edu.missouri.cf.projex4.ui.common.system.AdvancedTableEditComponent;
import edu.missouri.cf.projex4.ui.common.system.ExportButton;
import edu.missouri.cf.projex4.ui.desktop.TableDependentProjexEditor;
import edu.missouri.cf.projex4.ui.desktop.configuration.campuses.CampusIdComboBox;

@SuppressWarnings("serial")
public class DefaultReviewComponent extends TableDependentProjexEditor {
	
	private static Logger logger = Loggers.getLogger(DefaultReviewComponent.class);
	
	private DefaultReviewers query;

	private AdvancedTableEditComponent reviewerTableEditComponent;
	private ExportButton exportCampusListItemButton;

	public DefaultReviewComponent() {
	
		setAddingPermitted(true);
		setOverrideSecurityChecks(true);
		
		table.add(new TableColumn("CAMPUSID","Campus").setEditorClass(CampusIdComboBox.class));
		table.add(new TableColumn("REVIEWTYPE", "Review Type"));
		table.setContextHelp("");
		table.setMultiSelect(false);
		table.setSizeFull();
		
		reviewerTableEditComponent= new AdvancedTableEditComponent();
		reviewerTableEditComponent.setAttachedTable(table);
		exportCampusListItemButton = new ExportButton();
		exportCampusListItemButton.setAttachedTable(table);
	}
	

	public void setData(String userId) {
		
		query = new DefaultReviewers();
		query.setUserId(userId);
		
		try {
			
			sqlContainer = new OracleContainer(query);
			sqlContainer.addOrderBy(new OrderBy("CAMPUS", true));
			sqlContainer.addOrderBy(new OrderBy("REVIEWTYPE", true));
			table.setContainerDataSource(sqlContainer);
			table.configure();
			
		} catch (SQLException e) {
			
			if(logger.isErrorEnabled()) {
				logger.error(e.getSQLState());
			}
			
		}
		
		addButton.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				sqlContainer.addItem();

			}

		});
		
		
	}

	@Override
	public void setData(ObjectData refObjectData) {
		throw new UnsupportedOperationException("ObjectData is not accepted");
	}

}
