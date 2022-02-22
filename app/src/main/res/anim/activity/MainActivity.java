package com.example.doremusic.ui.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.example.doremusic.R;
import com.example.doremusic.adapter.adapter_interface.OnSongClick;
import com.example.doremusic.fragment.ControlFragment;
import com.example.doremusic.fragment.ListSongFragment;
import com.example.doremusic.fragment.PlayFragment;
import com.example.doremusic.fragment.myinterface.OnBtnClick;
import com.example.doremusic.fragment.myinterface.OnMediaDone;
import com.example.doremusic.fragment.myinterface.OnSeekBarChange;
import com.example.doremusic.fragment.myinterface.UpdateSeekBar;
import com.example.doremusic.service.MusicService;

import static com.example.doremusic.fragment.PlayFragment.NEXT_ACTION;
import static com.example.doremusic.fragment.PlayFragment.PLAY_ACTION;
import static com.example.doremusic.fragment.PlayFragment.PREV_ACTION;
import static com.example.doremusic.ui.activity.BeginActivity.listSong;

@RequiresApi(api = Build.VERSION_CODES.R)
public class MainActivity extends AppCompatActivity {

    private Messenger mMessenger;

    private MusicService musicService;

    private Intent playIntent;

    private Boolean isBound = false;

    private int songPos = 0;

    private ServiceConnection musicConnection;

    private ControlFragment controlFragment;

    private ListSongFragment listSongFragment;

    private int curSeekBar;

    private int totalSeekBar;

    private SeekBar seekBar;

    public UpdateSeekBar onSeekBarChange;

    private UpdateSeeBarThread updateSeeBarThread;

    PlayFragment playFragment;

    private final Handler handler = new Handler();

    private Boolean isStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setControlFragment();
        setListSongFragment();

    }

    @Override
    protected void onStart() {
        musicConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

                Log.d(">>>>>", "onServiceConnected: Service is started");

                mMessenger = new Messenger(service);

                MusicService.MusicBinder musicBinder = (MusicService.MusicBinder) service;
                musicService = musicBinder.getService();
                musicService.setSongList(listSong);
                musicService.setOnMediaDone(new OnMediaDone() {
                    @Override
                    public void onDone() {
                        if (songPos != listSong.size()) {
                            songPos++;

                        } else {
                            musicService.setSongPos(0);
                        }
                        musicService.setSongPos(songPos);
                        musicService.playSong();
                    }
                });

                isBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(">>>>>", "onServiceConnected: Service is end");
                isBound = false;
            }

        };

        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
        }
        bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        startService(playIntent);

        super.onStart();
    }


    private void setListSongFragment() {
        listSongFragment = new ListSongFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.list_container, listSongFragment, null)
                .commit();
        listSongFragment.setOnSongClick(new OnSongClick() {
            @Override
            public void onClick(View view, int pos, boolean isLongClick) {
                songPos = pos;
                if (musicService == null) {
                    Log.d(">>>>>>", "onClick: Null cmnr");
                } else {
                    musicService.setSongPos(pos);
                    musicService.playSong();
                    setPlayFragment(pos);
                }
            }


        });
    }

    private void setControlFragment() {
        controlFragment = new ControlFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.control_container, controlFragment, null)
                .commit();

        Bundle b = new Bundle();
        b.putSerializable("song", listSong.get(songPos));
        controlFragment.setArguments(b);
    }

    private void setPlayFragment(int pos) {
        playFragment = new PlayFragment();
        Bundle b = new Bundle();
        b.putInt("song_pos", pos);
        playFragment.setArguments(b);

        updateSeeBarThread = new UpdateSeeBarThread();
        updateSeeBarThread.run();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.play_container, playFragment, null)
                .addToBackStack(null)
                .hide(controlFragment)
                .hide(listSongFragment)
                .commit();

        playFragment.setOnBtnClick(new OnBtnClick() {
            @Override
            public void onClick(View view, int action, boolean isLongClick) {
                switch (action) {
                    case PLAY_ACTION: {
                        doOnPlay();
                        break;
                    }
                    case NEXT_ACTION: {
                        doOnNext();
                        break;
                    }
                    case PREV_ACTION: {
                        doOnPrev();
                        break;
                    }
                }
            }

        });

        playFragment.setOnSeekBarChange(new OnSeekBarChange() {
            @Override
            public void onChange(int cur, int dur) {
                musicService.seeToCur(cur);
            }
        });

    }

    private void doOnPlay() {
        musicService.pauseSong();
    }

    private void doOnNext() {
        if (songPos == listSong.size() - 1) {
            songPos = 0;
        } else {
            songPos++;
        }
        musicService.setSongPos(songPos);
        musicService.playSong();

    }

    private void doOnPrev() {
        if (songPos == 0) {
            songPos = listSong.size() - 1;
        } else {
            songPos--;
        }
        musicService.setSongPos(songPos);
        musicService.playSong();
    }

    @Override
    protected void onDestroy() {
        if (!musicService.isMediaPlaying()) {
            musicService.stopSelf();
            musicService.stopForeground(true);

            isBound = false;
        }
        super.onDestroy();
    }

    class UpdateSeeBarThread implements Runnable {

        @Override
        public void run() {
            if (playFragment != null && isBound) {
                playFragment.updateUi(listSong.get(songPos), musicService.isMediaPlaying());
                int dur = listSong.get(songPos).getTime();
                int currentPosition = musicService.getCurPos();
                playFragment.updateSeekBar(currentPosition, dur);
            }
            handler.postDelayed(this, 150);
        }

    }


}