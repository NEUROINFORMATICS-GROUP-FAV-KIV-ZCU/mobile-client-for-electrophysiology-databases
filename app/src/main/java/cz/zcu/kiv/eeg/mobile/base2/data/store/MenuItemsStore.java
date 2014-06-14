package cz.zcu.kiv.eeg.mobile.base2.data.store;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

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
                QueryRow row = it.next();
                list.add(new MenuItems(row.getDocumentProperties()));
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
        if(null != document) return new MenuItems(document.getProperties());
        return null;
    }

    public MenuItems getMenu(String name) {
        Query query = getQuery("name");
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
}
