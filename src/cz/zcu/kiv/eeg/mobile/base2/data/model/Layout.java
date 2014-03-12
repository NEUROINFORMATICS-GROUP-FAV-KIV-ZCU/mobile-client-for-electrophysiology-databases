package cz.zcu.kiv.eeg.mobile.base2.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Layout.TABLE_NAME)
public class Layout {
	public static final String TABLE_NAME = "layouts";
	public static final String FK_ID_FORM = "form_id";

	@DatabaseField(id = true, columnName = "name_id")
	private String name;

	//@DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_FORM)
	//private Form form;

	@DatabaseField(columnName = "xml_data")
	private String xmlData;

	public Layout() {
		super();
	}
	
	public Layout(String name, String xmlData) {
		super();
		this.name = name;	
		this.xmlData = xmlData;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getXmlData() {
		return xmlData;
	}

	public void setXmlData(String xmlData) {
		this.xmlData = xmlData;
	}
}
