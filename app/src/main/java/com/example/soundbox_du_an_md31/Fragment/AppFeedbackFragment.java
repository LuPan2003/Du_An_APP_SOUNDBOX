package com.example.soundbox_du_an_md31.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.soundbox_du_an_md31.Activity.MainActivity;
import com.example.soundbox_du_an_md31.Constant.GlobalFuntion;
import com.example.soundbox_du_an_md31.Model.Feedback;
import com.example.soundbox_du_an_md31.MyApplication;
import com.example.soundbox_du_an_md31.R;
import com.example.soundbox_du_an_md31.databinding.FragmentAppFeedbackBinding;
import com.example.soundbox_du_an_md31.utils.StringUtil;


public class AppFeedbackFragment extends Fragment {

    private FragmentAppFeedbackBinding appFeedbackBinding;
    public static final String TAG = FragmentAppFeedbackBinding.class.getName();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        appFeedbackBinding = FragmentAppFeedbackBinding.inflate(inflater, container, false);
        appFeedbackBinding.tvSendFeedback.setOnClickListener(v -> onClickSendFeedback());
        appFeedbackBinding.iconBack.setOnClickListener(v-> onClickBack());
        return appFeedbackBinding.getRoot();
    }
    private void onClickBack(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        }
    }
    private void onClickSendFeedback() {
        if (getActivity() == null) {
            return;
        }
        MainActivity activity = (MainActivity) getActivity();
        String strName = appFeedbackBinding.edtName.getText().toString();
        String strPhone = appFeedbackBinding.edtPhone.getText().toString();
        String strEmail = appFeedbackBinding.edtEmail.getText().toString();
        String strComment = appFeedbackBinding.edtComment.getText().toString();

        if (StringUtil.isEmpty(strName)) {
            GlobalFuntion.showToastMessage(activity, getString(R.string.name_require));
        } else if (StringUtil.isEmpty(strComment)) {
            GlobalFuntion.showToastMessage(activity, getString(R.string.comment_require));
        } else {
            activity.showProgressDialog(true);
            Feedback feedback = new Feedback(strName, strPhone, strEmail, strComment);
            MyApplication.get(getActivity()).getFeedbackDatabaseReference()
                    .child(String.valueOf(System.currentTimeMillis()))
                    .setValue(feedback, (databaseError, databaseReference) -> {
                        activity.showProgressDialog(false);
                        sendFeedbackSuccess();
                    });
        }
    }
    public void sendFeedbackSuccess() {
        GlobalFuntion.hideSoftKeyboard(getActivity());
        GlobalFuntion.showToastMessage(getActivity(), getString(R.string.msg_send_feedback_success));
        appFeedbackBinding.edtName.setText("");
        appFeedbackBinding.edtPhone.setText("");
        appFeedbackBinding.edtEmail.setText("");
        appFeedbackBinding.edtComment.setText("");
    }
}