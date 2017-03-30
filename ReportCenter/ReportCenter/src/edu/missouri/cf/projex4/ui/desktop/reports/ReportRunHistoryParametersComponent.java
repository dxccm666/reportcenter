package edu.missouri.cf.projex4.ui.desktop.reports;

import java.sql.SQLException;
import java.util.Locale;

import org.slf4j.Logger;

import c10n.C10N;

import com.vaadin.data.Container;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import edu.missouri.cf.projex4.data.reports.ReportRunParameters;
import edu.missouri.cf.projex4.data.system.User;
import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.ui.common.TableColumn;
import edu.missouri.cf.projex4.ui.desktop.TableDependentProjexEditor;
import edu.missouri.operations.reportcenter.ui.c10n.configuration.ReportRunParametersTableText;

@SuppressWarnings("serial")
public class ReportRunHistoryParametersComponent extends TableDependentProjexEditor {

	Logger logger = Loggers.getLogger(ReportRunHistoryParametersComponent.class);
	private ReportRunParametersTableText st;
	private ReportRunParameters query;

	public ReportRunHistoryParametersComponent() {
		
		if(User.getUser()!=null) {
			st = C10N.get(ReportRunParametersTableText.class, User.getUser().getUserLocale());
		} else {
			st = C10N.get(ReportRunParametersTableText.class, Locale.ENGLISH);
		}

		setCaption(st.componentName());

		table.add(new TableColumn("PARAMETERNUMBER", st.parameterNumber()).setReadOnly(true));
		table.add(new TableColumn("PARAMETERNAME", st.parameterName()).setReadOnly(true));
		table.add(new TableColumn("PARAMETERVALUE", st.parameterValue()).setReadOnly(true).setExpandRatio(1.0f));
		table.setContextHelp(st.contextHelp());
		
		addButton.setEnabled(false);
		addButton.setVisible(false);

	}

	public void setDataContainerSource(Container source) {
		table.setContainerDataSource(source);
	}

	public void setData(ObjectData refObjectData) {

		if (refObjectData == null) {
			return;
		}

		setObjectData(refObjectData);

		query = new ReportRunParameters();
		query.setRefObjectId(refObjectData.getObjectId());

		try {

			sqlContainer = new OracleContainer(query);
			sqlContainer.addOrderBy(new OrderBy("PARAMETERNUMBER",true));
			table.setContainerDataSource(sqlContainer);
			table.configure();

		} catch (SQLException e) {
			logger.error("Could not set sqlcontainer", e);
		}

	}
}
