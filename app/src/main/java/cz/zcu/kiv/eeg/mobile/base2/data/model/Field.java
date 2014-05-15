package cz.zcu.kiv.eeg.mobile.base2.data.model;

import java.util.List;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
@DatabaseTable(tableName = Field.TABLE_NAME)
public class Field {

	public static final String TABLE_NAME = "fields";
	public static final String FK_ID_FORM = "form_id";
	public static final String INDEX_NAME = "field_form_name_idx";
	public static final String FIELD_ID = "field_id";

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(uniqueIndexName = INDEX_NAME)
	private String name;

	@DatabaseField
	private String type;

	@DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_FORM, uniqueIndexName = INDEX_NAME)
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
