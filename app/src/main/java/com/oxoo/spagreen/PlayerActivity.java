package com.oxoo.spagreen;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

public class PlayerActivity extends AppCompatActivity {


    private String V_URL = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4";

    private VideoView videoView;

    private MediaController mediacontroller;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);


        videoView=findViewById(R.id.videoView);

        mediacontroller = new SpaGreenMediaController(this);

        videoView.setVideoURI(Uri.parse(V_URL));
        videoView.requestFocus();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

                        videoView.setMediaController(mediacontroller);
                        mediacontroller.setAnchorView(videoView);
                    }
                });

                videoView.start();
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
               mp.release();
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        videoView.stopPlayback();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        videoView.stopPlayback();
    }

    public void setPlayer(String s){


        if (s.equals("yes")){

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            videoView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        }else {

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            //videoView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            videoView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        }

    }


    public class SpaGreenMediaController extends MediaController {

        private ImageButton fullScreen;
        private boolean isFullScreen=false;

        public SpaGreenMediaController(Context context) {
            super(context);
        }

        @Override
        public void setAnchorView(View view) {

            super.setAnchorView(view);

            //image button for full screen to be added to media controller
            fullScreen = new ImageButton (super.getContext());

//            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(100, 100);


            params.gravity = Gravity.RIGHT;
            params.rightMargin = 20;
            params.topMargin=30;
            addView(fullScreen, params);

            if(isFullScreen){
                fullScreen.setImageResource(R.drawable.ic_fullscreen);
            }else{
                fullScreen.setImageResource(R.drawable.ic_fullscreen);
            }

            //add listener to image button to handle full screen and exit full screen events
            fullScreen.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(isFullScreen){
                        isFullScreen=false;
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        videoView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                    }else{
                        isFullScreen=true;
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        videoView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    }


                    //((Activity)getContext()).startActivity(intent);
                }
            });
        }
    }



}
