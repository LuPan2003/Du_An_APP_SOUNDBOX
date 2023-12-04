package com.example.soundbox_du_an_md31.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.soundbox_du_an_md31.Activity.MainActivity;
import com.example.soundbox_du_an_md31.Constant.GlobalFuntion;
import com.example.soundbox_du_an_md31.Model.Feedback;
import com.example.soundbox_du_an_md31.MyApplication;
import com.example.soundbox_du_an_md31.R;
import com.example.soundbox_du_an_md31.databinding.FragmentAppFeedbackBinding;
import com.example.soundbox_du_an_md31.utils.StringUtil;

import java.util.Calendar;


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

            // Thêm feedback với reply mặc định là false và timestamp là thời điểm hiện tại
            long timestamp = System.currentTimeMillis();
            Feedback feedback = new Feedback(strName, strPhone, strEmail, strComment, timestamp, false, 0, 0);

            // Lấy thông tin về tháng và năm từ timestamp
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);
            int month = calendar.get(Calendar.MONTH) + 1; // Vì tháng trong Calendar là 0-indexed
            int year = calendar.get(Calendar.YEAR);

            // Thiết lập giá trị cho trường tháng và năm
            feedback.setMonth(month);
            feedback.setYear(year);

            // Lưu ý dòng code sau để đảm bảo trường reply được thiết lập là false
            feedback.setReply(false);

            MyApplication.get(getActivity()).getFeedbackDatabaseReference()
                    .child(String.valueOf(timestamp))
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
        // Hiển thị thông báo cảm ơn bằng Toast
        // Hiển thị thông báo cảm ơn bằng AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.thank_you_feedback)
                .setPositiveButton("OK", (dialog, id) -> {
                    // Xử lý khi người dùng nhấn nút OK (nếu cần)
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}