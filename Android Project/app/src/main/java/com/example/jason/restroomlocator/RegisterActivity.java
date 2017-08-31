package com.example.jason.restroomlocator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText restroomNameField;
    String restroomName;
    CheckBox keyReqField;
    boolean keyReq;
    CheckBox custReqField;
    boolean custReq;
    Button submitButton;
    RatingBar ratingBar;
    int processedKeyReq;
    int processedCustReq;

    double latHolder = 0, longHolder = 0;

    private RequestQueue mRequestQueue;
    String json_url = "http://13.59.61.32/restroom";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Bundle b = getIntent().getExtras();
        latHolder = b.getDouble("lat");
        longHolder = b.getDouble("long");

        restroomNameField = (EditText)findViewById(R.id.registerRestroomName);
        keyReqField = (CheckBox)findViewById(R.id.registerKeyReq);
        custReqField= (CheckBox)findViewById(R.id.registerCustReq);

        submitButton = (Button)findViewById(R.id.registerButton);

        ratingBar = (RatingBar)findViewById(R.id.registerRatingBar);

        mRequestQueue = NetworkController.getInstance(this.getApplicationContext()).getRequestQueue();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle b = getIntent().getExtras();
        latHolder = b.getDouble("lat");
        longHolder = b.getDouble("long");
    }
    protected void postRequest(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        restroomName = restroomNameField.getText().toString();
        keyReq = keyReqField.isChecked();
        custReq = custReqField.isChecked();

        if (keyReq == true) {
            processedKeyReq = 1;
        } else {
            processedKeyReq = 0;
        }
        if (custReq == true) {
            processedCustReq = 1;
        } else {
            processedCustReq = 0;
        }


        StringRequest postRequest = new StringRequest(Request.Method.POST, json_url,
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
                params.put("name", restroomName.toString());
                params.put("xcoord", String.valueOf(latHolder));
                params.put("ycoord", String.valueOf(longHolder));
                params.put("cleanRating", String.valueOf(ratingBar.getRating()));
                params.put("keyReq", String.valueOf(processedKeyReq));
                params.put("custReq", String.valueOf(processedCustReq));
                return params;
            }
        };
        mRequestQueue.add(postRequest);
        Toast.makeText(getApplicationContext(), "Restroom successfully registered!", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }
}