package cz.zcu.kiv.eeg.mobile.base2.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Field.TABLE_NAME)
public class Field {

	public static final String TABLE_NAME = "fields";
	public static final String FK_ID_FORM = "form_id";

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(uniqueIndexName = "field_form_name_idx")
	private String name;

	@DatabaseField
	private String type;

	@DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_FORM, uniqueIndexName = "field_form_name_idx")
	private Form form;

	public Field() {
		super();
	}

	public Field(String name, String type, Form form) {
		super();
		this.name = name;
		this.type = type;
		this.form = form;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}
}
