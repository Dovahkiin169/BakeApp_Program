package com.omens.bakeapp;

import java.util.Map;

public interface DataInterface {
    interface View {
        void setData(Map<String, Object> numbers);
    }

    interface UserActionsListener {
        void fetchDataFormDB();
        void saveData(Map<String, Object> numbers);
    }
}
