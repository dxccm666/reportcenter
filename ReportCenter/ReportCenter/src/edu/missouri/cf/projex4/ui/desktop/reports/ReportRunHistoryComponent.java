package edu.missouri.cf.projex4.ui.desktop.reports;

import java.sql.SQLException;
import java.util.Locale;

import org.slf4j.Logger;

import c10n.C10N;

import com.vaadin.data.Container;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import edu.missouri.cf.projex4.data.reports.ReportRunHistory;
import edu.missouri.cf.projex4.data.system.User;
import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.ui.common.TableColumn;
import edu.missouri.cf.projex4.ui.desktop.TableDependentProjexEditor;
import edu.missouri.operations.reportcenter.ui.c10n.configuration.ReportRunHistoryTableText;

@SuppressWarnings("serial")
public class ReportRunHistoryComponent extends TableDependentProjexEditor {

	Logger logger = Loggers.getLogger(ReportRunHistoryComponent.class);
	private ReportRunHistoryTableText st;
	private ReportRunHistory query;

	public ReportRunHistoryComponent() {
		
		if(User.getUser()!=null) {
			st = C10N.get(ReportRunHistoryTableText.class, User.getUser().getUserLocale());
		} else {
			st = C10N.get(ReportRunHistoryTableText.class, Locale.ENGLISH);
		}

		setCaption(st.componentName());

		table.add(new TableColumn("RANON", st.ranOn()).setReadOnly(true));
		table.add(new TableColumn("USERID", st.userId()).setReadOnly(true));
		table.add(new TableColumn("FILELOCATION", st.fileLocation()).setReadOnly(true).setExpandRatio(1.0f));
		table.setContextHelp(st.contextHelp());
		
		addButton.setVisible(false);
		addButton.setEnabled(false);

	}

	public void setDataContainerSource(Container source) {
		table.setContainerDataSource(source);
	}

	public void setData(String reportId) {

		if (reportId == null) {
			return;
		}

		query = new ReportRunHistory();
		query.setRefObjectId(reportId);

		try {

			sqlContainer = new OracleContainer(query);
			sqlContainer.addOrderBy(new OrderBy("RANON",false));
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
