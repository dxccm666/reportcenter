package edu.missouri.cf.projex4.ui.desktop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;

import c10n.C10N;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;

import edu.missouri.cf.data.OracleString;
import edu.missouri.cf.projex4.data.DataNotFoundException;
import edu.missouri.cf.projex4.data.Pools;
import edu.missouri.cf.projex4.data.common.PersonDetails;
import edu.missouri.cf.projex4.data.system.User;
import edu.missouri.cf.projex4.data.system.core.Loggers;
import edu.missouri.cf.projex4.ui.c10n.projects.ProjectInfoComponentText;
import edu.missouri.cf.projex4.ui.desktop.lookups.persons.UserPersonLabel;
import edu.missouri.cf.ui.common.LabelField;

@SuppressWarnings("serial")
public class ProjectInfoComponent extends InfoComponent {

	protected Logger logger = Loggers.getLogger(ProjectInfoComponent.class);

	@PropertyId("CAMPUSNAME")
	protected LabelField campus = new LabelField();

	@PropertyId("PROJECTNUMBER")
	protected LabelField projectnumber = new LabelField();

	@PropertyId("COMBINEDTITLE")
	protected LabelField title = new LabelField();

	@PropertyId("STATUS")
	protected LabelField status = new LabelField();

	@PropertyId("PRIMARYPMPERSONID")
	protected UserPersonLabel pm = new UserPersonLabel(PersonDetails.Type.PERSON);

	@PropertyId("PRIMARYCMPERSONID")
	protected UserPersonLabel cm = new UserPersonLabel(PersonDetails.Type.PERSON);

	@PropertyId("PRIMARYACPERSONID")
	protected UserPersonLabel accountant = new UserPersonLabel(PersonDetails.Type.PERSON);
	
	@PropertyId("PROJECTTYPE")
	protected LabelField projecttype = new LabelField();

	private ProjectInfoComponentText st;

	public ProjectInfoComponent() {

		super("projectinfo");

		st = C10N.get(ProjectInfoComponentText.class, User.getUser().getUserLocale());

		addStyleName("projectinfo");
		addStyleName("projectinfo-block");
		addComponent(campus, "projectcampus");
		addComponent(projectnumber, "projectnumber");
		addComponent(title, "projecttitle");
		addComponent(status, "projectstatus");
		addComponent(projecttype,"projecttype");
		addComponent(pm, "primarypm");
		addComponent(cm, "primarycm");
		addComponent(accountant, "primaryac");

		pm.setCaption("Primary Project Manager");
		cm.setCaption("Primary Construction Manager");
		accountant.setCaption("Primary Accountant");
		projecttype.setCaption("Project Type");

	}

	@Override
	public void setItemDataSource(Item item) {
		super.setItemDataSource(item);
		if (item != null) {
			
			pm.setVisible(item.getItemProperty("PRIMARYPMPERSONID") != null);
			cm.setVisible(item.getItemProperty("PRIMARYCMPERSONID") != null);
			accountant.setVisible(item.getItemProperty("PRIMARYACPERSONID") != null
					&& item.getItemProperty("PRIMARYACPERSONID").getValue() != null);
		}
	}

	@Override
	public Item getItemDataSource(FIELD field, String value) {

		if (value == null) {
			return null;
		}

		Connection conn = null;
		try {
			conn = Pools.getConnection(Pools.Names.PROJEX);

			try (PreparedStatement stmt = getPreparedStatement(conn, field.toString(), value); ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {

					final PropertysetItem data = new PropertysetItem() {
						{

							addItemProperty("ID", new ObjectProperty<String>(rs.getString("ID")));
							addItemProperty("PROJECTNUMBER", new ObjectProperty<String>(rs.getString("PROJECTNUMBER")));
							addItemProperty("COMBINEDTITLE", new ObjectProperty<String>(rs.getString("COMBINEDTITLE")));
							addItemProperty("CAMPUSID", new ObjectProperty<String>(rs.getString("CAMPUSID")));
							addItemProperty("CAMPUSNAME", new ObjectProperty<String>(rs.getString("CAMPUSNAME")));
							addItemProperty("STATUS", new ObjectProperty<String>(rs.getString("STATUS")));

							if (rs.getString("PROJECTTYPE") != null) {
								addItemProperty("PROJECTTYPE", new ObjectProperty<String>(rs.getString("PROJECTTYPE")));
							}

							if (rs.getString("PROJECTMETHOD") != null) {
								addItemProperty("PROJECTMETHOD", new ObjectProperty<String>(rs.getString("PROJECTMETHOD")));
							}

							addItemProperty("UUID", new ObjectProperty<String>(rs.getString("UUID")));

							if (rs.getString("PRIMARYPMPERSONID") != null) {
								addItemProperty("PRIMARYPMPERSONID",
										new ObjectProperty<OracleString>(new OracleString(rs.getString("PRIMARYPMPERSONID"))));
								addItemProperty("PRIMARYPMNAME", new ObjectProperty<String>(rs.getString("PRIMARYPMNAME")));
							}

							if (rs.getString("PRIMARYCMPERSONID") != null) {
								addItemProperty("PRIMARYCMPERSONID",
										new ObjectProperty<OracleString>(new OracleString(rs.getString("PRIMARYCMPERSONID"))));
								addItemProperty("PRIMARYCMNAME", new ObjectProperty<String>(rs.getString("PRIMARYCMNAME")));
							}

							if (rs.getString("PRIMARYACPERSONID") != null) {
								addItemProperty("PRIMARYACPERSONID",
										new ObjectProperty<OracleString>(new OracleString(rs.getString("PRIMARYACPERSONID"))));
								addItemProperty("PRIMARYACNAME", new ObjectProperty<String>(rs.getString("PRIMARYACNAME")));
							}

						}
					};

					objectName = st.getProjectName(rs.getString("CAMPUSNAME"), rs.getString("PROJECTNUMBER"),
							rs.getString("TITLELOCATION"), rs.getString("TITLE"));

					return data;

				} else {
					throw new DataNotFoundException("Project not found for " + field + " = " + value);
				}
			}

		} catch (SQLException | DataNotFoundException e) {
			logger.error("Error retrieving ProjectInfo data ", e);
		} finally {
			Pools.releaseConnection(Pools.Names.PROJEX, conn);
		}

		return null;

	}

	/**
	 * Set the item datasource by getting the data from the database with the
	 * uuid
	 * 
	 * @param uuid
	 *            uuid of the project
	 * @param field
	 *            uuid or id
	 * @author reynoldsjj
	 * @throws DataNotFoundException
	 * @throws SQLException
	 */
	public void setItemDataSource(FIELD field, String value) {

		if (value == null) {
			return;
		}

		Item data = getItemDataSource(field, value);
		setItemDataSource(data);

	}

	private PreparedStatement getPreparedStatement(Connection conn, String field, String value) throws SQLException {

		PreparedStatement stmt = conn.prepareStatement("select * from projectinfo where " + field + " = ?");
		stmt.setString(1, value);
		return stmt;

	}

}
