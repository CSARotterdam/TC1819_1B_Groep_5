package com.hr.techlabapp.Networking;

import android.support.v4.util.ArrayMap;

import java.util.Map;

public class Category extends TechlabData {
    public String id;
    public String name;

    @Override
    protected Map<String, Object> getValues() {
        Map<String, Object> out = new ArrayMap<>();
        out.put("id", id);
        out.put("name", name);
        return out;
    }
}
