package cz.zcu.kiv.eeg.mobile.base2.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
@DatabaseTable(tableName = FieldLayouts.TABLE_NAME)
public class FieldLayouts {
	public static final String TABLE_NAME = "field_layouts";
	public static final String FK_ID_FIELD = "field_id";
	public static final String FK_ID_LAYOUT = "layout_id";
	public static final String INDEX_NAME = "field_layouts_fieldlayout_idx";

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_FIELD, uniqueIndexName = INDEX_NAME)
	private Field field;

	@DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_LAYOUT, uniqueIndexName = INDEX_NAME)
	private Layout layout;

	public FieldLayouts() {
		super();
	}

	public FieldLayouts(Field field, Layout layout) {
		super();
		this.field = field;
		this.layout = layout;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public Layout getLayout() {
		return layout;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
	}
}
