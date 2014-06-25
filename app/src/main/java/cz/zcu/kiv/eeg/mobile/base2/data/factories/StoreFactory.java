package cz.zcu.kiv.eeg.mobile.base2.data.factories;

import android.content.Context;

import cz.zcu.kiv.eeg.mobile.base2.data.store.DataStore;
import cz.zcu.kiv.eeg.mobile.base2.data.store.DatabaseHelper;
import cz.zcu.kiv.eeg.mobile.base2.data.store.DatasetStore;
import cz.zcu.kiv.eeg.mobile.base2.data.store.FieldLayoutsStore;
import cz.zcu.kiv.eeg.mobile.base2.data.store.FieldStore;
import cz.zcu.kiv.eeg.mobile.base2.data.store.FieldValueStore;
import cz.zcu.kiv.eeg.mobile.base2.data.store.FormLayoutsStore;
import cz.zcu.kiv.eeg.mobile.base2.data.store.FormStore;
import cz.zcu.kiv.eeg.mobile.base2.data.store.LayoutPropertyStore;
import cz.zcu.kiv.eeg.mobile.base2.data.store.LayoutStore;
import cz.zcu.kiv.eeg.mobile.base2.data.store.MenuItemsStore;
import cz.zcu.kiv.eeg.mobile.base2.data.store.UserStore;

/**
 * Object factory: Provides storage object
 */
public class StoreFactory {
    private final Context context;
    private DatabaseHelper databaseHelper = null;
    private UserStore userStore;
    private MenuItemsStore menuItemStore;
    private LayoutStore layoutStore;
    private FieldStore fieldStore;
    private DatasetStore datasetStore;
    private FormStore formStore;
    private DataStore dataStore;
    private LayoutPropertyStore layoutPropertyStore;
    private FieldValueStore fieldValueStore;
    private FieldLayoutsStore fieldLayoutsStore;
    private FormLayoutsStore formLayoutsStore;

    /**
     * Instantiates a new Store factory.
     *
     * @param context the context
     */
    public StoreFactory(Context context) {
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
    }

    /**
     * Gets database helper.
     *
     * @return the database helper
     */
    public DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

    /**
     * Gets context.
     *
     * @return the context
     */
    public Context getContext() {

        return context;
    }

    /**
     * Gets user store.
     *
     * @return the user store
     */
    public UserStore getUserStore() {
        if (null == userStore) userStore = new UserStore(databaseHelper);
        return userStore;
    }

    /**
     * Release helper.
     */
    public void releaseHelper() {
        databaseHelper.releaseHelper();
    }

    /**
     * Gets menu item store.
     *
     * @return the menu item store
     */
    public MenuItemsStore getMenuItemStore() {
        if (null == menuItemStore) menuItemStore = new MenuItemsStore(databaseHelper);
        return menuItemStore;
    }

    /**
     * Gets layout store.
     *
     * @return the layout store
     */
    public LayoutStore getLayoutStore() {
        if (null == layoutStore) layoutStore = new LayoutStore(databaseHelper);
        return layoutStore;
    }

    /**
     * Gets field store.
     *
     * @return the field store
     */
    public FieldStore getFieldStore() {
        if (null == fieldStore) fieldStore = new FieldStore(databaseHelper);
        return fieldStore;
    }

    /**
     * Gets dataset store.
     *
     * @return the dataset store
     */
    public DatasetStore getDatasetStore() {
        if (null == datasetStore) datasetStore = new DatasetStore(databaseHelper);
        return datasetStore;
    }

    /**
     * Gets form store.
     *
     * @return the form store
     */
    public FormStore getFormStore() {
        if (null == formStore) formStore = new FormStore(databaseHelper);
        return formStore;
    }

    /**
     * Gets data store.
     *
     * @return the data store
     */
    public DataStore getDataStore() {
        if (null == dataStore) dataStore = new DataStore(databaseHelper);
        return dataStore;
    }

    /**
     * Gets layout property store.
     *
     * @return the layout property store
     */
    public LayoutPropertyStore getLayoutPropertyStore() {
        if (null == layoutPropertyStore)
            layoutPropertyStore = new LayoutPropertyStore(databaseHelper);
        return layoutPropertyStore;
    }

    /**
     * Gets field value store.
     *
     * @return the field value store
     */
    public FieldValueStore getFieldValueStore() {
        if (null == fieldValueStore) fieldValueStore = new FieldValueStore(databaseHelper);
        return fieldValueStore;
    }

    /**
     * Gets field layouts store.
     *
     * @return the field layouts store
     */
    public FieldLayoutsStore getFieldLayoutsStore() {
        if (null == fieldLayoutsStore) fieldLayoutsStore = new FieldLayoutsStore(databaseHelper);
        return fieldLayoutsStore;
    }

    /**
     * Gets form layouts store.
     *
     * @return the form layouts store
     */
    public FormLayoutsStore getFormLayoutsStore() {
        if (null == formLayoutsStore) formLayoutsStore = new FormLayoutsStore(databaseHelper);
        return formLayoutsStore;
    }
}
