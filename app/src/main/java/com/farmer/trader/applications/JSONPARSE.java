package com.farmer.trader.applications;

import org.json.JSONException;
import org.json.JSONObject;



class JSONPARSE {

    public String parse(JSONObject json){
        String name = " ";
        try {
            name = json.getString("Value");
        } catch (JSONException e) {
//            e.printStackTrace();
            name =e.getMessage();

        }
        return name;
    }

}
