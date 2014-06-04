package cz.zcu.kiv.eeg.mobile.base2.data.model;

import java.util.List;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 
 * @author Jaroslav Hošek
 * 
 * zatím pouze pro uložení hodnot comboboxu
 * 
 */
@DatabaseTable(tableName = FieldValue.TABLE_NAME)
public class FieldValue {

	public static final String TABLE_NAME = "field_values";
	public static final String FK_ID_FIELD = "field_id";
	public static final String INDEX_NAME = "field_values_value_field_idx";

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(uniqueIndexName = INDEX_NAME)
	private String value;


	@DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_FIELD, uniqueIndexName = INDEX_NAME)
	private Field field;

	public FieldValue() {
		super();
	}
	
	public FieldValue(String value, Field field) {
		super();
		this.value = value;
		this.field = field;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}
}
