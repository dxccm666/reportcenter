package edu.missouri.cf.projex4.ui.desktop.configuration.users;

import java.sql.SQLException;

import com.vaadin.data.util.sqlcontainer.OracleContainer;
import com.vaadin.ui.ComboBox;

import edu.missouri.cf.projex4.data.OracleStringToRowIdConverter;
import edu.missouri.operations.reportcenter.data.SecurityGroups;

@SuppressWarnings("serial")
public class SecurityGroupIdComboBox extends ComboBox {

	private String captionPropertyId;
	private String dataPropertyId;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SecurityGroupIdComboBox() {
		setConverter(new OracleStringToRowIdConverter());
		setImmediate(true);
		setData();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SecurityGroupIdComboBox(String caption) {
		super(caption);
		setConverter(new OracleStringToRowIdConverter());
		setImmediate(true);
		setData();
	}

	private void setData() {

		try {

			this.captionPropertyId = "SECURITYGROUPNAME";
			this.dataPropertyId = "ID";
			
			OracleContainer sql = new OracleContainer(new SecurityGroups());
			sql.overrideType("SECURITYGROUPNAME", String.class);
			sql.overrideType("ID", String.class);
			
			setContainerDataSource(sql);
			
			setItemCaptionMode(ItemCaptionMode.PROPERTY);
			setItemCaptionPropertyId("SECURITYGROUPNAME");

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @return the captionProperty
	 */
	public String getCaptionPropertyId() {
		return captionPropertyId;
	}

	/**
	 * @return the dataProperty
	 */
	public String getDataPropertyId() {
		return dataPropertyId;
	}

}
