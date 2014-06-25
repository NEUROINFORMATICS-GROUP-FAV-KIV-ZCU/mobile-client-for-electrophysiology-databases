package cz.zcu.kiv.eeg.mobile.base2.data.factories;

import android.content.Context;

import cz.zcu.kiv.eeg.mobile.base2.data.store.DataStore;
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
 * Support class to enable usage of NoSQL storage factory using old SQLite API
 *
 * @author Jaroslav Hošek
 * @author Rahul Kadyan
 * @version 2.0.0
 * @deprecated use cz.zcu.kiv.eeg.mobile.base2.data.factories.StoreFactory instead
 * @see cz.zcu.kiv.eeg.mobile.base2.data.factories.StoreFactory
 */
public class DAOFactory extends StoreFactory {


    public DAOFactory(Context context) {
        super(context);
    }

    public FormStore getFormDAO() {
        return getFormStore();
    }

    public FieldStore getFieldDAO() {
        return getFieldStore();
    }

    public LayoutStore getLayoutDAO() {
        return getLayoutStore();
    }

    public FormLayoutsStore getFormLayoutsDAO() {
        return getFormLayoutsStore();
    }

    public FieldLayoutsStore getFieldLayoutsDAO() {
        return getFieldLayoutsStore();
    }

    public MenuItemsStore getMenuItemDAO() {
        return getMenuItemStore();
    }

    public DataStore getDataDAO() {
        return getDataStore();
    }

    public DatasetStore getDataSetDAO() {
        return getDatasetStore();
    }

    public UserStore getUserDAO() {
        return getUserStore();
    }

    public LayoutPropertyStore getLayoutPropertyDAO() {
        return getLayoutPropertyStore();
    }

    public FieldValueStore getFieldValueDAO() {
        return getFieldValueStore();
    }
}
