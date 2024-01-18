package com.example.soundbox_du_an_md31.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundbox_du_an_md31.Model.SongDown;
import com.example.soundbox_du_an_md31.databinding.ItemSongDownloadBinding;
import com.example.soundbox_du_an_md31.listener.IOnClickItemListenerDown;
import com.example.soundbox_du_an_md31.utils.GlideUtils;

import java.util.List;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.SongViewHolder>{
    private List<SongDown> mListSongs;
    public final IOnClickItemListenerDown iOnClickSongItemListener;

    public DownloadAdapter(List<SongDown> mListSongs, IOnClickItemListenerDown iOnClickSongItemListener) {
        this.mListSongs = mListSongs;
        this.iOnClickSongItemListener = iOnClickSongItemListener;
    }

    public void setData(List<SongDown> list){
        this.mListSongs = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public com.example.soundbox_du_an_md31.Adapter.DownloadAdapter.SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSongDownloadBinding itemSongBinding = ItemSongDownloadBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DownloadAdapter.SongViewHolder(itemSongBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.soundbox_du_an_md31.Adapter.DownloadAdapter.SongViewHolder holder, int position) {
        SongDown song = mListSongs.get(position);
        if (song == null) {
            return;
        }
        holder.mItemSongBinding.imgVip.setVisibility(View.GONE);
        GlideUtils.loadUrl(song.getImage(), holder.mItemSongBinding.imgSong);
        holder.mItemSongBinding.tvSongName.setText(song.getTitle());
        holder.mItemSongBinding.tvArtist.setText(song.getArtist());
        holder.mItemSongBinding.tvCountView.setText(String.valueOf(song.getCount()));
        holder.mItemSongBinding.layoutItem.setOnClickListener(v -> iOnClickSongItemListener.onClickItemSongDown(song));


    }

    @Override
    public int getItemCount() {
        return null == mListSongs ? 0 : mListSongs.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {

        private final ItemSongDownloadBinding mItemSongBinding;

        public SongViewHolder(ItemSongDownloadBinding itemSongBinding) {
            super(itemSongBinding.getRoot());
            this.mItemSongBinding = itemSongBinding;
        }

    }
}
