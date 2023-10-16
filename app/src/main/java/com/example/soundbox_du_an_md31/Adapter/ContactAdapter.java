package com.example.soundbox_du_an_md31.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundbox_du_an_md31.Constant.GlobalFuntion;
import com.example.soundbox_du_an_md31.Model.Contact;
import com.example.soundbox_du_an_md31.R;
import com.example.soundbox_du_an_md31.databinding.ItemContactBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private Context context;
    private final List<Contact> listContact;
    private final ICallPhone iCallPhone;

    public interface ICallPhone {
        void onClickCallPhone();
    }

    public ContactAdapter(Context context, List<Contact> listContact, ICallPhone iCallPhone) {
        this.context = context;
        this.listContact = listContact;
        this.iCallPhone = iCallPhone;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContactBinding itemContactBinding = ItemContactBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ContactViewHolder(itemContactBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = listContact.get(position);
        if (contact == null) {
            return;
        }
        holder.mItemContactBinding.imgContact.setImageResource(contact.getImage());
        switch (contact.getId()) {
            case Contact.FACEBOOK:
                holder.mItemContactBinding.tvContact.setText(context.getString(R.string.label_facebook));
                break;

            case Contact.HOTLINE:
                holder.mItemContactBinding.tvContact.setText(context.getString(R.string.label_call));
                break;

//            case Contact.GMAIL:
//                holder.mItemContactBinding.tvContact.setText(context.getString(R.string.label_gmail));
//                break;
//
//            case Contact.SKYPE:
//                holder.mItemContactBinding.tvContact.setText(context.getString(R.string.label_skype));
//                break;

            case Contact.YOUTUBE:
                holder.mItemContactBinding.tvContact.setText(context.getString(R.string.label_youtube));
                break;
            case Contact.FEeDBACK:
                holder.mItemContactBinding.tvContact.setText("Feedback");
                break;


//            case Contact.ZALO:
//                holder.mItemContactBinding.tvContact.setText(context.getString(R.string.label_zalo));
//                break;
        }

        holder.mItemContactBinding.layoutItem.setOnClickListener(v -> {
            switch (contact.getId()) {
                case Contact.FACEBOOK:
                    GlobalFuntion.onClickOpenFacebook(context);
                    break;

                case Contact.HOTLINE:
                    iCallPhone.onClickCallPhone();
                    break;

//                case Contact.GMAIL:
//                    GlobalFuntion.onClickOpenGmail(context);
//                    break;
//
//                case Contact.SKYPE:
//                    GlobalFuntion.onClickOpenSkype(context);
//                    break;

                case Contact.YOUTUBE:
                    GlobalFuntion.onClickOpenYoutubeChannel(context);
                    break;

//                case Contact.ZALO:
//                    GlobalFuntion.onClickOpenZalo(context);
//                    break;

                case  Contact.FEeDBACK:

              showFeedbackDialog();

                    break;
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == listContact ? 0 : listContact.size();
    }

    public void release() {
        context = null;
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        private final ItemContactBinding mItemContactBinding;

        public ContactViewHolder(ItemContactBinding itemContactBinding) {
            super(itemContactBinding.getRoot());
            this.mItemContactBinding = itemContactBinding;
        }
    }

    private void showFeedbackDialog() {
        // Tạo dialog
        BottomSheetDialog dialog = new BottomSheetDialog(context);

        // Inflate layout của hộp thoại từ một tệp XML
        View dialogView = LayoutInflater.from(context).inflate(R.layout.layout_feedback, null);

        // Lấy các View trong hộp thoại
        EditText edtFeedback = dialogView.findViewById(R.id.edtFeedback);
        Button btnSubmitFeedback = dialogView.findViewById(R.id.btnSubmitFeedback);

        // Gắn sự kiện cho nút Gửi
        btnSubmitFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String feedbackText = edtFeedback.getText().toString();
                // Xử lý phản hồi ở đây (ví dụ: có thể gửi phản hồi lên máy chủ)
                // Đóng hộp thoại
                dialog.dismiss();
            }
        });

        // Gắn layout và hiển thị hộp thoại
        dialog.setContentView(dialogView);
        dialog.show();
    }


    // Khai báo biến alertDialog
    private AlertDialog alertDialog;
}