package edu.missouri.cf.projex4.ui.desktop.reports;

import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import c10n.C10N;

import com.vaadin.data.Container;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;

import edu.missouri.cf.projex4.data.reports.ReportCronTaskParameters;
import edu.missouri.operations.data.User;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.ui.c10n.configuration.ReportRunParametersTableText;
import edu.missouri.cf.projex4.ui.common.TableColumn;
import edu.missouri.cf.projex4.ui.desktop.TableDependentProjexEditor;

@SuppressWarnings("serial")
public class ReportCronTaskParametersComponent extends TableDependentProjexEditor {

	final static transient Logger logger = LoggerFactory.getLogger(ReportCronTaskParametersComponent.class);
	private ReportRunParametersTableText st;
	private ReportCronTaskParameters query;

	public ReportCronTaskParametersComponent() {
		
		st = C10N.get(ReportRunParametersTableText.class, User.getUser().getUserLocale());

		setCaption(st.componentName());

		table.add(new TableColumn("PARAMETERNUMBER", st.parameterNumber()).setReadOnly(true));
		table.add(new TableColumn("PARAMETERTYPE", "Type").setReadOnly(true));
		table.add(new TableColumn("PARAMETERNAME", st.parameterName()).setReadOnly(true));
		table.add(new TableColumn("PARAMETERVALUE", st.parameterValue()).setReadOnly(true).setExpandRatio(1.0f));
		table.setContextHelp(st.contextHelp());
		
		addButton.setEnabled(false);
		addButton.setVisible(false);
		
		setOverrideSecurityChecks(true);

	}

	public void setDataContainerSource(Container source) {
		table.setContainerDataSource(source);
	}
	
	public void setData(String refObjectId) {

		query = new ReportCronTaskParameters();
		query.setRefObjectId(refObjectId);

		try {

			sqlContainer = new OracleContainer(query);
			sqlContainer.addOrderBy(new OrderBy("PARAMETERNUMBER",true));
			table.setContainerDataSource(sqlContainer);
			table.configure();

		} catch (SQLException e) {
			logger.error("Could not set sqlcontainer", e);
		}

	}

	@Override
	public void setData(ObjectData refObjectData) {
		// TODO Auto-generated method stub
		
	}
}
