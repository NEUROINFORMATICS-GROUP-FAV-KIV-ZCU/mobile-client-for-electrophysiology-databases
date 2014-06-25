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

/**
 * A helper class for NoSQL database
 * <p>
 * This class provides generic functions to store data objects in Couchbase lite database
 * </p>
 *
 * @author Rahul Kadyan, <mail@rahulkadyan.com>
 * @version 1.0.0
 */
public class DatabaseHelper {
    /**
     * Debug tag
     */
    public static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "eeg-mobile-database";
    /**
     * property name for kind of document storing
     */
    public static final String DOC_TYPE = "doc-type";

    private static Manager manager = null;
    private static Database database = null;
    private Context context;

    /**
     * Instantiates a new Database helper.
     * <p>
     * Creates a Database manager and opens a connection to default database ${DatabaseHelper::DATABASE_NAME}
     * </p>
     *
     * @param context Android application context
     */
    public DatabaseHelper(final Context context) {
        this.context = context;
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

    /**
     * Returns Database instance
     *
     * @return Database database
     */
    public Database getDatabase() {
        return database;
    }

    /**
     * Creates an empty document and returns it
     *
     * @return Document
     */
    public Document getDocument() {
        return database.createDocument();
    }

    /**
     * Save properties in given document
     *
     * @param document   Document instance
     * @param properties Key-Value pair of properties to be stored
     * @return String string
     */
    public String putProperties(Document document, Map<String, Object> properties) {
        try {
            document.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Cannot write document to database", e);
            return "";
        }
        return document.getId();
    }

    /**
     * Close all database connections
     */
    public void releaseHelper() {
        if (null != database) database.close();
        if (null != manager) manager.close();
    }

    /**
     * Get cached context
     *
     * @return Android application context
     */
    public Context getContext() {
        return context;
    }
}
