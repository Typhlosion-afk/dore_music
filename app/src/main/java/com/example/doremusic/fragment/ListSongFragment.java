package com.example.doremusic.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doremusic.R;
import com.example.doremusic.adapter.ListSongAdapter;
import com.example.doremusic.adapter.OnSongClick;

import static com.example.doremusic.ui.activity.BeginActivity.listSong;

@RequiresApi(api = Build.VERSION_CODES.R)
public class ListSongFragment extends Fragment {

    private View rootView;

    private OnSongClick onSongClick;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_list_song,container, false);
        setAdapter();
        return rootView;
    }

    private void setAdapter(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.scrollToPosition(0);

        ListSongAdapter listSongAdapter = new ListSongAdapter(listSong, getContext());
        RecyclerView recyclerView = rootView.findViewById(R.id.recycle_view_container);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(listSongAdapter);

        listSongAdapter.setOnSongClick(new OnSongClick() {
            @Override
            public void onClick(View view, int pos, boolean isLongClick) {
                onSongClick.onClick(view, pos, isLongClick);
            }
        });

    }

    public void setOnSongClick(OnSongClick onSongClick){
        this.onSongClick = onSongClick;
    }
}
