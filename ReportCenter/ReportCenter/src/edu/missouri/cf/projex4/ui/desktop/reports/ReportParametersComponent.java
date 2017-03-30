package edu.missouri.cf.projex4.ui.desktop.reports;

import java.sql.SQLException;
import java.util.Collection;
import org.slf4j.Logger;

import c10n.C10N;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

import edu.missouri.cf.data.OracleString;
import edu.missouri.cf.projex4.data.reports.ReportParameters;
import edu.missouri.cf.projex4.data.system.User;
import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.ui.common.TableColumn;
import edu.missouri.cf.projex4.ui.desktop.TableDependentProjexEditor;
import edu.missouri.operations.reportcenter.ui.c10n.configuration.ReportParametersTableText;

@SuppressWarnings("serial")
public class ReportParametersComponent extends TableDependentProjexEditor {

	Logger logger = Loggers.getLogger(ReportParametersComponent.class);
	private ReportParametersTableText st;
	private ReportParameters query;
	private String reportId;

	public ReportParametersComponent() {

		st = C10N.get(ReportParametersTableText.class, User.getUser().getUserLocale());
		
		setSubApplicationName("REPORTPARAMETERS");

		setCaption(st.componentName());

		
		// TODO This needs to be a Positive Integer Editor
		table.add(new TableColumn("PARAMETERNUMBER", st.parameterNumber()).setRequired(true));
		
		table.add(new TableColumn("PARAMETER", st.parameter()).setRequired(true));
		
		// TODO This needs to be changed to drop down box.
		table.add(new TableColumn("PARAMETERTYPE", st.parameterType()).setRequired(true));
		
		// TODO This needs to be a List Lookup 
		table.add(new TableColumn("LISTNAME", st.listName()));

		addButton.setDescription(st.addButton_help());
		addButton.addClickListener(new Button.ClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {
				
				Item item = (RowItem) sqlContainer.getItem(sqlContainer.addItem());
				
				item.getItemProperty("ROWSTAMP").setValue(new OracleString("AAAA"));
				item.getItemProperty("REPORTID").setValue(new OracleString(reportId));
				// item.getItemProperty("PARAMETERNUMBER").setValue(OracleDecimal.ONE);
				item.getItemProperty("PARAMETERTYPE").setValue(new OracleString("STRING"));

			}
		});
		
		deleteButton.setDescription(st.deleteButton_help());
		deleteButton.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				
				@SuppressWarnings("unchecked")
				Collection<Object> ids = (Collection<Object>) table.getValue();
				
				for(Object id : ids) {
					
					if(logger.isDebugEnabled()) {
						logger.debug("Removing item {} from report parameters table", id);
					}
					
					table.removeItem(id);
				};
				
				table.commit();
			}
		});
		
	}

	public void setDataContainerSource(Container source) {
		table.setContainerDataSource(source);
	}

	public void setData(String reportId) {

		if (reportId == null) {
			return;
		}

		this.reportId = reportId;

		query = new ReportParameters();
		query.setRefObjectId(reportId);

		try {

			sqlContainer = new OracleContainer(query);
			sqlContainer.addOrderBy(new OrderBy("PARAMETERNUMBER", true));
			table.setContainerDataSource(sqlContainer);
			table.configure();

		} catch (SQLException e) {
			logger.error("Could not set sqlcontainer", e);
		}

	}

	@Override
	public void setData(ObjectData refObjectData) {

		// Do nothing here.

	}
}
