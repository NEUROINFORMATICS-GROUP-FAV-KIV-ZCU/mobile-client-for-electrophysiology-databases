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
	public static final String FK_ID_FIELD_DESC1 = "field_id_desc1";
	public static final String FK_ID_FIELD_DESC2 = "field_id_desc2";
	public static final String FK_ID_FORM = "form_id";
	public static final String INDEX_NAME = "menu_items_name_idx";

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(uniqueIndexName = INDEX_NAME)
	private String name;

	@DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_LAYOUT)
	private Layout layout;

	/*
	 * @DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_FORM) private Form rootForm;
	 */

	@DatabaseField(foreign = true, canBeNull = true, columnName = FK_ID_FIELD)
	private Field fieldID;

	@DatabaseField(foreign = true, canBeNull = true, columnName = FK_ID_FIELD_TMP)
	private Field fieldTmp;

	@DatabaseField(foreign = true, canBeNull = true, columnName = FK_ID_FIELD_DESC1)
	private Field fieldDescription1;

	@DatabaseField(foreign = true, canBeNull = true, columnName = FK_ID_FIELD_DESC2)
	private Field fieldDescription2;

	public MenuItems() {
		super();
	}

	public MenuItems(String name, Layout layout, Field fieldDescription1, Field fieldDescription2) {
		super();
		this.name = name;
		this.layout = layout;
		this.fieldDescription1 = fieldDescription1;
		this.fieldDescription2 = fieldDescription2;
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

	public Field getFieldDescription1() {
		return fieldDescription1;
	}

	public void setFieldDescription1(Field fieldDescription1) {
		this.fieldDescription1 = fieldDescription1;
	}

	public Field getFieldDescription2() {
		return fieldDescription2;
	}

	public void setFieldDescription2(Field fieldDescription2) {
		this.fieldDescription2 = fieldDescription2;
	}
}
