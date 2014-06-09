package cz.zcu.kiv.eeg.mobile.base2.data.model;

import java.util.HashMap;
import java.util.Map;

import cz.zcu.kiv.eeg.mobile.base2.data.interfaces.NoSQLData;

/**
 *
 * @author Rahul Kadyan, (mail@rahulkadyan.com)
 *
 */
public class FormLayouts implements NoSQLData{

	private int id;

	private Form form;

	private Layout layout;

	public FormLayouts() {
		super();
	}

	public FormLayouts(Form form, Layout layout) {
		super();
		this.form = form;
		this.layout = layout;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

	public Layout getLayout() {
		return layout;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
	}

    @Override
    public Map<String, Object> get() {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("id", id);
        m.put("form",form.get());
        m.put("layout", layout.get());
        return m;
    }
}
