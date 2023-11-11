package com.example.soundbox_du_an_md31.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.soundbox_du_an_md31.Activity.PlayMusicActivity;
import com.example.soundbox_du_an_md31.Adapter.SongAdapter;
import com.example.soundbox_du_an_md31.Constant.Constant;
import com.example.soundbox_du_an_md31.Constant.GlobalFuntion;
import com.example.soundbox_du_an_md31.Model.Song;
import com.example.soundbox_du_an_md31.R;
import com.example.soundbox_du_an_md31.Service.MusicService;
import com.example.soundbox_du_an_md31.databinding.FragmentAlbumBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class ListSongFavoriteFragment extends Fragment {
    private ImageView icon_back;

    private RecyclerView rcv_songfavorite;

    private List<Song> mListSong;

    private SongAdapter adapter;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    public static final String TAG = ListSongFavoriteFragment.class.getName();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_list_song_favorite, container, false);
        initUI(view);
        rcv_songfavorite.setHasFixedSize(true);
        rcv_songfavorite.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
        rcv_songfavorite.addItemDecoration(dividerItemDecoration);
        mListSong = new ArrayList<>();
        adapter = new SongAdapter(mListSong,this::goToSongDetail);
        rcv_songfavorite.setAdapter(adapter);
        getMusicFavorite();

        icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                }
            }
        });
        return view;
    }

    private void getMusicFavorite() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Log.d("zzzz", mAuth.getUid());
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    reference = database.getReference("favoritesongs").child(user.getUid());
                    reference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            Song song = snapshot.getValue(Song.class);
                            if(song != null){
                                mListSong.add(song);
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            Song song = snapshot.getValue(Song.class);
                            if(song == null || mListSong == null || mListSong.isEmpty()){
                                return;
                            }

                            for (int i = 0 ; i< mListSong.size();i++){
                                if(song.getId() == mListSong.get(i).getId()){
                                    mListSong.set(i,song);
                                    break;
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                            Song song = snapshot.getValue(Song.class);
                            if(song == null || mListSong == null || mListSong.isEmpty()){
                                return;
                            }
                            for (int i = 0 ; i< mListSong.size();i++){
                                if(song.getId() == mListSong.get(i).getId()){
                                    mListSong.remove(mListSong.get(i));
                                    break;
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getActivity(), "Lỗi", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Bạn chưa login", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initUI(View view){
        icon_back = view.findViewById(R.id.icon_back);
        rcv_songfavorite = view.findViewById(R.id.rcv_favoritesongs);
    }

    private void goToSongDetail(@NonNull Song song) {
        MusicService.clearListSongPlaying();
        MusicService.mListSongPlaying.add(song);
        MusicService.isPlaying = false;
        GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
        GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
    }
}