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
        if (null == field) properties.put("field", null);
        else properties.put("field", field.get());
        if (null == layout) properties.put("layout", null);
        else properties.put("layout", layout.get());
        if (null == subLayout) properties.put("subLayout", null);
        else properties.put("subLayout", subLayout.get());
        if (null == previewMajor) properties.put("previewMajor", null);
        else properties.put("previewMajor", previewMajor.get());
        if (null == previewMinor) properties.put("previewMinor", null);
        else properties.put("previewMinor", previewMinor.get());
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
        id = (Integer) properties.get("id");
        Object object = properties.get("field");
        if (null == object) field = null;
        else field = new Field((Map<String, Object>) object);
        object = properties.get("layout");
        if (null == object) layout = null;
        else layout = new Layout((Map<String, Object>) object);
        object = properties.get("subLayout");
        if (null == object) subLayout = null;
        else subLayout = new Layout((Map<String, Object>) object);
        object = properties.get("previewMajor");
        if (null == object) previewMajor = null;
        else previewMajor = new Field((Map<String, Object>) object);
        object = properties.get("previewMinor");
        if (null == object) previewMinor = null;
        else previewMinor = new Field((Map<String, Object>) object);
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
