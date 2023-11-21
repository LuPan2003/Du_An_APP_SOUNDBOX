package com.example.soundbox_du_an_md31.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundbox_du_an_md31.Model.Song;
import com.example.soundbox_du_an_md31.databinding.ItemBannerSongBinding;
import com.example.soundbox_du_an_md31.listener.IOnClickSongItemListener;
import com.example.soundbox_du_an_md31.utils.GlideUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class BannerSongAdapter extends RecyclerView.Adapter<BannerSongAdapter.BannerSongViewHolder> {

    private final List<Song> mListSongs;
    public final IOnClickSongItemListener iOnClickSongItemListener;

    public BannerSongAdapter(List<Song> mListSongs, IOnClickSongItemListener iOnClickSongItemListener) {
        this.mListSongs = mListSongs;
        this.iOnClickSongItemListener = iOnClickSongItemListener;
    }

    @NonNull
    @Override
    public BannerSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBannerSongBinding itemBannerSongBinding = ItemBannerSongBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BannerSongViewHolder(itemBannerSongBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerSongViewHolder holder, int position) {
        Song song = mListSongs.get(position);
        if (song == null) {
            return;
        }
        if(song.isCopyrighted() == false){
            holder.mItemBannerSongBinding.imgBannerVip.setVisibility(View.GONE);
            GlideUtils.loadUrlBanner(song.getImage(), holder.mItemBannerSongBinding.imageBanner);
            holder.mItemBannerSongBinding.layoutItem.setOnClickListener(v -> iOnClickSongItemListener.onClickItemSong(song));
        }else{
            holder.mItemBannerSongBinding.imgBannerVip.setVisibility(View.GONE);
            GlideUtils.loadUrlBanner(song.getImage(), holder.mItemBannerSongBinding.imageBanner);
            holder.mItemBannerSongBinding.layoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user == null) {
                        Toast.makeText(view.getContext(), "Nhạc bản quyền , đăng kí tài khoản vip để nghe", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

// Truy cập đến nút (node) cần kiểm tra
                    DatabaseReference booleanRef = databaseRef.child("users/"+user.getUid()+"/isVIP");
                    // Đọc giá trị boolean từ nút đó
                    booleanRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Kiểm tra xem giá trị có tồn tại hay không
                            if (dataSnapshot.exists()) {
                                // Lấy giá trị boolean từ DataSnapshot
                                Boolean booleanValue = dataSnapshot.getValue(Boolean.class);

                                // Kiểm tra giá trị boolean
                                if (booleanValue != null && booleanValue) {
                                    // Giá trị là true
                                    // TODO: Xử lý khi giá trị là true
                                    holder.mItemBannerSongBinding.layoutItem.setOnClickListener(v -> iOnClickSongItemListener.onClickItemSong(song));
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

    }

    @Override
    public int getItemCount() {
        if (mListSongs != null) {
            return mListSongs.size();
        }
        return 0;
    }

    public static class BannerSongViewHolder extends RecyclerView.ViewHolder {

        private final ItemBannerSongBinding mItemBannerSongBinding;

        public BannerSongViewHolder(@NonNull ItemBannerSongBinding itemBannerSongBinding) {
            super(itemBannerSongBinding.getRoot());
            this.mItemBannerSongBinding = itemBannerSongBinding;
        }
    }
}
