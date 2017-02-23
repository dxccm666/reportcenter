/**
 * 
 */
package edu.missouri.cf.projex4.ui.desktop.reports;

import java.sql.SQLException;
import java.util.Locale;

import org.slf4j.Logger;

import c10n.C10N;

import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import edu.missouri.cf.data.OracleBoolean;
import edu.missouri.cf.data.OracleString;
import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.data.reports.Reports;
import edu.missouri.cf.projex4.data.system.User;
import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.ui.c10n.configuration.ReportEditorText;
import edu.missouri.cf.projex4.ui.common.TableColumn;
import edu.missouri.cf.projex4.ui.common.system.ExportButton;
import edu.missouri.cf.projex4.ui.desktop.AddNavigatorButton;
import edu.missouri.cf.projex4.ui.desktop.NavigatorOpenerButton;
import edu.missouri.cf.projex4.ui.desktop.StandardTable;
import edu.missouri.cf.projex4.ui.desktop.TableControlLayout;
import edu.missouri.cf.projex4.ui.desktop.TopBarView;

/**
 * @author graumannc
 * 
 */
@SuppressWarnings("serial")
public class ReportRegistrationView extends TopBarView {

	private final Logger logger = Loggers.getLogger(ReportRegistrationView.class);

	boolean allowswitch = true;

	private StandardTable table;

	private Label screendescription;

	private ExportButton exportButton;

	private Reports query;

	private ReportEditorText st;

	private AddNavigatorButton addButton;

	private Opener opener;

	private OracleContainer sqlContainer;

	public class Opener extends NavigatorOpenerButton {

		public Opener() {
			init();
		}

		private void init() {

			addComponent(ProjexViewProvider.Views.REPORTEDITOR, "report");

		}
	}

	/**
	 * 
	 */
	public ReportRegistrationView() {
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

			query = new Reports();
			sqlContainer = new OracleContainer(query);
			sqlContainer.addOrderBy(new OrderBy("REPORTNAME",true));
			table.setContainerDataSource(sqlContainer);
			table.configure();

		} catch (SQLException e) {
			logger.error("Error setting ReportRegistrationView query", e);
		}

	}

	protected void init() {

		if (User.getUser() != null) {
			st = C10N.get(ReportEditorText.class, User.getUser().getUserLocale());
		} else {
			st = C10N.get(ReportEditorText.class, Locale.ENGLISH);
		}

		screendescription = new Label("<h1>" + st.componentName() + "</h1>", ContentMode.HTML) {
			{
				addStyleName("projectlisting_label");
			}
		};

		table = new StandardTable() {
			{
				add(new TableColumn("REPORTNAME", st.reportName()).setWidth(200));
				add(new TableColumn("DESCRIPTION", st.description()).setWidth(200));
				add(new TableColumn("CATEGORY",st.category()));
				add(new TableColumn("HELPTEXT", st.helpText()).setCollapsed(true));
				add(new TableColumn("ISSCREENREPORT", st.isScreenReport()).setCollapsed(true));
				add(new TableColumn("SCREENNAME", st.screenName()).setWidth(100));
				add(new TableColumn("CAMPUS", st.campus()));
				add(new TableColumn("ISALLCAMPUS", st.isAllCampus()));
				add(new TableColumn("ISACTIVE", st.isActive()));
				add(new TableColumn("REQUESTEDBYPERSONNAME",st.requestedBy()).setCollapsed(true));
				add(new TableColumn("REQUESTED",st.requested()).setCollapsed(true));
				add(new TableColumn("MODIFICATIONNUMBER", st.modificationNumber()).setCollapsed(true));
				add(new TableColumn("REGISTERED",st.modification().registered()).setCollapsed(true));
				add(new TableColumn("REGISTEREDBYUSERNAME",st.modification().registeredBy()).setCollapsed(true));
				add(new TableColumn("LASTRAN",st.lastRan()));
				add(new TableColumn("RUNNUMBER", st.runNumber()));
				add(new TableColumn("ISSLOWRUNNING", st.slowRunning()));
				setContextHelp(st.contextHelp());
			}
		};

		addButton = new AddNavigatorButton() {
			{
				
				setDescription(st.addButton_help());
				
				addClickListener(new Button.ClickListener() {

					@Override
					public void buttonClick(ClickEvent event) {

						Item item = new PropertysetItem();

						item.addItemProperty("ROWSTAMP", new ObjectProperty<OracleString>(new OracleString("AAAA")));
						item.addItemProperty("ISSCREENREPORT", new ObjectProperty<OracleBoolean>(OracleBoolean.FALSE));
						item.addItemProperty("ISALLCAMPUS", new ObjectProperty<OracleBoolean>(OracleBoolean.TRUE));
						item.addItemProperty("ISACTIVE", new ObjectProperty<OracleBoolean>(OracleBoolean.TRUE));
						
						try {
							query.storeExternalRow(item);
						} catch (SQLException e) {
							logger.error("Could not save new Report", e);
						}
						
						System.err.println("New Id = " + query.getLastId());
						
						getNavigator().navigateTo(ProjexViewProvider.Views.REPORTEDITOR, query.getLastId().toString());
						
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
						addLeftComponent(addButton);
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
