package cz.zcu.kiv.eeg.mobile.base2.data.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
@Root(name = Layout.XML_ROOT)
@DatabaseTable(tableName = Layout.TABLE_NAME)
public class Layout {
	public static final String XML_ROOT = "layout";
	public static final String TABLE_NAME = "layouts";
	public static final String FK_ID_FORM = "root_form_id";

	@DatabaseField(id = true, columnName = "name_id")
	@Element(name = "layoutName")
	private String name;

	@DatabaseField(columnName = "xml_data")
	private String xmlData;

	@DatabaseField(foreign = true, columnName = FK_ID_FORM)
	private Form rootForm;

	// pouze pomocná proměnná při získávání layoutu z ws
	@Element
	private String formName;

	public Layout() {
		super();
	}

	public Layout(String name, String xmlData, Form rootForm) {
		super();
		this.name = name;
		this.xmlData = xmlData;
		this.rootForm = rootForm;
	}

	public Layout(Form rootForm) {
		super();
		this.rootForm = rootForm;
	}

	public Layout(String name) {
		super();
		this.name = name;
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

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public Form getRootForm() {
		return rootForm;
	}

	public void setRootForm(Form rootForm) {
		this.rootForm = rootForm;
	}
}
