package com.hr.techlabapp.Networking;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

abstract class TechlabData {
    protected abstract Map<String, Object> getValues();

    protected JSONObject serialize(JSONObject json) throws JSONException {
        for (Map.Entry<String, Object> entry : getValues().entrySet())
            json.put(entry.getKey(), entry.getValue());
        return json;
    }
}
