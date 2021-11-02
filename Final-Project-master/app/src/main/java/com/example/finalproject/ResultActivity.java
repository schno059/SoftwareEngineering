package com.example.finalproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class ResultActivity extends AppCompatActivity {
    int score;
    double time;

    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_screen);

        score = getIntent().getIntExtra("SCORE", 0);
        time = getIntent().getDoubleExtra("TIME", 0);

        result = findViewById(R.id.result);
        result.setText("You scored " + score + "/10\nin "+getTimerText());

        try {
            putScoreData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        createConfetti();
    }

    @Override
    public void onBackPressed()
    {
        startOver(null);
    }

    private String getTimerText()
    {
        int rounded = (int) Math.round(time);

        int seconds = (((rounded % 86400) % 3600) % 60);    // The "-1" is to prevent the timer from starting at 1 second.
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours);
    }

    private String formatTime(int seconds, int minutes, int hours)
    {
        return String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
    }

    private void putScoreData() throws JSONException {
        JSONObject scoreData = new JSONObject();
        scoreData.put("name", SignInActivity.getUserAccount().getDisplayName().split(" ")[0]);
        scoreData.put("score", score);
        scoreData.put("time", time);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, RestSingleton.getInstance(this).getUrl() + "putScoreData", scoreData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error connecting", String.valueOf(error));
            }
        });

        RestSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void createConfetti() {
        final KonfettiView konfettiView = findViewById(R.id.resutlKonfettiView);
        konfettiView.build()
                .addColors(Color.rgb(128,0,0), Color.rgb(255,215,0))
                .setDirection(250, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                .addSizes(new Size(12, 5f))
                .setPosition(konfettiView.getWidth() / 2, konfettiView.getWidth() + 50f, -30f, -100f)
                .streamFor(300, 5000L);
    }


    public void startOver(View view) {
        Intent intent = new Intent(this, RulesActivity.class);
        startActivity(intent);
    }

    public void viewLeaderboards(View view) {
        Intent intent = new Intent(this, LeaderboardActivity.class);
        startActivity(intent);
    }



}