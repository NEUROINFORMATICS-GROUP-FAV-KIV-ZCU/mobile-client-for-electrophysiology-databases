package cz.zcu.kiv.eeg.mobile.base2.data.model;

/**
 *
 * @author Jaroslav Hošek
 *
 */
public class MenuItems {

	private int id;

	private String name;

	private Layout layout;

	private Form rootForm;

	private Field fieldID;

	private Field fieldTmp;

	private Field previewMajor;

	private Field previewMinor;

	public MenuItems() {
		super();
	}

	public MenuItems(String name, Layout layout, Form rootForm, Field fieldDescription1, Field fieldDescription2) {
		super();
		this.name = name;
		this.layout = layout;
		this.previewMajor = fieldDescription1;
		this.previewMinor = fieldDescription2;
		this.rootForm = rootForm;
	}

	public MenuItems(String name, Layout layout) {
		super();
		this.name = name;
		this.layout = layout;
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

	public Field getFieldID() {
		return fieldID;
	}

	public void setFieldID(Field fieldID) {
		this.fieldID = fieldID;
	}

	public Field getFieldTmp() {
		return fieldTmp;
	}

	public void setFieldTmp(Field fieldTmp) {
		this.fieldTmp = fieldTmp;
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
}
