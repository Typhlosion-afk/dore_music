package com.example.doremusic.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.PowerManager;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.doremusic.R;
import com.example.doremusic.broadcast.MusicBroadcast;
import com.example.doremusic.fragment.myinterface.OnMediaDone;
import com.example.doremusic.model.Song;
import com.example.doremusic.ui.activity.MainActivity;
import static com.example.doremusic.ui.activity.BeginActivity.listSong;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Future;

@RequiresApi(api = Build.VERSION_CODES.R)
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {

    private static final String CHANNEL_ID = "PLAYING_CHANNEL";

    private static final String NOTIFICATION_ACTION = "com.example.doremusic.broadcast.NOTIFICATION_ACTION";

    MediaPlayer mediaPlayer = null;

    private final ArrayList<Song> mListSong = new ArrayList<>();

    private int songPos;

    private Song song;

    private final IBinder musicBinder = new MusicBinder();

    private int curPos = 0;

    private OnMediaDone onMediaDone;

    private RemoteViews largeNotificationLayout;

    private RemoteViews smallNotificationLayout;

    private Notification mNotification;

    public final Handler handler = new Handler();

    public NotificationManagerCompat notificationManagerCompat;

    public NotificationCompat.Builder builder;

    public UpdateNotificationThread thread;

    public HandlerThread handlerThread;

    public Boolean isStart = false;

    @Override
    public void onCreate() {
        super.onCreate();

        initData();

        createNotificationChanel();
        mNotification = createNotification();
        startForeground(1, mNotification);
        thread = new UpdateNotificationThread();
    }

    private void initData(){
        mListSong.addAll(listSong);
        songPos = 0;
        song = mListSong.get(songPos);
        mediaPlayer = new MediaPlayer();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBinder;
    }

    @SuppressLint({"NonConstantResourceId", "NewApi"})
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null || intent.getAction() == null) {
            stopForeground(true);
            stopSelf();
            return START_NOT_STICKY;
        }
        if(!intent.getAction().equals(NOTIFICATION_ACTION) ){
            Log.d("TAG", "onStartCommand: " + intent.getIntExtra("action",-1));
            switch (Integer.parseInt(intent.getAction())){
                case R.id.noti_next:{
                    nextSong();
                    updateNotification(song);
                    break;
                }
                case R.id.noti_prev:{
                    prevSong();

                    updateNotification(song);
                    break;
                }
                case R.id.noti_play_pause:{
                    pauseSong();
                    updateNotification(song);
                    break;
                }
                case R.id.noti_end:{
                    if(mediaPlayer!= null) {
                        mediaPlayer.reset();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                    stopSelf();
                    stopForeground(true);
                    Log.d("sssss", "onStartCommand: " + "stop");
                    break;
                }
            }
        }
        return START_NOT_STICKY;
    }

    private void stopForegroundSv(){

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    public void initMediaPlayer() {
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
    }

    private void destroyPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.reset();
                mediaPlayer.release();
                Log.d("TAG", "Player destroyed");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mediaPlayer = null;
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer.start();

        updateNotification(song);
        thread.run();
    }

    private Notification createNotification() {
        PendingIntent pendingIntent;

        notificationManagerCompat = NotificationManagerCompat.from(this);

        largeNotificationLayout = new RemoteViews(getPackageName(),
                R.layout.notification_large);

        smallNotificationLayout = new RemoteViews(getPackageName(),
                R.layout.notification_small);

        largeNotificationLayout.setOnClickPendingIntent(R.id.noti_prev, getNotificationPendingIntent(R.id.noti_prev));
        largeNotificationLayout.setOnClickPendingIntent(R.id.noti_next, getNotificationPendingIntent(R.id.noti_next));
        largeNotificationLayout.setOnClickPendingIntent(R.id.noti_play_pause, getNotificationPendingIntent(R.id.noti_play_pause));
        largeNotificationLayout.setOnClickPendingIntent(R.id.noti_end, getNotificationPendingIntent(R.id.noti_end));



        builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_app_small)
                .setCustomBigContentView(largeNotificationLayout)
                .setCustomContentView(smallNotificationLayout)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(isMediaPlaying())
                .setOnlyAlertOnce(true);

        return builder.build();
    }

    private void updateNotification(Song song) {

        if(builder != null && mediaPlayer!= null) {
            largeNotificationLayout.setTextViewText(R.id.noti_name_song, song.getName());
            largeNotificationLayout.setTextViewText(R.id.noti_author, song.getAuthor());

            smallNotificationLayout.setTextViewText(R.id.noti_name_song, song.getName());
            smallNotificationLayout.setTextViewText(R.id.noti_author, song.getAuthor());

            if (mediaPlayer.isPlaying()) {
                largeNotificationLayout.setImageViewResource(R.id.noti_play_pause, R.drawable.ic_baseline_pause_24);
            } else {
                largeNotificationLayout.setImageViewResource(R.id.noti_play_pause, R.drawable.ic_baseline_play_arrow_24);
            }
        }
    }

    public void createNotificationChanel() {
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

    public PendingIntent getNotificationPendingIntent(@IdRes int id){
        Log.d("TAG", "getNotificationPendingIntent: " + id);
        Intent i = new Intent(this, MusicService.class);
        i.setAction(String.valueOf(id));
        i.putExtra("action", id);
        return PendingIntent.getService(this, 0, i, 0);
    }



    public void setSongList(ArrayList<Song> list) {
        this.mListSong.addAll(list);
    }


    @Override
    public boolean onUnbind(Intent intent) {
        handler.removeCallbacksAndMessages(null);
        stopSelf();
        stopForeground(true);
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        onMediaDone.onDone();
    }

    public void playSong() {
        song = mListSong.get(songPos);
        Uri uri = Uri.parse(song.getPath());
        try {
            mediaPlayer.reset();
            initMediaPlayer();
            mediaPlayer.setDataSource(getApplicationContext(), uri);

        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.prepareAsync();
    }

    public void nextSong(){
        if(mediaPlayer == null){
            initMediaPlayer();
        }
        if (songPos == listSong.size() - 1) {
            songPos = 0;
        } else {
            songPos++;
        }
        playSong();
    }

    public void prevSong(){
        if (songPos == 0) {
            songPos = listSong.size() - 1;
        } else {
            songPos--;
        }
        playSong();
    }

    public int getCurPos() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public boolean isMediaPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    public void pauseSong() {
        if (isMediaPlaying()) {
            mediaPlayer.pause();
            curPos = mediaPlayer.getCurrentPosition();
        } else {
            mediaPlayer.seekTo(curPos - 5);
            mediaPlayer.start();
        }
    }

    public void seeToCur(int mSec) {
        mediaPlayer.seekTo(mSec);

    }

    public void setSongPos(int index) {
        songPos = index;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    public void stop() {
        mediaPlayer.stop();
        this.stopSelf();
    }

    public Song getPlayingSong() {
        return listSong.get(songPos);
    }


    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public void setOnMediaDone(OnMediaDone onMediaDone) {
        this.onMediaDone = onMediaDone;
    }

    class UpdateNotificationThread implements Runnable{
        boolean isStop = false;
        @Override
        public void run() {
            if(!isStop) {
                notificationManagerCompat.notify(1,builder.build());
                handler.postDelayed(this, 100);
            }else {
                Log.d("TAG", "run: " + "stop");
            }
        }

        private void setStop(){
            isStop = true;
        }

        public void stop(){
            isStop = true;
        }


        public void restart(){

        }
    };
}
