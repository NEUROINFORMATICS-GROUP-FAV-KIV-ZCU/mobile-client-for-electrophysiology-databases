package cz.zcu.kiv.eeg.mobile.base2.data.store;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;
import java.util.Map;

import static com.couchbase.lite.Manager.DEFAULT_OPTIONS;

public class DatabaseHelper {
    public static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "eeg-mobile-database";
    public static final String DOC_TYPE = "doc-type";
    private static final int DATABASE_VERSION = 1;

    private static Manager manager = null;
    private static Database database = null;

    public DatabaseHelper(final Context context) {
        if (null == manager)
            try {
                manager = new Manager(new AndroidContext(context), DEFAULT_OPTIONS);
            } catch (IOException e) {
                Log.e(TAG, "Cannot create NoSQL manager object", e);
                return;
            }

        if (null == database)
            try {
                database = manager.getDatabase(DATABASE_NAME);
            } catch (CouchbaseLiteException e) {
                Log.e(TAG, "Cannot get database", e);
            }
    }

    public Database getDatabase() {
        return database;
    }

    public Document getDocument() {
        return database.createDocument();
    }

    public String putProperties(Document document, Map<String, Object> docContent) {
        try {
            document.putProperties(docContent);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Cannot write document to database", e);
            return "";
        }
        return document.getId();
    }

    public void releaseHelper() {
        if (null != database) database.close();
        if (null != manager) manager.close();
    }
}
