package cz.zcu.kiv.eeg.mobile.base2.data.store;

/**
 * Storage class for FieldLayoutsStore.java
 *
 * @author Rahul Kadyan, mail@rahulkadyan.com
 * @version 1.0.0.
 */
public class FieldLayoutsStore extends Store {
    private final static String TAG = FieldLayoutsStore.class.getName();

    private final static String VIEW_NAME = "field-layouts-view";
    private final static String DOC_TYPE_VALUE = "field-layouts";

    /**
     * Instantiates a new Field layouts store.
     *
     * @param databaseHelper the database helper
     */
    public FieldLayoutsStore(DatabaseHelper databaseHelper) {
        super(databaseHelper, VIEW_NAME, DOC_TYPE_VALUE);
    }
}
