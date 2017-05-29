package in.co.conflicto.conflictoapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import in.co.conflicto.conflictoapp.R;
import in.co.conflicto.conflictoapp.models.User;
import in.co.conflicto.conflictoapp.utilities.Constants;
import in.co.conflicto.conflictoapp.utilities.JsonObjectRequestWithAuth;
import in.co.conflicto.conflictoapp.utilities.MyApplication;
import in.co.conflicto.conflictoapp.utilities.SessionData;
import in.co.conflicto.conflictoapp.utilities.UIUtils;
import in.co.conflicto.conflictoapp.utilities.Utilis;
import in.co.conflicto.conflictoapp.utilities.VolleySingelton;

public class MainActivity extends AppCompatActivity {

    private CallbackManager mCallbackManager;
    private AccessToken accessToken;
    private AuthCredential credential;
    private FirebaseAuth mAuth;
    private final AppCompatActivity activity = this;
    private Boolean handleFacebookAccessTokenLock = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email"));
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Utilis.log("debug", "fb", loginResult.toString());
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Utilis.log("debug", "fb", "Cancelled Request");
            }

            @Override
            public void onError(FacebookException error) {
                Utilis.log("debug", "fb", Arrays.toString(error.getStackTrace()));
            }

        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UIUtils.showLoader(this);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        UIUtils.hideLoader(this);
        if (accessToken !=null){
            handleFacebookAccessToken(accessToken);
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        synchronized(this) {
            if (!this.handleFacebookAccessTokenLock) {
                this.handleFacebookAccessTokenLock = true;
                accessToken = token;
                credential = FacebookAuthProvider.getCredential(token.getToken());
                mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Log.i("facebook", Arrays.toString(task.getException().getStackTrace()));
                        Toast.makeText(activity, "Facebook Authentication failed", Toast.LENGTH_SHORT).show();
                    } else {
                        processFirebaseAuthenticationToBackend();
                    }
                });
            }
        }
    }

    private void processFirebaseAuthenticationToBackend() {
        JSONObject js = new JSONObject();
        try {
            UIUtils.showLoader(activity);
            JSONObject fb_token = new JSONObject(new ObjectMapper().writeValueAsString(accessToken));
            js.put("firebase_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
            js.put("fb_token", fb_token.getString("token"));

            String fcmToken = FirebaseInstanceId.getInstance().getToken();
            if (fcmToken != null) {
                SessionData.setString(Constants.FCM_TOKEN, fcmToken);
            }
            js.put("fcm_token", SessionData.getString(Constants.FCM_TOKEN, ""));

            RequestQueue requestQueue = VolleySingelton.getInstance().getRequestQueue();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constants.SERVER_URL + "/user/authenticate", js,
                (JSONObject response) -> {
                    try {
                        User user = new User(response.getJSONObject(Constants.FBPROFILE_KEY));
                        user.fcmToken = response.getJSONObject("userprofile").getString(Constants.FCM_TOKEN);
                        user.firebaseId = response.getJSONObject("userprofile").getString(Constants.FIREBASE_ID_KEY);
                        user.uuid = response.getJSONObject("userprofile").getString(Constants.UUID_KEY);
                        SessionData.currentUser = user;
                        MyApplication.getInstance().saveCurrentUser(SessionData.currentUser);


                    } catch (JSONException e) {
                        Utilis.exc("volley", e);
                    }
                    UIUtils.hideLoader(activity);
                    Intent intent = new Intent(activity, HomeNavActivity.class);
                    UIUtils.startActivity(intent, true);
                },(VolleyError error) -> {
                    UIUtils.hideLoader(activity);
                    Utilis.exc("volley", error);
                });

            requestQueue.add(request);

            } catch (JSONException e) {
                UIUtils.showLoader(activity);
                Utilis.exc("firebase", e);
            } catch (JsonProcessingException e) {
                UIUtils.showLoader(activity);
                Utilis.exc("jackson", e);
            }

    }

}