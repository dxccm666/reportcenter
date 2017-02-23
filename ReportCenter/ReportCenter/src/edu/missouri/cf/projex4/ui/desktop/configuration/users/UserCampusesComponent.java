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
import edu.missouri.cf.projex4.data.CampusIdToStringConverter;
import edu.missouri.cf.projex4.data.Pools;
import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.data.system.core.objects.ObjectData;
import edu.missouri.cf.projex4.data.system.core.users.CampusUserDetails;
import edu.missouri.cf.projex4.ui.common.TableColumn;
import edu.missouri.cf.projex4.ui.desktop.TableDependentProjexEditor;
import edu.missouri.cf.projex4.ui.desktop.configuration.campuses.CampusIdComboBox;

@SuppressWarnings("serial")
public class UserCampusesComponent extends TableDependentProjexEditor {

	private static Logger logger = Loggers.getLogger(UserCampusesComponent.class);

	private CampusUserDetails query;

	public UserCampusesComponent() {

		setAddingPermitted(true);
		setOverrideSecurityChecks(true);

		table.add(new TableColumn("CAMPUSID", "Campus").setConverter(new CampusIdToStringConverter()).setEditorClass(CampusIdComboBox.class));
		table.setContextHelp("");
		table.setMultiSelect(false);
		table.setSizeFull();

	}

	String userId;

	public void setData(String userId) {

		this.userId = userId;

		query = new CampusUserDetails();
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

					Connection conn = null;

					try {
						conn = Pools.getConnection(Pools.Names.PROJEX);

						try (PreparedStatement stmt = conn
								.prepareStatement("delete from campususers " + "where id = ?")) {

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
