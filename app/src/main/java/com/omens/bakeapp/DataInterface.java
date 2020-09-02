package com.omens.bakeapp;

import java.util.Map;

public interface DataInterface {
    interface View {
        void setData(Map<String, Object> profile);
    }

    interface UserActionsListener {
        void fetchDataFormDB();
        void saveData(Map<String, Object> profile);
    }
}
