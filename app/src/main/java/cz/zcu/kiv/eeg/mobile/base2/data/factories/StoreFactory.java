package cz.zcu.kiv.eeg.mobile.base2.data.factories;

import android.content.Context;

import cz.zcu.kiv.eeg.mobile.base2.data.store.DataStore;
import cz.zcu.kiv.eeg.mobile.base2.data.store.DatabaseHelper;
import cz.zcu.kiv.eeg.mobile.base2.data.store.DatasetStore;
import cz.zcu.kiv.eeg.mobile.base2.data.store.FieldStore;
import cz.zcu.kiv.eeg.mobile.base2.data.store.FieldValueStore;
import cz.zcu.kiv.eeg.mobile.base2.data.store.FormStore;
import cz.zcu.kiv.eeg.mobile.base2.data.store.LayoutPropertyStore;
import cz.zcu.kiv.eeg.mobile.base2.data.store.LayoutStore;
import cz.zcu.kiv.eeg.mobile.base2.data.store.MenuItemsStore;
import cz.zcu.kiv.eeg.mobile.base2.data.store.UserStore;

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

    public StoreFactory(Context context) {
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
    }

    public DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }

    public Context getContext() {

        return context;
    }

    public UserStore getUserStore() {
        if (null == userStore) userStore = new UserStore(databaseHelper);
        return userStore;
    }

    public void releaseHelper() {
        databaseHelper.releaseHelper();
    }

    public MenuItemsStore getMenuItemStore() {
        if (null == menuItemStore) menuItemStore = new MenuItemsStore(databaseHelper);
        return menuItemStore;
    }

    public LayoutStore getLayoutStore() {
        if (null == layoutStore) layoutStore = new LayoutStore(databaseHelper);
        return layoutStore;
    }

    public FieldStore getFieldStore() {
        if (null == fieldStore) fieldStore = new FieldStore(databaseHelper);
        return fieldStore;
    }

    public DatasetStore getDatasetStore() {
        if (null == datasetStore) datasetStore = new DatasetStore(databaseHelper);
        return datasetStore;
    }

    public FormStore getFormStore() {
        if (null == formStore) formStore = new FormStore(databaseHelper);
        return formStore;
    }

    public DataStore getDataStore() {
        if (null == dataStore) dataStore = new DataStore(databaseHelper);
        return dataStore;
    }

    public LayoutPropertyStore getLayoutPropertyStore() {
        if (null == layoutPropertyStore)
            layoutPropertyStore = new LayoutPropertyStore(databaseHelper);
        return layoutPropertyStore;
    }

    public FieldValueStore getFieldValueStore() {
        if (null == fieldValueStore) fieldValueStore = new FieldValueStore(databaseHelper);
        return fieldValueStore;
    }
}
