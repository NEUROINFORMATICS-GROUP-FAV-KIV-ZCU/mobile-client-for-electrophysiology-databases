package cz.zcu.kiv.eeg.mobile.base2.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
@DatabaseTable(tableName = MenuItems.TABLE_NAME)
public class MenuItems {

	public static final String TABLE_NAME = "menu_items";
	public static final String FK_ID_LAYOUT = "layout_id";
	public static final String FK_ID_FIELD = "field_id";
	public static final String FK_ID_FIELD_TMP = "field_id_tmp";
	public static final String FK_ID_FIELD_PREVIEW_MAJOR = "field_id_prew_major";
	public static final String FK_ID_FIELD_PREVIEW_MINOR= "field_id_prew_minor";
	public static final String FK_ID_FORM = "form_id";
	public static final String INDEX_NAME = "menu_items_name_idx";

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(uniqueIndexName = INDEX_NAME)
	private String name;

	@DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_LAYOUT)
	private Layout layout;

	@DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_FORM)
	private Form rootForm;

	@DatabaseField(foreign = true, canBeNull = true, columnName = FK_ID_FIELD)
	private Field fieldID;

	@DatabaseField(foreign = true, canBeNull = true, columnName = FK_ID_FIELD_TMP)
	private Field fieldTmp;

	@DatabaseField(foreign = true, canBeNull = true, columnName = FK_ID_FIELD_PREVIEW_MAJOR)
	private Field previewMajor;

	@DatabaseField(foreign = true, canBeNull = true, columnName = FK_ID_FIELD_PREVIEW_MINOR)
	private Field previewMinor;

	public MenuItems() {
		super();
	}

	public MenuItems(String name, Layout layout, Form rootForm, Field fieldDescription1, Field fieldDescription2) {
		super();
		this.name = name;
		this.layout = layout;
		this.previewMajor = fieldDescription1;
		this.previewMinor = fieldDescription2;
		this.rootForm = rootForm;
	}

	public MenuItems(String name, Layout layout) {
		super();
		this.name = name;
		this.layout = layout;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Layout getLayout() {
		return layout;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	public Form getRootForm() {
		return rootForm;
	}

	public void setRootForm(Form rootForm) {
		this.rootForm = rootForm;
	}

	public Field getFieldID() {
		return fieldID;
	}

	public void setFieldID(Field fieldID) {
		this.fieldID = fieldID;
	}

	public Field getFieldTmp() {
		return fieldTmp;
	}

	public void setFieldTmp(Field fieldTmp) {
		this.fieldTmp = fieldTmp;
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
