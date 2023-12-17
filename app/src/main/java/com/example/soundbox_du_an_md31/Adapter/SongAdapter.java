package com.example.soundbox_du_an_md31.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundbox_du_an_md31.Model.Song;
import com.example.soundbox_du_an_md31.Service.MusicService;
import com.example.soundbox_du_an_md31.databinding.ItemSongBinding;
import com.example.soundbox_du_an_md31.listener.IOnClickSongItemListener;
import com.example.soundbox_du_an_md31.utils.GlideUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private final List<Song> mListSongs;
    public final IOnClickSongItemListener iOnClickSongItemListener;


    public SongAdapter(List<Song> mListSongs, IOnClickSongItemListener iOnClickSongItemListener) {
        this.mListSongs = mListSongs;
        this.iOnClickSongItemListener = iOnClickSongItemListener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSongBinding itemSongBinding = ItemSongBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SongViewHolder(itemSongBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = mListSongs.get(position);
        if (song == null) {
            return;
        }
        if (song.isCopyrighted() == false) {

            holder.mItemSongBinding.imgVip.setVisibility(View.GONE);
            GlideUtils.loadUrl(song.getImage(), holder.mItemSongBinding.imgSong);
            holder.mItemSongBinding.tvSongName.setText(song.getTitle());
            holder.mItemSongBinding.tvArtist.setText(song.getArtist());
            holder.mItemSongBinding.tvCountView.setText(String.valueOf(song.getCount()));
            holder.mItemSongBinding.layoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     iOnClickSongItemListener.onClickItemSong(song);

                    FirebaseAuth mAuth = FirebaseAuth.getInstance();

                    mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                        private boolean lichSu1= false;
                        @Override
                        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                            if(!lichSu1){
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                if (user != null) {
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference myRef = database.getReference("history").child(user.getUid());
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("artist", song.getArtist());
                                    data.put("count", song.getCount());
                                    data.put("genre", song.getGenre());
                                    data.put("id", song.getId());
                                    data.put("image", song.getImage());
                                    data.put("latest", song.isLatest());
                                    data.put("title", song.getTitle());
                                    data.put("url", song.getUrl());
                                    data.put("timestamp", song.getTimestamp());
                                    myRef.child(String.valueOf(song.getId())).setValue(data, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            Log.d("lichsu", "onComplete: 111111111111");

                                        }
                                    });
                                } else {

                                }
                                lichSu1= true;
                            }

                        }
                    });
                }
            });
        } else {
            holder.mItemSongBinding.imgVip.setVisibility(View.VISIBLE);
            GlideUtils.loadUrl(song.getImage(), holder.mItemSongBinding.imgSong);
            holder.mItemSongBinding.tvSongName.setText(song.getTitle());
            holder.mItemSongBinding.tvArtist.setText(song.getArtist());
            holder.mItemSongBinding.tvCountView.setText(String.valueOf(song.getCount()));
            holder.mItemSongBinding.layoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user == null) {
                        Toast.makeText(view.getContext(), "Nhạc bản quyền , đăng kí tài khoản vip để nghe", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

// Truy cập đến nút (node) cần kiểm tra
                    DatabaseReference booleanRef = databaseRef.child("users/" + user.getUid() + "/isVIP");
                    // Đọc giá trị boolean từ nút đó
                    booleanRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Kiểm tra xem giá trị có tồn tại hay không
                            if (dataSnapshot.exists()) {
                                Log.d("history", "đã vào: ");
                                // Lấy giá trị boolean từ DataSnapshot
                                Boolean booleanValue = dataSnapshot.getValue(Boolean.class);

                                // Kiểm tra giá trị boolean
                                if (booleanValue != null && booleanValue) {
                                    // Giá trị là true
                                    // TODO: Xử lý khi giá trị là true
                                    holder.mItemSongBinding.layoutItem.setOnClickListener(v -> iOnClickSongItemListener.onClickItemSong(song));

                                    Log.d("history1", "đã vào: ");
                                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                    mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                                        private boolean lichSu = false;
                                        @Override
                                        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                                            if(!lichSu){

                                                FirebaseUser user1 = firebaseAuth.getCurrentUser();
                                                if (user1 != null) {
                                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                    DatabaseReference myRef = database.getReference("history").child(user1.getUid());
                                                    Map<String, Object> data = new HashMap<>();
                                                    data.put("artist", song.getArtist());
                                                    data.put("count", song.getCount());
                                                    data.put("genre", song.getGenre());
                                                    data.put("id", song.getId());
                                                    data.put("image", song.getImage());
                                                    data.put("latest", song.isLatest());
                                                    data.put("title", song.getTitle());
                                                    data.put("url", song.getUrl());
                                                    data.put("timestamp", song.getTimestamp());

                                                    myRef.child(String.valueOf(song.getId())).setValue(data, new DatabaseReference.CompletionListener() {
                                                        @Override
                                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                        }
                                                    });
                                                } else {
                                                    holder.mItemSongBinding.layoutItem.setOnClickListener(v -> iOnClickSongItemListener.onClickItemSong(song));

                                                }
                                                lichSu= true;
                                            }

                                        }
                                    });


                                } else {

                                    // Giá trị là false hoặc null

                                    // TODO: Xử lý khi giá trị là false hoặc null
                                    Toast.makeText(view.getContext(), "Nhạc bản quyền , đăng kí tài khoản vip để nghe", Toast.LENGTH_SHORT).show();

                                }
                            } else {
                                // Nút không tồn tại trong database
                                // TODO: Xử lý khi nút không tồn tại
                                Toast.makeText(view.getContext(), "Lỗi app đang bảo trì", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Xử lý khi có lỗi xảy ra trong quá trình đọc giá trị
                        }
                    });
                }
            });
        }
        Log.d("isVip", " " + song.isVip());


    }

    @Override
    public int getItemCount() {
        return null == mListSongs ? 0 : mListSongs.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {

        private final ItemSongBinding mItemSongBinding;

        public SongViewHolder(ItemSongBinding itemSongBinding) {
            super(itemSongBinding.getRoot());
            this.mItemSongBinding = itemSongBinding;
        }

    }
}