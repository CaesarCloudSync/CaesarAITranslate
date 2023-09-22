package com.example.caesaraitranslate;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class caesarRequest {
    public  void getRequest(Context context,String url,String tag){

// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d(tag,response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void sendJsonPostRequest(Context context, String url, String text, String dest, TextView textView1, TextView textView2,TextToSpeech speech){

        try {

            // Make new json object and put params in it
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("text", text);
            jsonParams.put("dest",dest);


            // Building a request
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    // Using a variable for the domain is great for testing
                    url,
                    jsonParams,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String translation = response.getString("translation");
                                textView2.setText(text);
                                textView1.setText(translation);
                                // TODO TTS Here
                                speech.speak(translation, TextToSpeech.QUEUE_FLUSH, null);
                            } catch (JSONException e) {
                                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                            }


                            // Handle the response
                            Log.d("translate",response.toString());

                        }
                    },

                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context,error.toString(), Toast.LENGTH_SHORT).show();

                            // Handle the error

                        }
                    });

            /*

              For the sake of the example I've called newRequestQueue(getApplicationContext()) here
              but the recommended way is to create a singleton that will handle this.

              Read more at : https://developer.android.com/training/volley/requestqueue

              Category -> Use a singleton pattern

            */
            Volley.newRequestQueue(context).
                    add(request);

        } catch(JSONException ex){
            // Catch if something went wrong with the params
        }

    }
}
