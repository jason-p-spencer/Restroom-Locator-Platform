package com.example.jason.restroomlocator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jason on 8/7/2017.
 */

public class Comment {
    protected int id;   //Comment's actually ID number
    protected int refID; //ID number of restroom that it references
    protected String comment; //The comment itself

    public Comment(JSONObject object) {
        try {
            id = object.getInt("id");
            refID = object.getInt("restroom_id");
            comment = object.getString("comment");
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Comment> fromJson(JSONArray jsonObjects) {
        ArrayList<Comment> comments = new ArrayList<Comment>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                comments.add(new Comment(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return comments;
    }
}
