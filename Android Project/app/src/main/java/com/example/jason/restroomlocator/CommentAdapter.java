package com.example.jason.restroomlocator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;



public class CommentAdapter extends ArrayAdapter<Comment> {
    public CommentAdapter(Context context, ArrayList<Comment> comments) {
        super(context, 0, comments);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Get the data for the specific item at the position of inflation
        final Comment tempComment = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.comment_item_constraint, parent, false);
        }

        TextView commentText = (TextView)convertView.findViewById(R.id.commentItemComment);
        commentText.setText("\"" + tempComment.comment + "\"");
        return convertView;
    }
}