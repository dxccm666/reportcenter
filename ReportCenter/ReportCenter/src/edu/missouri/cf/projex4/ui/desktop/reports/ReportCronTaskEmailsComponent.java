package edu.missouri.cf.projex4.ui.desktop.reports;

import java.sql.SQLException;
import org.slf4j.Logger;

import c10n.C10N;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

import edu.missouri.cf.data.OracleString;
import edu.missouri.cf.projex4.data.reports.ReportCronTaskEmails;
import edu.missouri.cf.projex4.data.system.User;
import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.ui.c10n.configuration.ReportCronTaskEmailsText;
import edu.missouri.cf.projex4.ui.common.TableColumn;
import edu.missouri.cf.projex4.ui.desktop.TableDependentProjexEditor;

@SuppressWarnings("serial")
public class ReportCronTaskEmailsComponent extends TableDependentProjexEditor {

	Logger logger = Loggers.getLogger(ReportCronTaskEmailsComponent.class);
	private ReportCronTaskEmailsText st;
	private ReportCronTaskEmails query;

	public ReportCronTaskEmailsComponent() {
		
		st = C10N.get(ReportCronTaskEmailsText.class, User.getUser().getUserLocale());

		setCaption(st.componentName());

		// TODO Can we add validator to email address here?
		
		table.add(new TableColumn("EMAILADDRESS", st.emailAddress()).setReadOnly(true));
		table.setContextHelp(st.contextHelp());
		
		addButton.addClickListener(new Button.ClickListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {
				
				Item item = sqlContainer.getItem(sqlContainer.addItem());
				item.getItemProperty("ROWSTAMP").setValue(new OracleString("AAAA"));
				item.getItemProperty("REPORTCRONTASKID").setValue(new OracleString(refObjectId));
				
			}
		});
		
		addButton.setEnabled(false);
		addButton.setVisible(false);
		setOverrideSecurityChecks(true);

	}
	
	String refObjectId;

	public void setDataContainerSource(Container source) {
		table.setContainerDataSource(source);
	}

	public void setData(String refObjectId) {

		query = new ReportCronTaskEmails();
		query.setRefObjectId(refObjectId);
		this.refObjectId = refObjectId;

		try {

			sqlContainer = new OracleContainer(query);
			sqlContainer.addOrderBy(new OrderBy("EMAILADDRESS",true));
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
