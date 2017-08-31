package com.example.jason.restroomlocator;

import android.content.Intent;
import android.media.Rating;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MoreInfoActivity extends AppCompatActivity {

    private ListView commentListView;
    private ArrayList<Comment> commentList = new ArrayList<Comment>();
    private CommentAdapter commentAdapter;
    private String json_url;
    private String rest_url = "http://13.59.61.32/comments";

    private RequestQueue mRequestQueue;

    private Restroom restroom;
    private TextView name;
    private RatingBar ratingBar;
    private CheckBox keyReq;
    private CheckBox custReq;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);

        commentAdapter = new CommentAdapter(this, commentList);
        commentListView = (ListView)findViewById(R.id.moreInfoListView);
        commentListView.setAdapter(commentAdapter);

        mRequestQueue = NetworkController.getInstance(this.getApplicationContext()).getRequestQueue();

        restroom = (Restroom) getIntent().getSerializableExtra("restroom");

        json_url = rest_url + "/" + restroom.id;
        name = (TextView) findViewById(R.id.moreInfoName);
        name.setText(restroom.name);
        ratingBar = (RatingBar) findViewById(R.id.moreInfoRatingBar);
        ratingBar.setRating((float)restroom.cleanRating);
        keyReq = (CheckBox) findViewById(R.id.moreInfoKeyReq);
        keyReq.setChecked(restroom.keyReq);
        custReq = (CheckBox) findViewById(R.id.moreInfoCustReq);
        custReq.setChecked(restroom.custReq);

        editText = (EditText) findViewById(R.id.moreInfoEditTextComment);

        fetchJsonArrayResponse();
    }

    private void fetchJsonArrayResponse() {
        JsonArrayRequest req = new JsonArrayRequest(json_url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList tempList = Comment.fromJson(response);
                        commentAdapter.addAll(tempList);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
		/* Add your Requests to the RequestQueue to execute */
        mRequestQueue.add(req);
    }

    protected void addComment(View view) {
        String comment = editText.getText().toString();
        commentPostRequest(comment);
        commentPutRequest();
        Intent intent = new Intent (this, MainActivity.class);
        startActivity(intent);
    }

    protected void commentPostRequest(final String comment) {
        StringRequest postRequest = new StringRequest(Request.Method.POST, rest_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("refID", String.valueOf(restroom.id));
                params.put("comment", comment);
                return params;
            }
        };
        mRequestQueue.add(postRequest);
        Toast.makeText(getApplicationContext(), "Successfully added comment!", Toast.LENGTH_SHORT).show();
    }
    protected void commentPutRequest() {
        final double newRating = ratingBar.getRating();
        StringRequest postRequest = new StringRequest(Request.Method.PUT, "http://13.59.61.32/restroom/" + restroom.id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("rating", String.valueOf(newRating));
                return params;
            }
        };
        mRequestQueue.add(postRequest);
        Toast.makeText(getApplicationContext(), "Successfully added comment!", Toast.LENGTH_SHORT).show();
    }
}
