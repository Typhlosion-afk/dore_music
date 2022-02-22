package com.example.doremusic.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.doremusic.R;
import com.example.doremusic.fragment.myinterface.OnBtnClick;
import com.example.doremusic.fragment.myinterface.OnSeekBarChange;
import com.example.doremusic.model.Song;

import static com.example.doremusic.ui.activity.BeginActivity.listSong;

@RequiresApi(api = Build.VERSION_CODES.R)
public class PlayFragment extends Fragment implements View.OnClickListener {

    public static final int PAUSE_ACTION = 0;

    public static final int PLAY_ACTION = 1;

    public static final int NEXT_ACTION = 2;

    public static final int PREV_ACTION = 3;

    private ImageButton btnNext, btnPrev, btnPlay, btnLoopMode, btnMixMode;

    private ProgressBar progressBar;

    private TextView txtSongName, txtSongAuthor, txtCur, txtDur;

    private View rootView;

    private int pos;

    private Song song;

    private boolean isPlaying = false;

    private OnBtnClick onBtnClick;

    private OnSeekBarChange onSeekBarChange;

    public SeekBar seekBar;

    private int seekValue = 0;

    private ImageView imageView;

    private boolean isSeekBarTouch = false;

    private ViewGroup viewGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = container;
        rootView = inflater.inflate(R.layout.fragment_playing, container, false);

        initData();
        initView();
        initAction();

        return rootView;
    }


    private void initData() {
        pos = 0;
        if (getArguments() != null) {
            pos = getArguments().getInt("song_pos");
        }
        song = listSong.get(pos);
    }

    public void initView() {
//        imageView = rootView.findViewById(R.id.play_bg_id);
        btnNext = rootView.findViewById(R.id.btn_next);
        btnPrev = rootView.findViewById(R.id.btn_prev);
        btnPlay = rootView.findViewById(R.id.btn_play_pause);
        btnLoopMode = rootView.findViewById(R.id.btn_mode2);
        btnMixMode = rootView.findViewById(R.id.btn_mode1);
        seekBar = rootView.findViewById(R.id.seek_bar);
        seekBar.setMax(song.getTime());

        btnNext.setBackgroundResource(R.drawable.ic_baseline_skip_next_24);
        btnPrev.setBackgroundResource(R.drawable.ic_baseline_skip_previous_24);
        btnPlay.setBackgroundResource(R.drawable.ic_baseline_pause_24);
        btnLoopMode.setBackgroundResource(R.drawable.ic_baseline_repeat_24);
        btnMixMode.setBackgroundResource(R.drawable.ic_baseline_shuffle_24);

        txtSongName = rootView.findViewById(R.id.txt_song_name);
        txtSongAuthor = rootView.findViewById(R.id.txt_song_author);

        txtDur = rootView.findViewById(R.id.txt_dur);
        txtCur = rootView.findViewById(R.id.txt_cur);

        txtSongAuthor.setText(song.getAuthor());
        txtSongName.setText(song.getName());

    }

    public void initAction() {
        rootView.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                isSeekBarTouch = fromUser;
                if (fromUser) {
                    seekBar.setProgress(progress);
                    seekValue = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekBarTouch = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                onSeekBarChange.onChange(seekValue, seekBar.getMax());
                isSeekBarTouch = false;
            }
        });


    }

    public void updateSeekBar(int cur, int dur) {
        if (seekBar != null) {
            if (!isSeekBarTouch) {
                seekBar.setMax(dur);
                seekBar.setProgress(cur);
            }
            txtDur.setText(coverTime(seekBar.getMax()));
            txtCur.setText(coverTime(seekBar.getProgress()));
        }
    }

    public void updateUi(Song song, boolean isPlaying) {
        this.isPlaying = isPlaying;
        this.song = song;
        if (txtSongName != null && txtSongAuthor != null) {
            txtSongAuthor.setText(song.getAuthor());
            txtSongName.setText(song.getName());
        }
        if (btnPlay != null) {
            if (isPlaying) {
                btnPlay.setBackgroundResource(R.drawable.ic_baseline_pause_24);
            } else {
                btnPlay.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
            }
        }
    }

    private String coverTime(int i) {
        i = i / 1000;
        int s = i % 60;
        int m = i / 60;
        return (m < 10 ? "0" : "") + m + ":" + (s < 10 ? "0" : "") + s;
    }


    @SuppressLint({"NonConstantResourceId", "ResourceType"})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next: {
                nextEvent(v);
                break;
            }
            case R.id.btn_prev: {
                prevEvent(v);
                break;
            }
            case R.id.btn_play_pause: {
                playPauseEvent(v);
                break;
            }

            default: {
                break;
            }
        }
    }

    private void nextEvent(View v) {
        onBtnClick.onClick(v, NEXT_ACTION, false);
        if (pos != listSong.size() - 1) {
            pos++;
        } else {
            pos = 0;
        }
        song = listSong.get(pos);
        setText(song);
    }

    private void prevEvent(View v) {
        onBtnClick.onClick(v, PREV_ACTION, false);
        if (pos != 0) {
            pos--;
        } else {
            pos = listSong.size() - 1;
        }
        song = listSong.get(pos);
        setText(song);
    }

    private void playPauseEvent(View v) {
        onBtnClick.onClick(v, PLAY_ACTION, false);
        onSeekBarChange.onChange(seekBar.getProgress(), seekBar.getMax());
        if (isPlaying) {
            btnPlay.setBackgroundResource(R.drawable.ic_baseline_pause_24);
        } else {
            btnPlay.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
        }

    }

    public void setOnBtnClick(OnBtnClick click) {
        onBtnClick = click;
    }

    public void setText(Song song) {
        txtSongName.setText(song.getName());
        txtSongAuthor.setText(song.getAuthor());
    }


    public void setOnSeekBarChange(OnSeekBarChange onSeekBarChange) {
        this.onSeekBarChange = onSeekBarChange;
    }
}
