package com.example.doremusic.broadcast;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.doremusic.R;
import com.example.doremusic.service.MusicService;

public class MusicBroadcast extends BroadcastReceiver {

    MusicService musicService;

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TAG", "onReceive:"+ intent.getAction());
        Log.d("Tag", "" + intent.getIntExtra("action",-1));
        if(intent.getAction().equals("com.example.doremusic.broadcast.NOTIFICATION_ACTION")){
            switch (intent.getIntExtra("action",-1)){
                case R.id.noti_next:{

                    break;
                }
            }
        }
    }

}