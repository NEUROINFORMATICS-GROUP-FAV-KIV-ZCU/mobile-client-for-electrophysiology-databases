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
	public static final String FK_ID_ROOT_LAYOUT = "root_layout_id";
	//public static final String INDEX_NAME = "layout_name_idx";
	public static final String LAYOUT_NAME = "layoutName";
	public static final String LAYOUT_ID = "layoutId";
	public static final String FK_ID_FIELD_PREVIEW_MAJOR = "field_id_prew_major";
	public static final String FK_ID_FIELD_PREVIEW_MINOR= "field_id_prew_minor";

	/*@DatabaseField(generatedId = true)
	private int id;*/
	
	@DatabaseField(id = true, columnName = "name_id")
	@Element(name = LAYOUT_NAME)
	private String name;
	
	/*@DatabaseField(foreign = true, columnName = FK_ID_ROOT_LAYOUT)
	private Layout rootLayout;*/
	
	@DatabaseField(foreign = true, columnName = FK_ID_FORM)
	private Form rootForm;
	
	@DatabaseField(columnName = "xml_data")
	private String xmlData;
	
	@DatabaseField(foreign = true, canBeNull = true, columnName = FK_ID_FIELD_PREVIEW_MAJOR)
	private Field previewMajor;

	@DatabaseField(foreign = true, canBeNull = true, columnName = FK_ID_FIELD_PREVIEW_MINOR)
	private Field previewMinor;

	// pouze pomocná proměnná při získávání layoutu z ws
	@Element
	private String formName;

	public Layout() {
		super();
	}
	
	public Layout(Form rootForm) {
		super();
		this.rootForm = rootForm;
	}

	public Layout(String name) {
		super();
		this.name = name;
	}

	public Layout(String name, String xmlData, Form rootForm, Field major, Field minor) {
		super();
		this.name = name;
		this.xmlData = xmlData;
		//this.rootLayout = rootLayout;
		this.rootForm = rootForm;
		this.previewMajor = major;
		this.previewMinor = minor;
	}

	/*public Layout getRootLayout() {
		return rootLayout;
	}

	public void setRootLayout(Layout rootLayout) {
		this.rootLayout = rootLayout;
	}*/

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

	public Field getPreviewMajor() {
		return previewMajor;
	}

	public void setPreviewMajor(Field previewMajor) {
		this.previewMajor = previewMajor;
	}

	public Field getPreviewMinor() {
		return previewMinor;
	}

	public void setPreviewMinor(Field previewMinor) {
		this.previewMinor = previewMinor;
	}
}
