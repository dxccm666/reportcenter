/**
 * 
 */
package edu.missouri.cf.projex4.ui.desktop.configuration.securitygroups;

import java.sql.SQLException;
import java.util.Locale;

import org.slf4j.Logger;

import c10n.C10N;

import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.data.system.User;
import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.ui.common.TableColumn;
import edu.missouri.cf.projex4.ui.common.system.AdvancedTableEditComponent;
import edu.missouri.cf.projex4.ui.common.system.ExportButton;
import edu.missouri.cf.projex4.ui.desktop.NavigatorOpenerButton;
import edu.missouri.cf.projex4.ui.desktop.StandardTable;
import edu.missouri.cf.projex4.ui.desktop.TableControlLayout;
import edu.missouri.cf.projex4.ui.desktop.TopBarView;
import edu.missouri.operations.reportcenter.data.SecurityGroups;
import edu.missouri.operations.ui.desktop.buttons.NavigatorButton;

/**
 * @author graumannc
 * 
 */
@SuppressWarnings("serial")
public class SecurityGroupsView extends TopBarView {

	private final Logger logger = Loggers.getLogger(SecurityGroupsView.class);

	boolean allowswitch = true;

	private Label screendescription;

	private ExportButton exportButton;

	private StandardTable table;

	private AdvancedTableEditComponent editControls;

	private SecurityGroupsText st;

	private Opener opener;
	private AddNavigatorButton addButton;
	
	SecurityGroups query;
	OracleContainer sqlContainer;
	
	NewSecurityGroupnameWindow newSecurityWin = new NewSecurityGroupnameWindow();
	/**
	 * 
	 */
	public SecurityGroupsView() {
		super();
	}
	
	@Override
	public void attach() {
		super.attach();
		init();
		layout();
	}

	class Opener extends NavigatorOpenerButton {

		public Opener() {
			init();
		}

		private void init() {

			addComponent(ProjexViewProvider.Views.SECURITYGROUPEDITOR, "Security Group");

		}

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
		logger.debug("SecurityGroupsView enter called.");

		try {

			query = new SecurityGroups();
			query.setOrderBy(new OrderBy("DESCRIPTION", true));
			sqlContainer = new OracleContainer(query);
			table.setContainerDataSource(sqlContainer);
			table.configure();

		} catch (SQLException e) {
			logger.error("Error setting query", e);
		}

	}

	protected void init() {

		// decide display languages
		if (User.getUser() != null) {
			st = C10N.get(SecurityGroupsText.class, User.getUser().getUserLocale());
		} else {
			st = C10N.get(SecurityGroupsText.class, Locale.ENGLISH);
		}

		screendescription = new Label("<h1>" + st.screenName() + "</h1>", ContentMode.HTML);
		screendescription.addStyleName("projectlisting_label");

		// These should be different
		// editControls = new AdvancedTableEditComponent();

		opener = new Opener();
		addButton = new AddNavigatorButton();
		exportButton = new ExportButton();

		table = new StandardTable() {
			{
				add(new TableColumn("SECURITYGROUPNAME", st.securityGroupName()));
				add(new TableColumn("DESCRIPTION", st.description()));
				add(new TableColumn("ISACTIVE", st.isActive()));
				add(new TableColumn("ISSYSTEMSECURITYGROUP", st.isSystemSecurityGroup()));
				setContextHelp(st.contextHelp());
			}
		};
		
		opener.setTable(table);
		// editControls.setAttachedTable(table);
		exportButton.setAttachedTable(table);

	}

	private void layout() {

		VerticalLayout wrapper = new VerticalLayout() {
			{
				setMargin(true);
				setSpacing(true);
				setSizeFull();
				addComponent(screendescription);
				addComponent(new TableControlLayout() {
					{
						addLeftComponent(addButton);
						addLeftComponent(opener);
						addRightComponent(exportButton);
					}
				});
				addComponent(table);
				setExpandRatio(table, 1.0f);
			}
		};
		addComponent(wrapper);
		setExpandRatio(wrapper, 1.0f);

	}
	
	public class AddNavigatorButton extends NavigatorButton implements Button.ClickListener{

		public AddNavigatorButton() {
			
			super(ProjexViewProvider.Views.SECURITYGROUPEDITOR, "add");
			setImmediate(true);
			addClickListener(this);
			setDescription("add new security group");
			setIcon(new ThemeResource("icons/chalkwork/basic/add_16x16.png"));
			addStyleName("borderless");

		}

		@Override
		public void buttonClick(ClickEvent event) {
			newSecurityWin.reset();
			newSecurityWin.addWindow();
			
		}
		
	}

}
