package com.anand.upiqr.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.VideoView;

import com.anand.upiqr.R;
import com.anand.upiqr.Utils.AppPreferences;
import com.anand.upiqr.Utils.ConnectionChecking;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SplashActivity extends AppCompatActivity {
    ConnectionChecking checking;
    SweetAlertDialog dialog;
    private boolean videoCompleted = false;
    VideoView videoView;
    // path of video in assets folder


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        checking = new ConnectionChecking();
        videoView = findViewById(R.id.videoView);
        if (checking.isConnectingToInternet(SplashActivity.this)) {
            StringBuilder stringBuilder = new StringBuilder().append("android.resource://").append(getPackageName()).append("/").append(R.raw.splash_new);

            videoView.setVideoURI(Uri.parse(stringBuilder.toString()));
            videoView.start();

            videoView.setBackgroundColor(getResources().
                    getColor(android.R.color.white));
            videoView.setZOrderOnTop(true);
            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                    //wait for 3 seconds
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    videoCompleted = true;
                    if (videoCompleted) {
                        if (AppPreferences.getInstance(getApplicationContext()).getBoolean(AppPreferences.Key.LOGGED)) {
                            goToNextScreen("HOME");
                        } else {
                            goToNextScreen("LOGIN");
                        }

                    }
                    return false;
                }


            });

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    videoCompleted = true;
                    if (videoCompleted) {
                        if (AppPreferences.getInstance(getApplicationContext()).getBoolean(AppPreferences.Key.LOGGED)) {
                            goToNextScreen("HOME");
                        } else {
                            goToNextScreen("LOGIN");
                        }
                    }
                }
            });

        } else {
            dialog = new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.PROGRESS_TYPE);
            dialog.setCancelable(false);
            dialog.setTitle("Network Error");
            dialog.setContentText("Please check Your Network Connection");
            dialog.setConfirmText("OK");
            dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    finishAffinity();
                }
            });
            dialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
            dialog.show();
        }


//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (checking.isConnectingToInternet(SplashActivity.this)) {
//
//                    // Check User Is Logged In
//                    if(AppPreferences.getInstance(getApplicationContext()).getBoolean(AppPreferences.Key.LOGGED)) {
//                        Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                } else {
//                    dialog = new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.PROGRESS_TYPE);
//                    dialog.setCancelable(false);
//                    dialog.setTitle("Network Error");
//                    dialog.setContentText("Please check Your Network Connection");
//                    dialog.setConfirmText("OK");
//                    dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                        @Override
//                        public void onClick(SweetAlertDialog sweetAlertDialog) {
//                            finishAffinity();
//                        }
//                    });
//                    dialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
//                    dialog.show();
//                }
//            }
//        },3000);
    }

    private void goToNextScreen(String activity) {
        if (activity.equals("HOME"))
            startActivity(new Intent(this, HomeActivity.class));
        else if (activity.equals("LOGIN"))
            startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}