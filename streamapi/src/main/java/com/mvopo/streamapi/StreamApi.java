package com.mvopo.streamapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.mvopo.streamapi.contracts.KeyCallback;
import com.mvopo.streamapi.helper.JSONApi;
import com.mvopo.streamapi.model.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class StreamApi {
    public static KeyCallback ytubeCallback;

    Context mContext;
    CallbackManager manager;

    public StreamApi(Context mContext) {
        this.mContext = mContext;
    }

    public void createStreamKey(int streamApp, final KeyCallback callback) {
        switch (streamApp) {
            case Constants.FACEBOOK_KEY:
                manager = CallbackManager.Factory.create();
                LoginManager.getInstance().registerCallback(manager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        final AccessToken accessToken = loginResult.getAccessToken();

                        String user_id = accessToken.getUserId();
                        try {
                            GraphRequest request = GraphRequest.newPostRequest(
                                    accessToken,
                                    "/" + user_id + "/live_videos",
                                    new JSONObject("{\"title\":\"Sample Video\",\"description\":\"My sample video.\"}"),
                                    new GraphRequest.Callback() {
                                        @Override
                                        public void onCompleted(GraphResponse response) {
                                            JSONObject result = response.getJSONObject();
                                            try {
                                                String streamUrl = result.getString("stream_url");
                                                String streamKey = streamUrl.split("rtmp/")[1];

                                                callback.onSuccess(streamKey);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                            request.executeAsync();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {

                    }
                });

                LoginManager.getInstance().logInWithPublishPermissions(
                        (Activity) mContext,
                        Arrays.asList("publish_video"));
                break;
            case Constants.YOUTUBE_KEY:

                ytubeCallback = callback;

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestServerAuthCode(Constants.client_id)
                        .requestScopes(new Scope("https://www.googleapis.com/auth/youtube"))
                        .build();

                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(mContext, gso);

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                ((Activity) mContext).startActivityForResult(signInIntent,
                        Constants.YOUTUBE_KEY_CODE);
                break;
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.FACEBOOK_KEY_CODE:
                manager.onActivityResult(requestCode, resultCode, data);
                break;
            case Constants.YOUTUBE_KEY_CODE:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);

                    JSONObject request = new JSONObject();
                    request.accumulate("code", account.getServerAuthCode());
                    request.accumulate("client_id", Constants.client_id);
                    request.accumulate("client_secret", Constants.client_secret);
                    request.accumulate("grant_type", "authorization_code");

                    JSONApi.getInstance(mContext).getToken(request);

                } catch (ApiException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
        }
    }
}
