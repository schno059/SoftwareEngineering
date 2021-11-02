package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class SignInActivity extends AppCompatActivity  {

    private static final int RC_SIGN_IN = 100;
    Button signInOutButton;
    GoogleSignInClient mGoogleSignInClient;

    public static GoogleSignInAccount getUserAccount() {
        return userAccount;
    }

    private static GoogleSignInAccount userAccount = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_screen);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInOutButton = findViewById(R.id.signInOutButton);

        if(getIntent().getBooleanExtra("SIGN_OUT", false) == true) {
            mGoogleSignInClient.signOut();
            signInOutButton.setText("Sign in with Google");
        }

        signInOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(signInOutButton.getText().toString() == "Sign in with Google"){
                    signIn();
                } else {
//                    mGoogleSignInClient.signOut();
//                    Log.d("TAG", "onClick: Signed out.");
//                    signInOutButton.setText("Sign in with Google");
                    onStart();
                }
            }
        });


        TextView ericCredit = findViewById(R.id.ericCredit);
        TextView jacobCredit = findViewById(R.id.jacobCredit);
        TextView michaelCredit = findViewById(R.id.michaelCredit);
        TextView nicCredit = findViewById(R.id.nicCredit);
        TextView signinTitle = findViewById(R.id.signinTitle);

        YoYo.with(Techniques.DropOut).duration(1000).playOn(signinTitle);
        
        YoYo.with(Techniques.BounceInLeft)
                .delay(700)
                .duration(700)
                .playOn(ericCredit);

        YoYo.with(Techniques.BounceInLeft)
                .delay(800)
                .duration(700)
                .playOn(jacobCredit);

        YoYo.with(Techniques.BounceInLeft)
                .delay(900)
                .duration(700)
                .playOn(michaelCredit);

        YoYo.with(Techniques.BounceInLeft)
                .delay(1000)
                .duration(700)
                .playOn(nicCredit);


    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d("SignIn", "handleSignInResult: Account" + account.getDisplayName());
//            signInOutButton.setText("Sign out with Google");
            signInOutButton.setText("Continue");
            // Signed in successfully, show authenticated UI.
//            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("ApiException", "signInResult:failed code=" + e.getStatusCode());
//            updateUI(null);
        }
    }

    public String getUsername(){
        return userAccount.getDisplayName();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null){
            userAccount = account;
//            signInOutButton.setText("Sign out with Google");
            goToRules();
        } else {
            userAccount = null;
            signInOutButton.setText("Sign in with Google");
        }
    }

    private void goToRules() {
        Intent intent = new Intent(this, RulesActivity.class);
        startActivity(intent);
    }
}
