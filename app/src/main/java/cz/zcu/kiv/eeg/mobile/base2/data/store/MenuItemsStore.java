package cz.zcu.kiv.eeg.mobile.base2.data.store;

import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;

import java.util.ArrayList;
import java.util.List;

import cz.zcu.kiv.eeg.mobile.base2.data.model.MenuItems;

public class MenuItemsStore extends Store {
    private final static String TAG = DataStore.class.getName();

    private final static String VIEW_NAME = "menus";
    private final static String DOC_TYPE_VALUE = "menu";

    public MenuItemsStore(DatabaseHelper databaseHelper) {
        super(databaseHelper, VIEW_NAME, DOC_TYPE_VALUE);
    }

    public List<MenuItems> getMenu() {
        Query query = getQuery();
        List<MenuItems> list = new ArrayList<MenuItems>();
        try {
            for (QueryEnumerator it = query.run(); it.hasNext(); ) {
                Document document = it.next().getDocument();
                list.add(new MenuItems(document.getProperties()));
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<MenuItems> getMenu(MenuItems parent) {
        Query query = getQuery();
        List<MenuItems> list = new ArrayList<MenuItems>();
        try {
            for (QueryEnumerator it = query.run(); it.hasNext(); ) {
                Document document = it.next().getDocument();
                MenuItems menuItems = new MenuItems(document.getProperties());
                if (menuItems.getParentId() != null && menuItems.getParentId().getId() == parent.getId())
                    list.add(menuItems);
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return list;
    }

    public String[] getMenuNames() {
        List<MenuItems> items = getMenu();
        String[] names = new String[items.size()];
        int i = 0;
        for (MenuItems item : items) {
            names[i++] = item.getName();
        }
        return names;
    }

    public MenuItems getMenu(int id) {
        Document document = getDocument(id);
        Log.i(TAG, "-----------" + id);
        if (null != document) return new MenuItems(document.getProperties());
        return null;
    }

    public MenuItems getMenu(String name) {
        Query query = getQuery();
        List<Object> keys = new ArrayList<Object>();
        keys.add(name);
        query.setKeys(keys);
        try {
            QueryEnumerator it = query.run();
            if (it.hasNext())
                return new MenuItems(it.next().getDocumentProperties());
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<MenuItems> getRootMenu() {
        Query query = getQuery();
        List<MenuItems> list = new ArrayList<MenuItems>();
        try {
            for (QueryEnumerator it = query.run(); it.hasNext(); ) {
                Document document = it.next().getDocument();
                MenuItems menuItems = new MenuItems(document.getProperties());
                if (menuItems.getParentId() == null)
                    list.add(menuItems);
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return list;
    }
}
