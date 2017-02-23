package edu.missouri.cf.projex4.ui.desktop.reports;

import java.sql.SQLException;
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
import edu.missouri.cf.projex4.data.SecurityGroupIdtoStringConverter;
import edu.missouri.cf.projex4.data.reports.SecurityGroupReports;
import edu.missouri.cf.projex4.data.system.User;
import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.ui.c10n.configuration.ReportParametersTableText;
import edu.missouri.cf.projex4.ui.common.TableColumn;
import edu.missouri.cf.projex4.ui.desktop.TableDependentProjexEditor;
import edu.missouri.cf.projex4.ui.desktop.configuration.users.SecurityGroupIdComboBox;

@SuppressWarnings("serial")
public class SecurityGroupReportsComponent extends TableDependentProjexEditor {

	Logger logger = Loggers.getLogger(SecurityGroupReportsComponent.class);
	private ReportParametersTableText st;
	private SecurityGroupReports query;
	private String reportId;

	public SecurityGroupReportsComponent() {

		st = C10N.get(ReportParametersTableText.class, User.getUser().getUserLocale());

		setSubApplicationName("REPORTSECURITY");

		setCaption("Report Security");

		// TODO This needs to be a security group lookup editor/combobox.

		table.add(new TableColumn("SECURITYGROUPID", "Security Group")
				.setConverter(new SecurityGroupIdtoStringConverter()).setEditorClass(SecurityGroupIdComboBox.class)
				.setRequired(true));

		addButton.setDescription(st.addButton_help());
		addButton.addClickListener(new Button.ClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {

				Item item = (RowItem) sqlContainer.getItem(sqlContainer.addItem());

				item.getItemProperty("ROWSTAMP").setValue(new OracleString("AAAA"));
				item.getItemProperty("REPORTID").setValue(new OracleString(reportId));

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

		query = new SecurityGroupReports();
		query.setRefObjectId(reportId);

		try {

			sqlContainer = new OracleContainer(query);
			sqlContainer.addOrderBy(new OrderBy("SECURITYGROUPID", true));
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
