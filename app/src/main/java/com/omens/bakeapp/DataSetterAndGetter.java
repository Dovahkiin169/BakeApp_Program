package com.omens.bakeapp;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.MutableDocument;

import java.util.HashMap;
import java.util.Map;

public class DataSetterAndGetter implements DataInterface.UserActionsListener {
    private DataInterface.View dataFromDB;

    public DataSetterAndGetter(DataInterface.View dataFromDB) {
        this.dataFromDB = dataFromDB;
    }

    public void fetchDataFormDB() {
        Database database = DatabaseManager.getDatabase();
        String docId = DatabaseManager.getSharedInstance().getCurrentDocId();

        if (database != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("document", DatabaseManager.getSharedInstance().currentDoc);

            Document document = database.getDocument(docId);

            if (document != null) {
                data.put("first_number", document.getString("first_number"));
                data.put("second_number", document.getString("second_number"));
            }

            dataFromDB.setData(data);
        }
    }

    public void saveData(Map<String, Object> data) {
        Database database = DatabaseManager.getDatabase();
        String docId = DatabaseManager.getSharedInstance().getCurrentDocId();
        MutableDocument mutableDocument = new MutableDocument(docId, data);

        try {
            database.save(mutableDocument);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }
}
