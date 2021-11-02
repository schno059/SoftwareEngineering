package com.example.finalproject;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class LeaderboardActivity extends AppCompatActivity {

    LinearLayout scoreList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard_screen);

        scoreList = findViewById(R.id.scoreList);

        RestSingleton restSingleton = RestSingleton.getInstance(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, restSingleton.getUrl() + "getScoreData",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray scoreData = new JSONArray(response);
                            for(int i=0; i<scoreData.length(); i++)
                            {
                                fillEntry((JSONObject) scoreData.get(i));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error connecting", String.valueOf(error));
            }
        });
        restSingleton.addToRequestQueue(stringRequest);
    }

    private void fillEntry(JSONObject scoreElement) throws JSONException {
        String name = scoreElement.getString("name");
        String score = scoreElement.getString("score");
        String time = scoreElement.getString("time");
        TextView entry = new TextView(scoreList.getContext());
        entry.setText(row(name,score,time));
        entry.setTypeface(Typeface.MONOSPACE);
        entry.setTextSize(18);
        entry.setTextColor(Color.WHITE);

        //Add our current layout to the overall list
        scoreList.addView(entry);
    }

    private String row(String n, String s, String t) {
        return (format(n,10, true)+"   "+
                format(s,2, true)+"   "+
                format(t, 8, false));
    }

    private static String format(String string, int length, boolean left) {
        String returnString = string;
        if(string.length()>length)
            returnString = returnString.substring(0, length);
        if(left) return String.format("%1$-"+length+ "s", returnString);
        else return String.format("%1$"+length+ "s", returnString);
    }



}
