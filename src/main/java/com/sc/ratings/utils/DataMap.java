package com.sc.ratings.utils;

import java.util.HashMap;

public class DataMap extends HashMap<String, Object> {
    public static DataMap builder() {
        return new DataMap();
    }

    public DataMap set(String k, Object v) {
        put(k, v);
        return this;
    }
}
