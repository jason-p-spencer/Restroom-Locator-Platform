package com.example.jason.restroomlocator;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Jason on 7/31/2017.
 */

public class Restroom implements Serializable{
    protected int id;
    protected String name;
    protected double xcoord;
    protected double ycoord;
    protected double cleanRating;
    protected boolean keyReq;
    protected boolean custReq;
    protected double distance;

    public Restroom(JSONObject object) {
        try {
            id = object.getInt("id");
            name = object.getString("name");
            xcoord = object.getDouble("xcoord");
            ycoord = object.getDouble("ycoord");
            cleanRating = object.getDouble("cleanRating");

            int tempKeyReq = object.getInt("keyReq");
            if (tempKeyReq == 0) {
                keyReq = false;
            } else {
                keyReq = true;
            }
            int tempCustReq = object.getInt("custReq");
            if (tempCustReq == 0) {
                custReq = false;
            } else {
                custReq = true;
            }
        } catch (JSONException e) {

        }
    }
    // Factory method to convert an array of JSON objects into a list of objects
    // Restroom.fromJson(jsonArray);
    public static ArrayList<Restroom> fromJson(JSONArray jsonObjects) {
        ArrayList<Restroom> restrooms = new ArrayList<Restroom>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                restrooms.add(new Restroom(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return restrooms;
    }

    public static ArrayList<Restroom> sortRestrooms (ArrayList<Restroom> rooms, double x, double y) {
        //The tricky thing about sorting this is I need to sort based on distance,
        //So I first need some way to keep track of all the distances.
        //The idea is to use a parallel array to populate distances,
        //then use those distances to swap the rooms around.

        double[] distances = new double[rooms.size()];
        for (int i = 0; i < distances.length; i++) {
            //Two formulas being used
            double theta = y - rooms.get(i).ycoord;
            double dist = Math.sin(deg2rad(x)) * Math.sin(deg2rad(rooms.get(i).xcoord)) + Math.cos(deg2rad(x)) * Math.cos(deg2rad(rooms.get(i).xcoord)) * Math.cos(deg2rad(theta));
            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515;
            distances[i] = dist;
            Restroom tempRestroom = rooms.get(i);
            tempRestroom.distance = distances[i];
            rooms.set(i, tempRestroom);
        }
        //We now have a parallel distances array for our restroom list.
        //We now need to sort both the distances and corresponding restrooms in ascending order.
        Restroom roomSwap;
        double distanceSwap;

        for (int i = 0; i < distances.length; i++) {
            for (int j = i; j < distances.length; j++) {
                if (distances[i] > distances[j]) {
                    distanceSwap = distances[i];
                    distances[i] = distances[j];
                    distances[j] = distanceSwap;

                    roomSwap = rooms.get(i);
                    rooms.set(i, rooms.get(j));
                    rooms.set(j, roomSwap);
                }
            }
        }
        return rooms;
    }
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


}