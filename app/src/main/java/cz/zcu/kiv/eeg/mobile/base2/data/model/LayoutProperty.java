package cz.zcu.kiv.eeg.mobile.base2.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 
 * @author Jaroslav Hošek
 * 
 */
@DatabaseTable(tableName = LayoutProperty.TABLE_NAME)
public class LayoutProperty {
	public static final String TABLE_NAME = "properties";
	public static final String FK_ID_FIELD = "field_id";
	public static final String FK_ID_LAYOUT = "layout_id";
	public static final String INDEX_NAME = "property_field_layout_idx";

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_FIELD, uniqueIndexName = INDEX_NAME)
	private Field field;

	@DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_LAYOUT, uniqueIndexName = INDEX_NAME)
	private Layout layout;
	
	@DatabaseField
	private String label;
	
	@DatabaseField
	private int idNode;
	
	@DatabaseField
	private int idTop;
	
	@DatabaseField
	private int idBottom;
	
	@DatabaseField
	private int idLeft;
	
	@DatabaseField
	private int idRight;
	
	@DatabaseField
	private int weight;

	public LayoutProperty() {
		super();
	}
	
	public LayoutProperty(Field field, Layout layout) {
		super();
		this.field = field;
		this.layout = layout;
		weight = 100;
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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getIdNode() {
		return idNode;
	}

	public void setIdNode(int idNode) {
		this.idNode = idNode;
	}

	public int getIdTop() {
		return idTop;
	}

	public void setIdTop(int idTop) {
		this.idTop = idTop;
	}

	public int getIdBottom() {
		return idBottom;
	}

	public void setIdBottom(int idBottom) {
		this.idBottom = idBottom;
	}

	public int getIdLeft() {
		return idLeft;
	}

	public void setIdLeft(int idLeft) {
		this.idLeft = idLeft;
	}

	public int getIdRight() {
		return idRight;
	}

	public void setIdRight(int idRight) {
		this.idRight = idRight;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
}
