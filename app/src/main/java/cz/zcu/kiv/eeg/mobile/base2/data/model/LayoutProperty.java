package cz.zcu.kiv.eeg.mobile.base2.data.model;

import java.util.HashMap;
import java.util.Map;

import cz.zcu.kiv.eeg.mobile.base2.data.interfaces.NoSQLData;

/**
 *
 * @author Jaroslav Hošek
 * @author Rahul Kadyan, (mail@rahulkadyan.com)
 *
 */

public class LayoutProperty implements NoSQLData{

	private int id;

	private Field field;

	private Layout layout;

	private Layout subLayout;

	private Field previewMajor;

	private Field previewMinor;

	private String label;

	private int idNode;

	private int idTop;

	private int idBottom;

	private int idLeft;

	private int idRight;

	private int weight;

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

    @Override
    public Map<String, Object> get() {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("id", id);
        m.put("field", field.get());
        m.put("layout", layout.get());
        m.put("subLayout", subLayout.get());
        m.put("previewMajor", previewMajor.get());
        m.put("previewMinor", previewMinor.get());
        m.put("label", label);
        m.put("idNode", idNode);
        m.put("idTop", idTop);
        m.put("idBottom", idBottom);
        m.put("idLeft", idLeft);
        m.put("idRight", idRight);
        m.put("weight", weight);
        m.put("cardinality", cardinality);
        return m;
    }
}
