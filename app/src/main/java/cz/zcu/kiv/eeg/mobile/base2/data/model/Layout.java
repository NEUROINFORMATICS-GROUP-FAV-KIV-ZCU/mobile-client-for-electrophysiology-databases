package cz.zcu.kiv.eeg.mobile.base2.data.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.HashMap;
import java.util.Map;

import cz.zcu.kiv.eeg.mobile.base2.data.interfaces.NoSQLData;

/**
 * @author Rahul Kadyan, (mail@rahulkadyan.com)
 */
@Root(name = Layout.XML_ROOT)
public class Layout extends NoSQLData {
    public static final String XML_ROOT = "layout";
    public static final String LAYOUT_NAME = "layoutName";
    public static final String LAYOUT_ID = "layoutId";

	/*@DatabaseField(generatedId = true)
    private int id;*/

    @Element(name = LAYOUT_NAME)
    private String name;

//    private Layout rootLayout;

    private Form rootForm;

    private String xmlData;

    // cz: pouze pomocná proměnná při získávání layoutu z ws
    // en: only auxiliary variable in getting the layout of ws
    @Element
    private String formName;

    public Layout() {
        super();
    }

    public Layout(Form rootForm) {
        super();
        this.rootForm = rootForm;
    }

    public Layout(String name) {
        super();
        this.name = name;
    }

    public Layout(String name, String xmlData, Form rootForm) {
        super();
        this.name = name;
        this.xmlData = xmlData;
//        this.rootLayout = rootLayout;
        this.rootForm = rootForm;
    }

//    public Layout getRootLayout() {
//        return rootLayout;
//    }

//    public void setRootLayout(Layout rootLayout) {
//        this.rootLayout = rootLayout;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getXmlData() {
        return xmlData;
    }

    public void setXmlData(String xmlData) {
        this.xmlData = xmlData;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public Form getRootForm() {
        return rootForm;
    }

    public void setRootForm(Form rootForm) {
        this.rootForm = rootForm;
    }

    public Layout(Map<String, Object> properties) {
        set(properties);
    }

    @Override
    public void setId(int id) {

    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public Map<String, Object> get() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("name", name);
        if (rootForm != null) {
            properties.put("rootForm", rootForm.get());
            properties.put("rootForm-id", rootForm.getId());
        } else properties.put("rootForm", null);
        properties.put("xmlData", xmlData);
        properties.put("formName", formName);
        return properties;
    }

    @Override
    public void set(Map<String, Object> properties) {
        if (null == properties) return;
        name = (String) properties.get("name");
        Object object;
        rootForm = (object = properties.get("rootForm")) != null ? new Form((Map<String, Object>) object) : null;
        xmlData = (String) properties.get("xmlData");
        formName = (String) properties.get("formName");
    }
}
