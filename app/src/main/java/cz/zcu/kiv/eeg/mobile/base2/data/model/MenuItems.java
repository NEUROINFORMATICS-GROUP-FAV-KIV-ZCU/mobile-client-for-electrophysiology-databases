package cz.zcu.kiv.eeg.mobile.base2.data.model;

import java.util.HashMap;
import java.util.Map;

import cz.zcu.kiv.eeg.mobile.base2.data.interfaces.NoSQLData;

/**
 * @author Jaroslav Hošek
 */
public class MenuItems extends NoSQLData {
    public static final String ROOT_MENU = "root_menu";

    private int id;

    private MenuItems parentId;

    private String name;

    private Layout layout;

    private Form rootForm;

    private String icon;

//    private Field fieldID;
//
//    private Field fieldTmp;

    private Field previewMajor;

    private Field previewMinor;

    public MenuItems() {
        super();
    }

    public MenuItems(String name, Layout layout, Form rootForm, Field previewMajor, Field previewMinor, MenuItems parentId) {
        super();
        this.name = name;
        this.layout = layout;
        this.previewMajor = previewMajor;
        this.previewMinor = previewMinor;
        this.rootForm = rootForm;
        this.parentId = parentId;
    }

    public MenuItems(String name, Layout layout) {
        super();
        this.name = name;
        this.layout = layout;
    }

    public MenuItems(String name, String icon) {
        super();
        this.name = name;
        this.icon = icon;
    }

    public MenuItems(Map<String, Object> properties) {
        set(properties);
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

    public Layout getLayout() {
        return layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    public Form getRootForm() {
        return rootForm;
    }

    public void setRootForm(Form rootForm) {
        this.rootForm = rootForm;
    }

//    public Field getFieldID() {
//        return fieldID;
//    }
//
//    public void setFieldID(Field fieldID) {
//        this.fieldID = fieldID;
//    }
//
//    public Field getFieldTmp() {
//        return fieldTmp;
//    }
//
//    public void setFieldTmp(Field fieldTmp) {
//        this.fieldTmp = fieldTmp;
//    }

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

    @Override
    public Map<String, Object> get() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("id", id);
        properties.put("name", name);
        properties.put("parentId", parentId != null ? parentId.get() : null);
        properties.put("layout", layout != null ? layout.get() : null);
        properties.put("rootForm", rootForm != null ? rootForm.get() : null);
//        properties.put("fieldID", fieldID != null ? fieldID.get() : null);
//        properties.put("fieldTmp", fieldTmp != null ? fieldTmp.get() : null);
        properties.put("previewMajor", previewMajor != null ? previewMajor.get() : null);
        properties.put("previewMinor", previewMinor != null ? previewMinor.get() : null);
        properties.put("icon", icon);
        return properties;
    }

    @Override
    public void set(Map<String, Object> properties) {
        if (null == properties) return;
        id = (Integer) properties.get("id");
        name = (String) properties.get("name");
        Object object;
        parentId = (object = properties.get("parentId")) != null ? new MenuItems((Map<String, Object>) object) : null;
        layout = (object = properties.get("layout")) != null ? new Layout((Map<String, Object>) object) : null;
        rootForm = (object = properties.get("rootForm")) != null ? new Form((Map<String, Object>) object) : null;
//        fieldID = (object = properties.get("fieldID")) != null ? new Field((Map<String, Object>) object) : null;
//        fieldTmp = (object = properties.get("fieldTmp")) != null ? new Field((Map<String, Object>) object) : null;
        previewMajor = (object = properties.get("previewMajor")) != null ? new Field((Map<String, Object>) object) : null;
        previewMinor = (object = properties.get("previewMinor")) != null ? new Field((Map<String, Object>) object) : null;
        icon = (String)properties.get("icon");
    }

    public MenuItems getParentId() {
        return parentId;
    }

    public void setParentId(MenuItems parentId) {
        this.parentId = parentId;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }
}
