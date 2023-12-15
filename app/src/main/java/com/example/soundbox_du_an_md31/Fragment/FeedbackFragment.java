package com.example.soundbox_du_an_md31.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.soundbox_du_an_md31.Activity.MainActivity;
import com.example.soundbox_du_an_md31.Activity.SoundSettingsActivity;
import com.example.soundbox_du_an_md31.Adapter.ContactAdapter;
import com.example.soundbox_du_an_md31.Constant.GlobalFuntion;
import com.example.soundbox_du_an_md31.Model.Contact;
import com.example.soundbox_du_an_md31.R;
import com.example.soundbox_du_an_md31.databinding.FragmentFeedbackBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class FeedbackFragment extends Fragment {
    private FragmentFeedbackBinding mFragmentFeedbackBinding;
    private ContactAdapter mContactAdapter;
    private MainActivity mainActivity;
    InterstitialAd mInterstitialAd;
    private AdView mAdView;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentFeedbackBinding = FragmentFeedbackBinding.inflate(inflater, container, false);
        mainActivity = (MainActivity) getActivity();
        mFragmentFeedbackBinding.btnGotoFeedback.setOnClickListener(v -> onClickSendFeedback());
        mContactAdapter = new ContactAdapter(getActivity(), getListContact(), () -> GlobalFuntion.callPhoneNumber(getActivity()));
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        mFragmentFeedbackBinding.rcvData.setNestedScrollingEnabled(false);
        mFragmentFeedbackBinding.rcvData.setFocusable(false);
        mFragmentFeedbackBinding.rcvData.setLayoutManager(layoutManager);
        mFragmentFeedbackBinding.rcvData.setAdapter(mContactAdapter);

        mFragmentFeedbackBinding.gotoPremium.setOnClickListener(v -> openPremium());
        mFragmentFeedbackBinding.backgrPay.setOnClickListener(v -> openQuangCao());
//        mFragmentFeedbackBinding.btnCharts.setOnClickListener(v -> openCharts());
        return mFragmentFeedbackBinding.getRoot();
    }

    private void openCharts() {
//        Intent intent = new Intent(getActivity() , RankingActivity.class);
//        startActivity(intent);
    }


    private void openQuangCao() {
        // Khởi tạo AdMob
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        // Hiển thị quảng cáo toàn màn hình
        mAdView = mFragmentFeedbackBinding.adView;
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        InterstitialAd.load(getActivity(), "ca-app-pub-8801498166910444/9063512975", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        if (mInterstitialAd != null) {
                            mInterstitialAd.show(getActivity());
                            // Tăng điểm khi quảng cáo được xem thêm
                            increasePointsForAdView();
                        } else {
                            Log.d("TAG", "Quảng cáo toàn màn hình chưa sẵn sàng.");
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Xử lý lỗi
                        mInterstitialAd = null;
                    }
                });
    }
    private void increasePointsForAdView() {
        // Lấy UserId của người dùng đăng nhập
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = (user != null) ? user.getUid() : null;

        if (userId != null) {
            // Lấy điểm hiện tại từ bảng rankings
            DatabaseReference rankingRef = FirebaseDatabase.getInstance().getReference("rankings").child(userId);

            rankingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Người dùng có trong bảng rankings
                        int currentPoints = dataSnapshot.child("point").getValue(Integer.class);

                        // Tăng điểm lên 10 đơn vị
                        int newPoints = currentPoints + 10;

                        // Cập nhật giá trị mới vào Firebase
                        rankingRef.child("point").setValue(newPoints);

                        // Hiển thị thông báo hoặc cập nhật UI nếu cần
                        showPointsIncreasedMessage();
                    } else {
                        // Người dùng không có trong bảng rankings
                        // Xử lý theo ý của bạn, có thể hiển thị một thông báo hoặc ẩn RecyclerView
                        showNoUserInRankingsMessage();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Xử lý lỗi nếu cần
                }
            });
        }
    }

    private void showPointsIncreasedMessage() {
        Toast.makeText(getActivity(), "Điểm của bạn đã tăng lên 10 đơn vị sau khi xem thêm quảng cáo.", Toast.LENGTH_SHORT).show();
    }

    private void showNoUserInRankingsMessage() {
        Toast.makeText(getActivity(), "Vui lòng nghe nhạc hoặc xem quảng cáo để đủ điểm", Toast.LENGTH_SHORT).show();
    }


    private void settingSound() {
        Intent intent = new Intent(getActivity() , SoundSettingsActivity.class);
        startActivity(intent);
    }

    private void onClickSendFeedback() {
        mainActivity.gotoFeedback();
    }
    public List<Contact> getListContact() {
        List<Contact> contactArrayList = new ArrayList<>();
        contactArrayList.add(new Contact(Contact.FACEBOOK, R.drawable.ic_facebook));
        contactArrayList.add(new Contact(Contact.HOTLINE, R.drawable.ic_hotline));
        contactArrayList.add(new Contact(Contact.YOUTUBE, R.drawable.ic_youtube));
        return contactArrayList;
    }




    public void openPremium(){
        mainActivity.gotoPremium();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContactAdapter.release();
    }
}
