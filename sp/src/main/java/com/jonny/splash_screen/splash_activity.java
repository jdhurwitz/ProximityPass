package com.jonny.splash_screen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.mycompany.p2pwifi.P2pMain;


public class splash_activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.splash);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //set content view AFTER ABOVE sequence (to avoid crash)
        this.setContentView(R.layout.splash);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(2000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent p2p = new Intent(splash_activity.this, P2pMain.class);
                    startActivity(p2p);
                }
            }
        };
        timerThread.start();
    }
    @Override
    protected  void onPause(){
        super.onPause();
        finish();
    }
}
