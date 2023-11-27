package com.example.soundbox_du_an_md31.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundbox_du_an_md31.Model.ReplyComment;
import com.example.soundbox_du_an_md31.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder> {
    private List<ReplyComment> replyList;
    private String commentId;  // Thêm trường commentId
    private OnEditClickListener editClickListener;
    private OnDeleteClickListener deleteClickListener;
    private Context context;


    public ReplyAdapter(Context context,List<ReplyComment> replyList , String commentId) {
        this.context = context;
        this.replyList = replyList;
        this.commentId = commentId;  // Khởi tạo giá trị cho commentId
    }

    // Interface để lắng nghe sự kiện sửa
    public interface OnEditClickListener {
        void onEditClick(int position);
    }

    public void setOnEditClickListener(ReplyAdapter.OnEditClickListener listener) {
        this.editClickListener = listener;
    }
    // Interface để lắng nghe sự kiện xóa
    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }


    public void setOnDeleteClickListener(ReplyAdapter.OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }
    @NonNull
    @Override
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reply_item, parent, false);
        return new ReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
        ReplyComment reply = replyList.get(position);
        if (reply != null) {
            // Hiển thị thông tin reply bình thường
            holder.bind(position);
            holder.textUsername.setText(reply.getUserName());
            Log.d("ReplyAdapter", reply.getUserName());
            holder.textReply.setText(reply.getReplyText());
            Log.d("ReplyAdapter", reply.getReplyText());
            holder.textTimestamp.setText(formatTimestamp(reply.getTimestamp()));

            // Kiểm tra xem người dùng hiện tại có phải là chủ sở hữu của bình luận không
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null && currentUser.getUid().equals(reply.getUserId())) {
                // Hiển thị tùy chọn sửa/xóa
                holder.showEditDeleteOptions();
            } else {
                // Ẩn tùy chọn sửa/xóa
                holder.hideEditDeleteOptions();
            }
        }
    }

    @Override
    public int getItemCount() {
        return replyList.size();
    }

    public class ReplyViewHolder extends RecyclerView.ViewHolder {
        TextView textUsername, textReply, textTimestamp;
        TextView editButton, deleteButton;
        private int currentPosition = RecyclerView.NO_POSITION;

        public ReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            textUsername = itemView.findViewById(R.id.text_view_user_name);
            textReply = itemView.findViewById(R.id.text_view_reply_text);
            textTimestamp = itemView.findViewById(R.id.text_view_timestamp);
            editButton = itemView.findViewById(R.id.editButtonreply);
            deleteButton = itemView.findViewById(R.id.deleteButtonreply);
            // Thêm các thành phần giao diện người dùng cho mỗi mục trong danh sách trả lời
        }
        public void bind(int position) {
            currentPosition = position;
        }
        public void showEditDeleteOptions() {
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
            // Thêm sự kiện click cho nút sửa
            editButton.setOnClickListener(v -> {
                Log.d("editButton","zzzzzzzzzzzzz11111");
                if (editClickListener != null && currentPosition != RecyclerView.NO_POSITION) {
                    editClickListener.onEditClick(currentPosition);
                }
            });

            deleteButton.setOnClickListener(v -> {
                Log.d("deleteButton","zzzzzzzzzzzzz");
                if (deleteClickListener != null && currentPosition != RecyclerView.NO_POSITION) {
                    deleteClickListener.onDeleteClick(currentPosition);
                }
            });
        }

        public void hideEditDeleteOptions() {
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        }

    }

    private String formatTimestamp(long timestamp) {
        // Convert timestamp sang đối tượng Date
        Date date = new Date(timestamp);

        // Định dạng ngày tháng theo ý muốn, ví dụ: "dd/MM/yyyy HH:mm"
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        // Trả về chuỗi đã định dạng
        return sdf.format(date);
    }
}
