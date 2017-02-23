package edu.missouri.cf.projex4.ui.desktop.reports;

import java.sql.SQLException;
import java.util.Locale;

import org.slf4j.Logger;

import c10n.C10N;

import com.vaadin.data.Container;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import edu.missouri.cf.projex4.data.reports.ReportModificationHistory;
import edu.missouri.cf.projex4.data.system.User;
import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.ui.c10n.configuration.ReportModificationHistoryTableText;
import edu.missouri.cf.projex4.ui.common.TableColumn;
import edu.missouri.cf.projex4.ui.desktop.TableDependentProjexEditor;

@SuppressWarnings("serial")
public class ReportModificationHistoryComponent extends TableDependentProjexEditor {

	Logger logger = Loggers.getLogger(ReportModificationHistoryComponent.class);
	private ReportModificationHistoryTableText st;
	private ReportModificationHistory query;

	public ReportModificationHistoryComponent() {
		
		if(User.getUser()!=null) {
			st = C10N.get(ReportModificationHistoryTableText.class, User.getUser().getUserLocale());
		} else {
			st = C10N.get(ReportModificationHistoryTableText.class, Locale.ENGLISH);
		}

		setCaption(st.componentName());

		table.add(new TableColumn("REQUESTED", st.requested()).setReadOnly(true));
		table.add(new TableColumn("REQUESTEDBY", st.requestedBy()).setReadOnly(true));
		table.add(new TableColumn("REASON", st.reason()).setReadOnly(true).setExpandRatio(1.0f));
		table.add(new TableColumn("REGISTERED", st.registered()).setReadOnly(true));
		table.add(new TableColumn("REGISTEREDBY", st.registeredBy()).setReadOnly(true));
		table.add(new TableColumn("FILENAME", st.fileName()).setReadOnly(true).setCollapsed(true));
		
		table.setContextHelp(st.contextHelp());
		
		addButton.setEnabled(false);
		addButton.setVisible(false);

	}

	public void setDataContainerSource(Container source) {
		table.setContainerDataSource(source);
	}

	public void setData(String reportId) {

		if (reportId == null) {
			return;
		}
		
		query = new ReportModificationHistory();
		query.setRefObjectId(reportId);

		try {

			sqlContainer = new OracleContainer(query);
			sqlContainer.addOrderBy(new OrderBy("REGISTERED",false));
			table.setContainerDataSource(sqlContainer);
			table.configure();

		} catch (SQLException e) {
			logger.error("Could not set sqlcontainer", e);
		}

	}

	@Override
	public void setData(ObjectData refObjectData) {
	}
		
}
