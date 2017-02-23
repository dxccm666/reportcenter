/**
 * 
 */
package edu.missouri.cf.projex4.ui.desktop.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.cf.data.OracleBoolean;
import edu.missouri.cf.projex4.data.Pools;
import edu.missouri.cf.projex4.data.reports.ReportListings;
import edu.missouri.cf.projex4.data.reports.ReportRunHistory;
import edu.missouri.cf.projex4.ui.common.CollapsibleLayout;
import edu.missouri.cf.projex4.ui.common.TableColumn;
import edu.missouri.cf.projex4.ui.desktop.ReportsTopBarView;
import edu.missouri.cf.projex4.ui.desktop.StandardTable;

/**
 * @author graumannc
 * 
 */
@SuppressWarnings("serial")
public class ReportsView extends ReportsTopBarView {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	private Label screendescription;
	private StandardTable filterTable_1;
	private StandardTable filterTable_runhistory;
	private ReportListings query;
	private ReportRunHistory query_history;
	private CollapsibleLayout historyLayout;
	private String screenName;
	private Button reportButton;
	private String uuid;

	/**
	 * 
	 */
	public ReportsView() {
	}

	@Override
	public void attach() {
		super.attach();
		init();
		layout();
	}

	private void init() {

		screendescription = new Label("<h1>Reports</h1>", ContentMode.HTML);
		screendescription.addStyleName("Reports Screen");

		reportButton = new Button() {
			{
				setCaption("run report");
				setIcon(new ThemeResource("icons/chalkwork/basic/report_16x16.png"));
				addStyleName("borderless");

				addClickListener(new ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {

						if (logger.isDebugEnabled()) {
							logger.debug("Run Report Button clicked");
						}

						Item item = (Item) filterTable_1.getItem(filterTable_1.getValue());

						if (item != null) {

							String reportName = item.getItemProperty("REPORTNAME").getValue().toString();
							String fileName = item.getItemProperty("FILENAME").getValue().toString();
							String reportId = item.getItemProperty("ID").getValue().toString();
							OracleBoolean slowRunning = (OracleBoolean) item.getItemProperty("ISSLOWRUNNING").getValue();
							ReportWindow report = new ReportWindow(reportId, reportName, fileName, getReportParameters(reportId),
									uuid, slowRunning.toBoolean());
							report.addWindow();

						} else {
							Notification.show("A report needs to be selected below.");
							return;
						}

					}
				});
			}
		};

		filterTable_1 = new StandardTable() {
			{
				setSizeFull();
				setMultiSelect(false);

				// add(new TableColumn("ID", "#"));
				add(new TableColumn("CATEGORY", "Category"));
				add(new TableColumn("REPORTNAME", "Report Name").setCollapsed(false));
				add(new TableColumn("DESCRIPTION", "Description"));
				add(new TableColumn("HELPTEXT", "Help"));
				add(new TableColumn("CAMPUS", "Campus"));
				add(new TableColumn("ISALLCAMPUS", "Is all campus?"));
				add(new TableColumn("ISSLOWRUNNING", "Is Slow Running?"));

				addValueChangeListener(new Property.ValueChangeListener() {

					@Override
					public void valueChange(Property.ValueChangeEvent event) {

						try {

							Item selectedReport = (Item) filterTable_1.getItem(filterTable_1.getValue());

							if (selectedReport != null) {
								query_history = new ReportRunHistory();
								query_history.setRefObjectId(selectedReport.getItemProperty("ID").getValue().toString());
								OracleContainer sqlContainer_history = new OracleContainer(query_history);
								filterTable_runhistory.setContainerDataSource(sqlContainer_history);
								filterTable_runhistory.configure();
							}

						} catch (SQLException e) {

							if (logger.isErrorEnabled()) {
								logger.error("Error setting Report Run History", e);
							}

						}

					}
				});
			}
		};

		filterTable_runhistory = new StandardTable() {
			{
				setSizeFull();
				add(new TableColumn("ID", "#"));
				add(new TableColumn("USERID", "User Name").setCollapsed(false));
				add(new TableColumn("REPORTID", "Report Name"));
				add(new TableColumn("RANON", "Ran on"));
				add(new TableColumn("FILELOCATION", "File Location"));
			}
		};

		historyLayout = new CollapsibleLayout();
		historyLayout.setCaption("Report Run History");

	}

	protected HashMap<String, String> getReportParameters(String reportId) {

		HashMap<String, String> map = new HashMap<String, String>();

		if (reportId != null) {

			Connection conn = null;
			try {
				conn = Pools.getConnection(Pools.Names.PROJEX);

				try (PreparedStatement stmt = conn
						.prepareStatement("select PARAMETER, PARAMETERTYPE from reportparameters where reportid = ?")) {
					stmt.setString(1, reportId);

					try (ResultSet rs = stmt.executeQuery()) {

						while (rs.next()) {
							map.put(rs.getString("PARAMETER"), rs.getString("PARAMETERTYPE"));
						}
					}
				}

			} catch (SQLException e) {
				if (logger.isErrorEnabled()) {
					logger.error(e.getMessage());
				}
			} finally {
				Pools.releaseConnection(Pools.Names.PROJEX, conn);
			}
		}

		return map;
	}

	private void layout() {

		VerticalLayout wrapper = new VerticalLayout() {
			{
				setMargin(true);
				setSpacing(true);
				addComponent(screendescription);
				addComponent(reportButton);
				addComponent(filterTable_1);
				addComponent(historyLayout);
				historyLayout.addComponent(filterTable_runhistory);
			}
		};

		addComponent(wrapper);
		setExpandRatio(wrapper, 1.0f);

	}

	@Override
	public void enter(ViewChangeEvent event) {
		setScreenData(event.getParameters());
	}

	class DataLoader implements Runnable {

		String screenName;
		String uuid;

		DataLoader(String screenName, String uuid) {
			this.screenName = screenName;
			this.uuid = uuid;
		}

		@Override
		public void run() {

			query = new ReportListings();
			query.setRefObjectId(screenName.toUpperCase());

			try {

				final OracleContainer sqlContainer = new OracleContainer(query);
				sqlContainer.addOrderBy(new OrderBy("CATEGORY", true), new OrderBy("REPORTNAME", true));

				filterTable_1.setContainerDataSource(sqlContainer);
				filterTable_1.configure();


			} catch (SQLException e) {
				if (logger.isErrorEnabled()) {
					logger.error("Could not set table data", e);
				}
			}
		}

	}

	public void setScreenData(String parameters) {
		logger.debug("parameters = {}", parameters);

		String fragment = Page.getCurrent().getUriFragment().substring(1);
		System.err.println("Fragment = " + fragment);
		String[] fragments = fragment.split("/");

		screenName = fragments[1];
		if (fragments.length > 2) {
			uuid = fragments[2];
		}

		System.err.println("SCREENNAME = " + screenName);
		System.err.println("UUID = " + uuid);
		
		new DataLoader(screenName, uuid).run();
		
	}

}
