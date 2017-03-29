package edu.missouri.cf.projex4.ui.desktop.configuration.users;

import java.sql.SQLException;
import java.util.Collection;

import org.vaadin.hene.popupbutton.PopupButton;

import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import edu.missouri.cf.projex4.ProjexViewProvider;
import edu.missouri.cf.projex4.ui.common.TableColumn;
import edu.missouri.cf.projex4.ui.common.system.ExportButton;
import edu.missouri.cf.projex4.ui.desktop.NavigatorOpenerButton;
import edu.missouri.cf.projex4.ui.desktop.StandardTable;
import edu.missouri.cf.projex4.ui.desktop.TableClickListener;
import edu.missouri.cf.projex4.ui.desktop.TableControlLayout;
import edu.missouri.cf.projex4.ui.desktop.TopBarView;
import edu.missouri.operations.reportcenter.data.Users;
import edu.missouri.operations.ui.desktop.buttons.NavigatorButton;

@SuppressWarnings("serial")
public class UsersView extends TopBarView {

	private StandardTable filterTable_1;
	// private Button addButton;
	private UserOpener openButton;
	private Users query;
	private Label screendescription;
	private ExportButton exportButton;

	private Button addButton;

	AddNewExternalUserView addNewExternalView = new AddNewExternalUserView();
	AddNewPSUserView addPSView = new AddNewPSUserView();

	public UsersView() {
		super();
	}

	@Override
	public void attach() {
		super.attach();
		init();
		layout();
	}

	public class UserOpener extends NavigatorOpenerButton {

		class UserTableClickListener implements TableClickListener {

			protected NavigatorButton navigatorButton;

			@Override
			public void setNavigatorButton(NavigatorButton navigatorButton) {
				this.navigatorButton = navigatorButton;
			}

			@Override
			public void setUriFragments(Item selectedItem) {
				// TODO Auto-generated method stub

			}

			@SuppressWarnings("deprecation")
			@Override
			public void buttonClick(ClickEvent event) {
				if (navigatorButton.getView() != null) {

					if (navigatorButton.getTable() != null) {

						String id = null;

						if (navigatorButton.getTable().isMultiSelect()) {

							if (navigatorButton.getTable().getValue() instanceof Collection<?>) {

								Collection<?> col = (Collection<?>) navigatorButton.getTable().getValue();

								if (col.isEmpty()) {
									Notification.show("Select a value in the table!");
									return;
								}

								id = col.iterator().next().toString();
							}

						} else {

							if (navigatorButton.getTable().getValue() != null) {
								id = navigatorButton.getTable().getValue().toString();
							} else {
								Notification.show("Select a value in the table!");
								return;
							}

						}

						Item selectedItem = navigatorButton.getTable().getItem(new RowId(new Object[] { id }));

						if (selectedItem == null) {

							Notification.show("Selected value is no longer in the table!");
							return;

						}

						String itemid = selectedItem.getItemProperty("ID").getValue().toString();
						navigatorButton.setUriFragment(itemid);

						if (!event.isCtrlKey()) {
							navigatorButton.navigateTo(navigatorButton.getUriFragment());
						} else {
							Page.getCurrent().open(new ExternalResource(navigatorButton.getUrl(navigatorButton.getUriFragment())), "_blank", false);
						}

					}

				}
			}

		}

		public UserOpener() {
			init();
		}

		public UserOpener(String caption) {
			super(caption);
			init();
		}

		private void init() {
			addComponent(ProjexViewProvider.Views.USEREDITOR, "user details", new UserTableClickListener());
		}

	}

	private void init() {

		// TODO Change to C10N
		screendescription = new Label("<h1>Users</h1>", ContentMode.HTML);
		screendescription.addStyleName("projectlisting_label");

		addButton = new PopupButton() {
			{

				setCaption("add ...");
				setDescription("add n rows to table");
				setIcon(new ThemeResource("icons/chalkwork/basic/add_16x16.png"));
				addStyleName("borderless");

				final Button addOneButton = new Button() {
					{
						setCaption("add external user");
						addStyleName("borderless");

						addClickListener(new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								addNewExternalView.addWindow();
							}

						});
					}
				};

				final Button addTenButton = new Button() {
					{
						setCaption("add peoplesoft user");
						addStyleName("borderless");

						addClickListener(new Button.ClickListener() {

							@Override
							public void buttonClick(ClickEvent event) {
								addPSView.addWindow();
							}

						});
					}
				};

				setContent(new VerticalLayout() {
					{
						addComponent(addOneButton);
						addComponent(addTenButton);

					}
				});

			}
		};

		openButton = new UserOpener() {
			{
				setImmediate(true);
			}
		};
		exportButton = new ExportButton();

		filterTable_1 = new StandardTable() {
			{
				setSizeFull();
				setContextHelp("");

				add(new TableColumn("ID", "User Id"));
				add(new TableColumn("PERSONID", "Person Id"));
				add(new TableColumn("USERLOGIN", "User Login"));
				add(new TableColumn("FULLNAME", "Full Name"));
				add(new TableColumn("STATUS", "Status"));
				add(new TableColumn("INITIALIZED", "Initialized"));
				add(new TableColumn("FORCEEXPIRATION", "Force Expiration?"));
				add(new TableColumn("USERTYPE", "User Type"));
				add(new TableColumn("ISACTIVE", "Is Active?"));
				add(new TableColumn("NOTIFYBYEMAIL", "Notify By Email?"));
			}
		};

		openButton.setTable(filterTable_1);
		exportButton.setAttachedTable(filterTable_1);

	}

	private void layout() {

		VerticalLayout root = new VerticalLayout() {
			{
				setMargin(true);
				setSpacing(true);
				setSizeFull();
				addComponent(screendescription);
				addComponent(new TableControlLayout() {
					{
						addLeftComponent(addButton);
						addLeftComponent(openButton);
						addRightComponent(exportButton);
					}
				});
				addComponent(filterTable_1);
				setExpandRatio(filterTable_1, 1.0f);
			}
		};

		addComponent(root);
		setExpandRatio(root, 1.0f);
	}

	@Override
	public void enter(ViewChangeEvent event) {

		query = new Users();

		try {

			OracleContainer sqlContainer = new OracleContainer(query);
			sqlContainer.addOrderBy(new OrderBy("USERLOGIN", true));
			filterTable_1.setContainerDataSource(sqlContainer);
			filterTable_1.configure();

		} catch (SQLException e) {
			logger.error(e.getSQLState());
		}

	}

}