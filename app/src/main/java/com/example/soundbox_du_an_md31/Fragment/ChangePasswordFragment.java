package com.example.soundbox_du_an_md31.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.soundbox_du_an_md31.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ChangePasswordFragment extends Fragment {
    private ImageView icon_back;

    private Button btnUpdate;
    private TextInputEditText emailXacThuc, currentPass, newPassWord, reNewPass;
    public static final String TAG = ChangeInformationFragment.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        emailXacThuc = view.findViewById(R.id.edt_emailXacThuc);
        currentPass = view.findViewById(R.id.edt_oldpass);
        newPassWord = view.findViewById(R.id.edt_newpass);
        btnUpdate = view.findViewById(R.id.btn_update);

        reNewPass = view.findViewById(R.id.edt_renewpass);
        icon_back = view.findViewById(R.id.icon_back);
        icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                }
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                if (user1 == null) {
                    return;
                } else {
                    String newPassWordXT = newPassWord.getText().toString().trim();
                    String reNewPassWordXT = reNewPass.getText().toString().trim();

                    String email = emailXacThuc.getText().toString().trim() + "";
                    String currentPassword = currentPass.getText().toString().trim() + "";
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    if (user1.getEmail().equals(email)) {
                        AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);

                        mAuth.signInWithCredential(credential)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {

                                        if (!newPassWordXT.equals(reNewPassWordXT)) {
                                            Log.d("pass", "onClick: " + newPassWordXT + reNewPassWordXT);
                                            Toast.makeText(getActivity(), "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                                            return;
                                        } else {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            if (user != null) {
                                                user.updatePassword(reNewPassWordXT).addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        Toast.makeText(getActivity(), "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();

                                                    } else {
                                                        Toast.makeText(getActivity(), "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();

                                                    }
                                                });

                                            }
                                        }
                                        // Người dùng đã xác thực thành công
                                        // Bạn có thể chuyển đến bước tiếp theo để thay đổi mật khẩu
                                    } else {
                                        Toast.makeText(getActivity(), "Xác thực thất bại , email hoặc mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show();
                                        return;
                                        // Xác thực thất bại
                                        // Xử lý lỗi tại đây
                                    }
                                });
                    }else{
                        Toast.makeText(getActivity(), "Nhập đúng email bạn đang sử dụng trong app", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }

            }
        });


        return view;
    }
}