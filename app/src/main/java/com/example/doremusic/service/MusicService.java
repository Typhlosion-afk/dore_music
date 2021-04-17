package com.example.doremusic.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.doremusic.R;
import com.example.doremusic.fragment.myinterface.OnMediaDone;
import com.example.doremusic.model.Song;
import com.example.doremusic.notification.MusicNotification;
import com.example.doremusic.ui.activity.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

@RequiresApi(api = Build.VERSION_CODES.R)
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private static final String CHANNEL_ID = "PLAYING_CHANNEL";

    MediaPlayer mediaPlayer = null;

    private ArrayList<Song> mListSong = new ArrayList<>();

    private IBinder iBinder;

    private int songPos;

    private Song song;

    private final IBinder musicBinder = new MusicBinder();

    private int curPos;

    private OnMediaDone onMediaDone;

    @Override
    public void onCreate() {
        super.onCreate();
        songPos = 0;
        mediaPlayer = new MediaPlayer();
        //initMediaPlayer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return musicBinder;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if(!isMediaPlaying() && mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        super.onDestroy();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();

        createNotificationChanel();

        Intent notificationIntent = new Intent(this, MainActivity.class);

        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setTicker(song.getName())
                .setSmallIcon(R.drawable.card_view_icon)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(song.getAuthor())
                .build();

        MusicNotification musicNotification = new MusicNotification();
        musicNotification.createNotificationChanel(this);
        musicNotification.setView(this);

        startForeground(1, notification);

    }

    public void createNotificationChanel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "play";
            String description = "just play";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void setSongList(ArrayList<Song> list){
        this.mListSong.addAll(list);
    }

    public void initMediaPlayer(){
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if(!isMediaPlaying()){
            mediaPlayer.reset();
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
//        if(songPos != mListSong.size() - 1) {
//            songPos++;
//            playSong();
//
//        }
        onMediaDone.onDone();
    }



    public void playSong(){

        song = mListSong.get(songPos);
        Uri uri = Uri.parse(song.getPath());
        try{
            mediaPlayer.reset();
            mediaPlayer.setDataSource(getApplicationContext(), uri);
            initMediaPlayer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.prepareAsync();
    }

    public int getCurPos(){
        return mediaPlayer.getCurrentPosition();
    }

    public boolean isMediaPlaying(){
        return mediaPlayer.isPlaying();
    }

    public void stop(){
        mediaPlayer.reset();
        mediaPlayer.stop();
    }

    public long getDuration(){
        return mediaPlayer.getDuration();
    }

    public void pauseSong(){
        if(isMediaPlaying()) {
            mediaPlayer.pause();
            curPos = mediaPlayer.getCurrentPosition();
        }else {
            mediaPlayer.seekTo(curPos - 5);
            mediaPlayer.start();
        }
    }

    public void seeToCur(int mSec){
        mediaPlayer.seekTo(mSec);
    }

    public void nextSong(){

    }

    public void prevSong(){

    }

    public void setSongPos(int index){
        songPos = index;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }


    public class MusicBinder extends Binder{
        public MusicService getService(){
            return MusicService.this;
        }
    }

    public void releaseWakeLock(){

    }

    public void setOnMediaDone(OnMediaDone onMediaDone){
        this.onMediaDone = onMediaDone;
    }

}
