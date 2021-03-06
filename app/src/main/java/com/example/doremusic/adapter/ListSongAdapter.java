package com.example.doremusic.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doremusic.R;
import com.example.doremusic.adapter.adapter_interface.OnSongClick;
import com.example.doremusic.model.Song;

import java.util.ArrayList;

public class ListSongAdapter extends RecyclerView.Adapter<ListSongAdapter.AdapterViewHolder> {

    private ArrayList<Song> mListSong = new ArrayList<>();

    public Context context;

    public OnSongClick mOnSongClick;

    public ListSongAdapter(ArrayList<Song> ls, Context context) {
        mListSong.addAll(ls);
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        view = inflater.inflate(R.layout.card_view_song, parent, false);

        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewHolder holder, int position) {
        holder.txtName.setText(mListSong.get(position).getName());
        holder.txtAuthor.setText(mListSong.get(position).getAuthor());
    }

    @Override
    public int getItemCount() {
        return mListSong.size();
    }

    class AdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtName, txtAuthor;
        CardView cardView;

        public AdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAuthor = itemView.findViewById(R.id.txt_card_author);
            txtName = itemView.findViewById(R.id.txt_card_name);
            cardView = itemView.findViewById(R.id.card_list);

            cardView.setCardBackgroundColor(Color.TRANSPARENT);
            cardView.setElevation(0);
            itemView.setOnClickListener(this);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            mOnSongClick.onClick(v, getAdapterPosition(), false);
        }
    }

    public void setOnSongClick(OnSongClick mOnSongClick) {
        this.mOnSongClick = mOnSongClick;
    }
}
