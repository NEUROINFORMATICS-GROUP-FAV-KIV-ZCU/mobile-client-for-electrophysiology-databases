package cz.zcu.kiv.eeg.mobile.base2.data.model;

import java.util.HashMap;
import java.util.Map;

import cz.zcu.kiv.eeg.mobile.base2.data.interfaces.NoSQLData;

/**
 * @author Jaroslav Hošek
 * @author Rahul Kadyan, (mail@rahulkadyan.com)
 */

public class LayoutProperty extends NoSQLData {

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

    public LayoutProperty(Map<String, Object> properties) {
        set(properties);
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
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("id", id);
        properties.put("field", field != null ? field.get() : null);
        properties.put("layout", layout != null ? layout.get() : null);
        properties.put("subLayout", subLayout != null ? subLayout.get() : null);
        properties.put("previewMajor", previewMajor != null ? previewMajor.get() : null);
        properties.put("previewMinor", previewMinor != null ? previewMinor.get() : null);
        properties.put("label", label);
        properties.put("idNode", idNode);
        properties.put("idTop", idTop);
        properties.put("idBottom", idBottom);
        properties.put("idLeft", idLeft);
        properties.put("idRight", idRight);
        properties.put("weight", weight);
        properties.put("cardinality", cardinality);
        return properties;
    }

    @Override
    public void set(Map<String, Object> properties) {
        if (null == properties) return;
        id = (Integer) properties.get("id");
        Object object;
        field = (object = properties.get("field")) != null ? new Field((Map<String, Object>) object) : null;
        layout = (object = properties.get("layout")) != null ? new Layout((Map<String, Object>) object) : null;
        subLayout = (object = properties.get("subLayout")) != null ? new Layout((Map<String, Object>) object) : null;
        previewMajor = (object = properties.get("previewMajor")) != null ? new Field((Map<String, Object>) object) : null;
        previewMinor = (object = properties.get("previewMinor")) != null ? new Field((Map<String, Object>) object) : null;
        label = (String) properties.get("label");
        idNode = (Integer) properties.get("idNode");
        idTop = (Integer) properties.get("idTop");
        idBottom = (Integer) properties.get("idBottom");
        idLeft = (Integer) properties.get("idLeft");
        idRight = (Integer) properties.get("idRight");
        weight = (Integer) properties.get("weight");
        cardinality = (Integer) properties.get("cardinality");
    }
}
