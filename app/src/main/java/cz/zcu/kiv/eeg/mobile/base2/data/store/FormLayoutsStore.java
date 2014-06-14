package cz.zcu.kiv.eeg.mobile.base2.data.store;

import cz.zcu.kiv.eeg.mobile.base2.data.model.Form;
import cz.zcu.kiv.eeg.mobile.base2.data.model.FormLayouts;
import cz.zcu.kiv.eeg.mobile.base2.data.model.Layout;

public class FormLayoutsStore extends Store {
    private final static String TAG = FormLayoutsStore.class.getName();

    private final static String VIEW_NAME = "form-layouts";
    private final static String DOC_TYPE_LAYOUT = "form-layout";

    public FormLayoutsStore(DatabaseHelper databaseHelper) {
        super(databaseHelper, VIEW_NAME, DOC_TYPE_LAYOUT);
    }

    public Boolean saveOrUpdate(Form form, Layout layout) {
        FormLayouts data = new FormLayouts(form, layout);
        return saveOrUpdate(data);
    }
}
