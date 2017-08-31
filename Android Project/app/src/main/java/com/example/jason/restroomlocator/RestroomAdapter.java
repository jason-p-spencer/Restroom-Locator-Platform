package com.example.jason.restroomlocator;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * Created by Jason on 7/23/2017.
 */

public class RestroomAdapter extends ArrayAdapter<Restroom> {


    public RestroomAdapter(Context context, ArrayList<Restroom> restrooms) {
        super(context, 0, restrooms);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
                //Get the data for the specific item at the position of inflation
        final Restroom tempRestroom = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.restroom_item_constraint, parent, false);
        }

        TextView restroomName = (TextView) convertView.findViewById(R.id.restroomName);
        TextView restroomDistance = (TextView) convertView.findViewById(R.id.distanceText);

        RatingBar ratingBar = (RatingBar)convertView.findViewById(R.id.restroomRatingBar);
        ratingBar.setRating((float)tempRestroom.cleanRating);

        DecimalFormat decimalFormat = new DecimalFormat("0.##");
        String distString = decimalFormat.format(tempRestroom.distance);
        restroomDistance.setText(distString + " miles");

        Button button = (Button)convertView.findViewById(R.id.restroomButton);
        button.setText("Direct me!");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Uri will hold the data that we want the intent to have
                Uri location = Uri.parse("google.navigation:q=" + tempRestroom.xcoord + ", " + tempRestroom.ycoord);
                //Intent needs some action, in this case VIEW, and the data, which is from the Uri location.
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
                mapIntent.setPackage("com.google.android.apps.maps");
                getContext().startActivity(mapIntent);

        }
        });
        restroomName.setText(tempRestroom.name);

        return convertView;
    }

}
