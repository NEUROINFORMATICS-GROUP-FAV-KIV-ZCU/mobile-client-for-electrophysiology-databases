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
	public static final String FK_ID_SUBLAYOUT = "sublayout_id";
	public static final String FK_ID_FIELD_PREVIEW_MAJOR = "field_id_prew_major";
	public static final String FK_ID_FIELD_PREVIEW_MINOR= "field_id_prew_minor";
	public static final String INDEX_NAME = "property_field_layout_idx";

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_FIELD, uniqueIndexName = INDEX_NAME)
	private Field field;

	@DatabaseField(foreign = true, canBeNull = false, columnName = FK_ID_LAYOUT, uniqueIndexName = INDEX_NAME)
	private Layout layout;
	
	@DatabaseField(foreign = true, canBeNull = true, columnName = FK_ID_SUBLAYOUT)
	private Layout subLayout;
	
	@DatabaseField(foreign = true, canBeNull = true, columnName = FK_ID_FIELD_PREVIEW_MAJOR)
	private Field previewMajor;
	
	@DatabaseField(foreign = true, canBeNull = true, columnName = FK_ID_FIELD_PREVIEW_MINOR)
	private Field previewMinor;
	
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
		
	@DatabaseField
	private int cardinality;

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

	public Layout getSubLayout() {
		return subLayout;
	}

	public void setSubLayout(Layout subLayout) {
		this.subLayout = subLayout;
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

	public int getCardinality() {
		return cardinality;
	}

	public void setCardinality(int cardinality) {
		this.cardinality = cardinality;
	}
}
