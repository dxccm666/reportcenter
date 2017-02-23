package edu.missouri.cf.projex4.ui.desktop.configuration.users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;

import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;

import edu.missouri.cf.data.OracleString;
import edu.missouri.cf.projex4.data.Pools;
import edu.missouri.cf.projex4.data.SecurityGroupIdtoStringConverter;
import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.data.system.core.securitygroups.SecurityGroupUsersView;
import edu.missouri.cf.projex4.ui.common.TableColumn;
import edu.missouri.cf.projex4.ui.common.system.AdvancedTableEditComponent;
import edu.missouri.cf.projex4.ui.desktop.TableDependentProjexEditor;

@SuppressWarnings("serial")
public class UserSecurityGroupsComponent extends TableDependentProjexEditor {

	private static Logger logger = Loggers.getLogger(UserSecurityGroupsComponent.class);

	private SecurityGroupUsersView query;
	private AdvancedTableEditComponent securityTableEditComponent;

	public UserSecurityGroupsComponent() {

		setAddingPermitted(true);
		setOverrideSecurityChecks(true);

		table.add(new TableColumn("SECURITYGROUPID", "Security Group")
				.setConverter(new SecurityGroupIdtoStringConverter()).setEditorClass(SecurityGroupIdComboBox.class));

		table.setContextHelp("");
		table.setMultiSelect(false);
		table.setSizeFull();

		securityTableEditComponent = new AdvancedTableEditComponent();
		securityTableEditComponent.setAttachedTable(table);

	}

	public void setData(String userId) {

		query = new SecurityGroupUsersView();
		query.setUserId(userId);

		try {

			sqlContainer = new OracleContainer(query);
			sqlContainer.addOrderBy(new OrderBy("ID", true));
			table.setContainerDataSource(sqlContainer);
			table.configure();

		} catch (SQLException e) {

			if (logger.isErrorEnabled()) {
				logger.error(e.getSQLState());
			}

		}

		addButton.addClickListener(new Button.ClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {
				Item item = sqlContainer.getItem(sqlContainer.addItem());
				item.getItemProperty("ROWSTAMP").setValue(new OracleString("AAAA"));
				item.getItemProperty("USERID").setValue(new OracleString(userId));

			}

		});

		deleteButton.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				Object id = table.getValue();
				if (id != null) {
					System.err.println("^^^^^^^^^^^^^^^^^^^^^^^^ security = " + id.toString());
					Connection conn = null;

					try {
						conn = Pools.getConnection(Pools.Names.PROJEX);

						try (PreparedStatement stmt = conn
								.prepareStatement("delete from securitygroupusers " + "where id = ?")) {

							stmt.setString(1, id.toString());
							stmt.executeUpdate();
							conn.commit();
							sqlContainer.refresh();

						}
					} catch (SQLException sqle) {
						if (logger.isErrorEnabled()) {
							logger.error("Could not retrieve userid from userLogin {}", id.toString(), sqle);
						}
					} finally {
						Pools.releaseConnection(Pools.Names.PROJEX, conn);
					}

				}

			}
		});

	}

	@Override
	public void setData(ObjectData refObjectData) {
		throw new UnsupportedOperationException("ObjectData is not accepted");
	}

}
