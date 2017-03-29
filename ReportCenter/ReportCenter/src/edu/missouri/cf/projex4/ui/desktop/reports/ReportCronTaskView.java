/**
 * 
 */
package edu.missouri.cf.projex4.ui.desktop.reports;

import java.sql.SQLException;
import java.util.Collection;

import org.slf4j.Logger;

import c10n.C10N;

import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button;

import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.data.reports.ReportCronTasks;
import edu.missouri.cf.projex4.data.system.User;
import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.ui.c10n.configuration.ReportCronTaskText;
import edu.missouri.cf.projex4.ui.common.TableColumn;
import edu.missouri.cf.projex4.ui.common.system.ExportButton;
import edu.missouri.cf.projex4.ui.desktop.NavigatorOpenerButton;
import edu.missouri.cf.projex4.ui.desktop.StandardTable;
import edu.missouri.cf.projex4.ui.desktop.TableControlLayout;
import edu.missouri.cf.projex4.ui.desktop.TopBarView;
import edu.missouri.operations.reportcenter.data.SecurityGroupUsers;
import edu.missouri.operations.ui.desktop.buttons.DeleteButton;

/**
 * @author graumannc
 * 
 */
@SuppressWarnings("serial")
public class ReportCronTaskView extends TopBarView {

	private final Logger logger = Loggers.getLogger(ReportCronTaskView.class);

	boolean allowswitch = true;

	private StandardTable table;

	private Label screendescription;

	private ExportButton exportButton;

	private ReportCronTasks query;

	private Opener opener;

	private OracleContainer sqlContainer;

	private ReportCronTaskText st;

	private DeleteButton deleteButton;

	public class Opener extends NavigatorOpenerButton {

		public Opener() {
			init();
		}

		private void init() {

			addComponent(ProjexViewProvider.Views.SCHEDULEDREPORTEDITOR, "scheduled report");

		}
	}

	/**
	 * 
	 */
	public ReportCronTaskView() {
	}

	@Override
	public void attach() {
		super.attach();
		init();
		layout();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener
	 * .ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {

		try {

			query = new ReportCronTasks();

			if (!User.canDo(ProjexViewProvider.Views.SCHEDULEDREPORTS, "SEEALLSCHEDULEDREPORTS")) {
				query.setUserId(User.getUser().getUserId());
			}

			sqlContainer = new OracleContainer(query);
			sqlContainer.addOrderBy(new OrderBy("REPORTNAME", true));
			table.setContainerDataSource(sqlContainer);
			table.configure();

		} catch (SQLException e) {
			logger.error("Error setting ReportRegistrationView query", e);
		}

	}

	protected void init() {

		st = C10N.get(ReportCronTaskText.class, User.getUser().getUserLocale());

		screendescription = new Label("<h1>" + st.componentName() + "</h1>", ContentMode.HTML) {
			{
				addStyleName("projectlisting_label");
			}
		};

		table = new StandardTable() {
			{
				add(new TableColumn("ID", st.id()).setWidth(50));
				add(new TableColumn("REPORTNAME", st.reportName()).setWidth(200));
				add(new TableColumn("DESCRIPTION", st.description()).setWidth(200));
				add(new TableColumn("SCREENNAME", st.screenName()).setWidth(100));
				add(new TableColumn("CATEGORY", st.category()));
				add(new TableColumn("ISACTIVE", st.isActive()));
				add(new TableColumn("SCHEDULEDBYUSERNAME", st.scheduledByUserName()));
				add(new TableColumn("SCHEDULED", st.scheduled()));
				add(new TableColumn("CRONEXPRESSION", st.cronExpression()));
				add(new TableColumn("FILEFORMAT", st.fileFormat()));
				add(new TableColumn("ISONETIME", st.isOneTime()));
				setContextHelp(st.contextHelp());
			}
		};

		deleteButton = new DeleteButton() {
			{
				setDescription("delete scheduled report.");

				addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(Button.ClickEvent event) {

						@SuppressWarnings("unchecked")
						Collection<Object> selected = (Collection<Object>) table.getValue();
						if (selected == null) {
							Notification.show("must select items below");
						} else {

							try {

								if (SecurityGroupUsers.memberOf("ADMINISTRATORS", User.getUser().getUserId())) {
									
									for (Object selectedObject : selected) {
											sqlContainer.removeItem(selectedObject);
									}
									

								} else {

									for (Object selectedObject : selected) {
										Item i = table.getItem(selectedObject);
										if (User.getUser().getUserId().equals(i.getItemProperty("SCHEDULEDBY").getValue().toString())) {
											sqlContainer.removeItem(selectedObject);
										}
									}
								}
								sqlContainer.commit();

							} catch (SQLException e) {
								if (logger.isErrorEnabled()) {
									logger.error(e.getMessage(), e);
								}
							}
						}

					}

				});
			}
		};

		exportButton = new ExportButton();
		exportButton.setAttachedTable(table);

		opener = new Opener();
		opener.setTable(table);

	}

	protected void layout() {

		VerticalLayout wrapper = new VerticalLayout() {
			{
				setMargin(true);
				setSpacing(true);
				addComponent(screendescription);
				addComponent(new TableControlLayout() {
					{
						addLeftComponent(deleteButton);
						addLeftComponent(opener);
						addRightComponent(exportButton);
					}
				});
				addComponent(table);
				table.setSizeFull();
				setExpandRatio(table, 1.0f);
				setSizeFull();

			}
		};
		addComponent(wrapper);
		setExpandRatio(wrapper, 1.0f);

	}

}
