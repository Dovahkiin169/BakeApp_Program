package com.omens.bakeapp;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLite;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.Document;
import com.couchbase.lite.ListenerToken;

public class DatabaseManager {
    private static Database database;
    private static DatabaseManager instance = null;
    private ListenerToken listenerToken;
    public String currentDoc = null;

    protected DatabaseManager() {}

    public static DatabaseManager getSharedInstance() {
        if (instance == null)
            instance = new DatabaseManager();
        return instance;
    }

    public static Database getDatabase() {
        return database;
    }

    public void initCouchbaseLite(Context context) {
        CouchbaseLite.init(context);
    }


    public String getCurrentDocId() {
        return "document::" + currentDoc;
    }

    public void openOrCreateDatabase(Context context, String doc) {
        DatabaseConfiguration config = new DatabaseConfiguration();
        config.setDirectory(String.format("%s/%s", context.getFilesDir(), doc));
        currentDoc = doc;
        try {
            String dbName = "data";
            database = new Database(dbName, config);
            registerForDatabaseChanges();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }


    private void registerForDatabaseChanges() {
        listenerToken = database.addChangeListener(change -> {
            for(String docId : change.getDocumentIDs()) {
                Document doc = database.getDocument(docId);
                if (doc != null)
                    Log.i("DatabaseChangeEvent", "Document was added/updated");
                else
                    Log.i("DatabaseChangeEvent","Document was deleted");
            }
        });
    }

    public void closeDatabase(){
        try {
            if (database != null) {
                deregisterForDatabaseChanges();
                database.close();
                database = null;
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    private void deregisterForDatabaseChanges(){
        if (listenerToken != null)
            database.removeChangeListener(listenerToken);
    }
}
