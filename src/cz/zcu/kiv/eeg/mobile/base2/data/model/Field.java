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
	public static final String DATA_TYPE = "data_type";

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(uniqueIndexName = INDEX_NAME)
	private String name;

	@DatabaseField
	private String type;
	
	@DatabaseField(columnName = DATA_TYPE)
	private String dataType;

	@DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_FORM, uniqueIndexName = INDEX_NAME)
	private Form form;
	
	@DatabaseField
	private int minLength;
	
	@DatabaseField
	private int maxLength;
	
	@DatabaseField
	private int minValue;
	
	@DatabaseField
	private int maxValue;
	
	@DatabaseField
	private String defaultValue;
	
	@DatabaseField
	private int action;

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

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

	public int getMinLength() {
		return minLength;
	}

	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public int getMinValue() {
		return minValue;
	}

	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}
}
