package com.mvopo.streaminglib;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mvopo.streamapi.StreamApi;
import com.mvopo.streamapi.contracts.KeyCallback;
import com.mvopo.streamapi.model.Constants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MAINACTIVITY";

    private TextView streamKeyTv;
    private Button fbSignBtn, ytubeSignBtn;
    private StreamApi streamApi;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        printHashKey();
        streamApi = new StreamApi(this);

        streamKeyTv = findViewById(R.id.stream_key_tv);
        fbSignBtn = findViewById(R.id.fb_sign_in_btn);
        ytubeSignBtn = findViewById(R.id.ytube_sign_in_btn);
        progressBar = findViewById(R.id.progress_loader);

        fbSignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                streamApi.createStreamKey(Constants.FACEBOOK_KEY, new KeyCallback() {
                    @Override
                    public void onSuccess(String key) {
                        streamKeyTv.setText(key);
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });

        ytubeSignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                streamApi.createStreamKey(Constants.YOUTUBE_KEY, new KeyCallback() {
                    @Override
                    public void onSuccess(String key) {
                        streamKeyTv.setText(key);
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    public void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i(TAG, "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "printHashKey()", e);
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        streamApi.onActivityResult(requestCode, resultCode, data);
    }
}
