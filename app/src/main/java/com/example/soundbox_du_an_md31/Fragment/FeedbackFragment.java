package com.example.soundbox_du_an_md31.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

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

import java.util.ArrayList;
import java.util.List;


public class FeedbackFragment extends Fragment {
    private FragmentFeedbackBinding mFragmentFeedbackBinding;
    private ContactAdapter mContactAdapter;
    private MainActivity mainActivity;



    @Nullable
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

        mFragmentFeedbackBinding.btndkiSale.setOnClickListener(v -> openNhacVip() );
        //btndkiplus = mFragmentFeedbackBinding.btndkiSale;
//        btndkiplus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               openZalo();
//            }
//        });

        return mFragmentFeedbackBinding.getRoot();

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




    public void openNhacVip(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        LayoutInflater inflater = requireActivity().getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.layout_dialognhacplus, null);
//
//// Tạo WindowManager.LayoutParams mới
//        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT; // R.dimen.dialog_width chứa giá trị 200dp
//        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//
//        builder.setView(dialogView);
//        AlertDialog dialog = builder.create();
//        dialog.show();
//
//// Đặt thuộc tính cửa sổ cho Dialog
//        dialog.getWindow().setAttributes(layoutParams);
        mainActivity.gotoNhacVip();



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContactAdapter.release();
    }
}
