package com.example.sarabjeetsingh.beacondetector;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    private Button facebookSignUp;
    private ProgressDialog progressDialog;
    private static final int RC_SIGN_IN = 9001;

    String TAG ="LogInActivity";

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(ZPreferences.isUserLogIn(LoginActivity.this)){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            LoginActivity.this.finish();
        }

        progressDialog =  new ProgressDialog(this);
        facebookSignUp = (Button) findViewById(R.id.facebook_sign_up);
        facebookSignUp.setOnClickListener(this);


        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]

        // [START customize_button]
        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.
        Button signInButton = (Button) findViewById(R.id.google_sign_up);
        signInButton.setOnClickListener(this);
        // [END customize_button]
    }

    @Override
    public void onStart() {
        super.onStart();

//        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
//        if (opr.isDone()) {
//            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
//            // and the GoogleSignInResult will be available instantly.
//            Log.d(TAG, "Got cached sign-in");
//            GoogleSignInResult result = opr.get();
//            handleSignInResult(result);
//        } else {
//            // If the user has not previously signed in on this device or the sign-in has expired,
//            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
//            // single sign-on will occur in this branch.
//            showProgressDialog();
//            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                @Override
//                public void onResult(GoogleSignInResult googleSignInResult) {
//                    hideProgressDialog();
//                    handleSignInResult(googleSignInResult);
//                }
//            });
//        }
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }else{
            ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
        }
    }
    // [END onActivityResult]

    String personName,personEmail,personId,personPhoto, gender;
    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            showProgressDialog();
            GoogleSignInAccount acct = result.getSignInAccount();
            personName = acct.getDisplayName();
            personEmail = acct.getEmail();
            personId = acct.getId();


            Log.d(TAG, personEmail +", "+ personName + ", " + personId);
            if( mGoogleApiClient.hasConnectedApi(Plus.API)){

                Person person  = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                if(person.getGender() == 0){
                    gender = "male";
                }else {
                    gender = "female";
                }

                personPhoto = person.getImage().getUrl();
                Log.d(TAG, personName + ", " + id + ", " + email + ", " + personPhoto);
                ParseLogin(personEmail, personId);
            }


        }
    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]


    private void showProgressDialog() {

        progressDialog.setMessage("Loading..");
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if ( progressDialog != null &&  progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.facebook_sign_up:
                onFacebookSignUpClicked();
                break;
            case R.id.google_sign_up:
                signIn();
        }

    }


    private  void onFacebookSignUpClicked(){

        List<String> permissions = new ArrayList<>();
        permissions.add("public_profile");
        permissions.add("email");

        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    makeMeRequest();
                    Log.d("MyApp", "User signed up and logged in through Facebook!");
                } else {
                    ZPreferences.setIsUserLogin(LoginActivity.this, true);
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    LoginActivity.this.finish();
                    Log.d("MyApp", "User logged in through Facebook!");
                }
            }
        });
    }

    String email, id;

    private void makeMeRequest() {
        progressDialog.show();
        progressDialog.setMessage("Getting Personal Info..");
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        if (jsonObject != null) {

                            progressDialog.dismiss();
                            Log.d("profile info", jsonObject.toString());

                            try {
                                // Save the user profile info in a user property
                                ParseUser currentUser = ParseUser.getCurrentUser();
                                if(currentUser != null) {

                                    progressDialog.show();
                                    progressDialog.setMessage("Saving Profile Details");

                                    currentUser.setUsername(jsonObject.getString("email"));
                                    currentUser.setEmail(jsonObject.getString("email"));
                                    currentUser.setPassword(jsonObject.getString("id"));
                                    currentUser.put("firstName", jsonObject.getString("first_name"));
                                    currentUser.put("lastName", jsonObject.getString("last_name"));
                                    currentUser.put("Gender", jsonObject.getString("gender"));
                                    currentUser.put("profilePic", "http://graph.facebook.com/" + jsonObject.getString("id") + "/picture?type=large");
                                    currentUser.put("SignUpType", "Facebook");


                                    email = jsonObject.getString("email");
                                    id = jsonObject.getString("id");
                                    Log.d("SaveIn Background", "start");
                                    currentUser.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            progressDialog.dismiss();

                                            ParseUser.logInInBackground(email, id, new LogInCallback() {
                                                @Override
                                                public void done(ParseUser user, ParseException e) {
                                                    Toast.makeText(getApplicationContext(),
                                                            "Successfully Logged in",
                                                            Toast.LENGTH_LONG).show();
                                                    ZPreferences.setIsUserLogin(LoginActivity.this, true);
                                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    LoginActivity.this.finish();
                                                    // Show the user info

                                                }
                                            });


                                        }
                                    });


                                }

                            } catch (JSONException e) {

                            }
                        } else if (graphResponse.getError() != null) {
                            progressDialog.dismiss();
                            switch (graphResponse.getError().getCategory()) {
                                case LOGIN_RECOVERABLE:
                                    Log.d("LogInActivity", "Authentication error: " + graphResponse.getError());
                                    break;

                                case TRANSIENT:
                                    Log.d("LogInActivity", "Transient error. Try again. " + graphResponse.getError());
                                    break;

                                case OTHER:
                                    Log.d("LogInActivity", "Some other error: " + graphResponse.getError());
                                    break;
                            }
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,gender,first_name, last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void ParseLogin(final String email, final String password){

       Log.d(TAG, email + password +" ");

        ParseUser.logInInBackground(email, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // Hooray! The user is logged in.
                    hideProgressDialog();
                    Log.d("LogInActivity", "User Login Success");
                    ZPreferences.setIsUserLogin(LoginActivity.this, true);
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    LoginActivity.this.finish();
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                    Log.d("LogInActivity", "User Login Failed - "+ e.getCode() + e.getMessage().toString());
                    if(e.getCode() == ParseException.EMAIL_NOT_FOUND || e.getCode() == 101){
                        Log.d("Login", "Email not excist");
                        ParseSignUp(email, password);
                    }else {
                        hideProgressDialog();
                    }

                }
            }
        });

    }

    private void ParseSignUp(String email, String password) {


        Log.d("Email", email +" ");
        Log.d("Password", password + " ");
        ParseUser _user = new ParseUser();
        _user.setUsername(email);
        _user.setPassword(password);
        _user.setEmail(email);
        _user.put("firstName", personName);
        _user.put("Gender", gender);
        _user.put("profilePic", personPhoto +"");
        _user.put("SignUpType", "Google");



        _user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                   hideProgressDialog();
                    // Hooray! Let them use the app now.
                    Log.d("LogInActivity", "User SignUp Success");
                    ZPreferences.setIsUserLogin(LoginActivity.this, true);
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    LoginActivity.this.finish();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Log.d("LogInActivity", "User SignUp Failed" + e.getCode() + e.getMessage().toString());
                    switch (e.getCode()){
                        case ParseException.EMAIL_TAKEN:
                            Log.d("Sign Up", "Email exist");
                            Toast.makeText(LoginActivity.this," Email Excists", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    hideProgressDialog();

                }
            }
        });

    }

}
