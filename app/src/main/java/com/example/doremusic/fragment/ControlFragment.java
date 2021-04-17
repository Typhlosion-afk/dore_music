package com.example.doremusic.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.doremusic.R;
import com.example.doremusic.model.Song;


public class ControlFragment extends Fragment {

    private View rootView;

    private ImageButton btnNext, btnPrev, btnPlay;

    private TextView txtName, txtAuthor;

    private Song song;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_control, container,false);
        initData();
        initView();
        initAction();
        return rootView;
    }

    public void initData(){
        song = (Song) getArguments().getSerializable("song");
    }

    public void initView(){
        btnNext = rootView.findViewById(R.id.btn_next_control);
        btnPlay = rootView.findViewById(R.id.btn_play_pause_control);
        btnPrev = rootView.findViewById(R.id.btn_prev_control);

        txtName = rootView.findViewById(R.id.txt_song_control);
        txtAuthor = rootView.findViewById(R.id.txt_author_control);

    }
    public void initAction(){
        txtName.setText(song.getName());
        txtAuthor.setText(song.getAuthor());

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
