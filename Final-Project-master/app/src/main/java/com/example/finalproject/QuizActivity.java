package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class QuizActivity extends AppCompatActivity {
    // Source code for timer: https://github.com/codeWithCal/Timer

    TextView quizQuestionHeader;
    TextView quizQuestion;
    TextView timerText;

    Timer timer;
    TimerTask timerTask;
    public Double time = -1.0;

    public int numCorrect = 0;
    int numAnswered = 0;
    boolean timerIsPaused = true;
    boolean form1 = true;

    Random r = new Random();
    JSONObject randomObject;
    String[] answerStrings = new String[5];
    ArrayList<Button> buttons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_screen);

        quizQuestionHeader = findViewById(R.id.quizQuestionHeader);
        quizQuestion = findViewById(R.id.quizQuestion);
        buttons.add(findViewById(R.id.quizButtonA));
        buttons.add(findViewById(R.id.quizButtonB));
        buttons.add(findViewById(R.id.quizButtonC));
        buttons.add(findViewById(R.id.quizButtonD));
        buttons.add(findViewById(R.id.quizButtonE));

        getRandomObject();

        timerText = (TextView) findViewById(R.id.timerCounting);
        timer = new Timer();
        startTimer();
    }

    private void getRandomObject()
    {
        quizQuestionHeader.setText("Question #"+(numAnswered+1));
        quizQuestion.setVisibility(View.INVISIBLE);
        for(Button b: buttons) {
            b.setVisibility(View.INVISIBLE);
            b.setEnabled(false);
        }

        RestSingleton restSingleton = RestSingleton.getInstance(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, restSingleton.getUrl() + "getRandomObject",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            randomObject = new JSONObject(response);
                            form1 = r.nextBoolean();
                            writeQuestion();
                            getAnswers();
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

    private void writeQuestion()
    {
        MediaPlayer ring = MediaPlayer.create(this, R.raw.buttons_swish);
        ring.start();
        for(int i = 0; i < buttons.size(); i++)
            YoYo.with(Techniques.BounceIn)
                    .delay(i*100)
                    .duration(700)
                    .playOn(buttons.get(i));

        System.out.println(randomObject);

        try {
            if (form1) writeQuestionForm1();
            else writeQuestionForm2();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void writeQuestionForm1() throws JSONException {
        quizQuestion.setText("What year was the name " + randomObject.getString("name") + " the most popular?");
        quizQuestion.setVisibility(View.VISIBLE);
    }

    private void writeQuestionForm2() throws JSONException{
        quizQuestion.setText("Of these five names, which one was the most popular in " + randomObject.getString("year") + "?");
        quizQuestion.setVisibility(View.VISIBLE);
    }

    private void getAnswers() {
        try {
            if(form1) getAnswersForm1();
            else getAnswersForm2();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getAnswersForm1() throws JSONException {
        RestSingleton restSingleton = RestSingleton.getInstance(this);
        StringRequest stringRequest = null;
        stringRequest = new StringRequest(Request.Method.GET, restSingleton.getUrl() + "getNameAnswers/" + randomObject.getString("name"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray answerObjects = new JSONArray(response);
                            for(int i = 0; i < 5; i++)
                                answerStrings[i] = ((JSONObject) answerObjects.get(i)).getString("year");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        writeAnswers();
                        timerIsPaused = false;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error connecting", String.valueOf(error));
            }
        });
        restSingleton.addToRequestQueue(stringRequest);
    }

    private void getAnswersForm2() throws JSONException {
        RestSingleton restSingleton = RestSingleton.getInstance(this);
        StringRequest stringRequest = null;
        stringRequest = new StringRequest(Request.Method.GET, restSingleton.getUrl() + "getYearAnswers/" + randomObject.getString("year"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray answerObjects = new JSONArray(response);
                            for(int i = 0; i < 5; i++)
                                answerStrings[i] = ((JSONObject) answerObjects.get(i)).getString("name");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        writeAnswers();
                        timerIsPaused = false;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error connecting", String.valueOf(error));
            }
        });
        restSingleton.addToRequestQueue(stringRequest);
    }

    private void writeAnswers() {
        System.out.println(answerStrings[0]);

        String[] buttonStrings = new String[5];
        for(int i = 0; i < answerStrings.length; i++)
            buttonStrings[i] = answerStrings[i];

        Arrays.sort(buttonStrings);
        for(int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setText(buttonStrings[i]);
            buttons.get(i).setEnabled(true);
            buttons.get(i).setVisibility(View.VISIBLE);
        }
    }

    public void quizButtonAction(View view) {
        if(!timerIsPaused) checkGuess(view);
        else if(numAnswered<10) nextQuestion();
        else completeQuiz();
    }

    private void checkGuess(View view) {
        timerIsPaused = true;
        numAnswered++;

        Button thisButton = (Button) view;
        if(thisButton.getText().toString().equals(answerStrings[0])) {
            numCorrect++;
            thisButton.setBackgroundColor(Color.GREEN);
        } else {
            thisButton.setBackgroundColor(Color.RED);
            for(Button b: buttons)
                if(b.getText().toString().equals(answerStrings[0]))
                    b.setBackgroundColor(Color.GREEN);
        }
        System.out.println(numCorrect + "/" + numAnswered + " correct so far");
    }

    private void nextQuestion() {
        for(Button b: buttons)
            b.setBackgroundColor(Color.GRAY);
        getRandomObject();
    }

    private void completeQuiz() {
        for(Button b: buttons)
            b.setEnabled(false);
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("SCORE", numCorrect);
        intent.putExtra("TIME", time);
        startActivity(intent);
    }

    private void startTimer()
    {
        timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(!timerIsPaused) {
                            time++;
                            timerText.setText(getTimerText());
                        }
                    }
                });
            }

        };
        timer.scheduleAtFixedRate(timerTask, 0 ,1000);
    }

    private String getTimerText()
    {
        int rounded = (int) Math.round(time);

        int seconds = (((rounded % 86400) % 3600) % 60);
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours);
    }

    private String formatTime(int seconds, int minutes, int hours)
    {
        return String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
    }

}