package cz.zcu.kiv.eeg.mobile.base2.data.store;

public class FieldLayoutsStore extends Store {
    private final static String TAG = FieldLayoutsStore.class.getName();

    private final static String VIEW_NAME = "field-layouts-view";
    private final static String DOC_TYPE_VALUE = "field-layouts";

    public FieldLayoutsStore(DatabaseHelper databaseHelper) {
        super(databaseHelper, VIEW_NAME, DOC_TYPE_VALUE);
    }
}
