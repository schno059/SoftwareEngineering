package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class RulesActivity extends AppCompatActivity {

    private Button startButton;
    private Button signOut;
    private TextView rules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rules_screen);

        signOut = (Button) findViewById(R.id.goBackButton);
        startButton = (Button) findViewById(R.id.mainButtonStart);
        rules = (TextView) findViewById(R.id.mainRules);

        YoYo.with(Techniques.ZoomInUp).playOn(startButton);
        YoYo.with(Techniques.ZoomInDown).playOn(signOut);
    }

    public void startQuiz(View view) {
        Intent intent = new Intent(this, QuizActivity.class);
        startActivity(intent);
    }

    public void googleSignInScreen(View view) {
        Intent intent = new Intent(this, SignInActivity.class);
        intent.putExtra("SIGN_OUT", true);
        startActivity(intent);
    }
}