package com.example.soundbox_du_an_md31.Fragment;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.soundbox_du_an_md31.Activity.MainActivity;
import com.example.soundbox_du_an_md31.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LibraryFragment extends Fragment {
    private MainActivity mainActivity;
    private ImageView profile,imgavatar;
    private TextView soLuongAlbum,tv_favorite;
    private LinearLayout album, favorite;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        mainActivity = (MainActivity) getActivity();
        profile = view.findViewById(R.id.icon_profile);
        album = view.findViewById(R.id.danhsachphat);
        favorite = view.findViewById(R.id.btn_listsongfavorite);
        soLuongAlbum = view.findViewById(R.id.soLuongAlbum);
        tv_favorite = view.findViewById(R.id.tv_favorite);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            soLuongAlbum.setText("0 danh sách");
            tv_favorite.setText("0 bài hát");
        }else{
            Uri photoUrl = user.getPhotoUrl();

            Glide.with(getContext()).load(photoUrl).error(R.drawable.avata).into(profile);
            Log.d("image", "onCreateView: "+ photoUrl);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference musicRef = database.getReference("album");
            DatabaseReference albumRef = musicRef.child(user.getUid());
            DatabaseReference favRef = database.getReference("favoritesongs").child(user.getUid());

            albumRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int count = (int) dataSnapshot.getChildrenCount();
                    // Số lượng phần tử trong bảng "album"
                    soLuongAlbum.setText(count+" danh sách");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xảy ra lỗi trong quá trình truy vấn
                    System.out.println("Lỗi: " + databaseError.getMessage());
                }
            });
            favRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int count = (int) snapshot.getChildrenCount();
                    // Số lượng phần tử trong bảng "album"
                    tv_favorite.setText(count+" bài hát");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("Lỗi: " + error.getMessage());
                }
            });
        }


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.gotoProfile();
            }
        });
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.gotoAlBum();
            }
        });
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.gotoFavoriteSongs();
            }
        });

        return view;
    }

}