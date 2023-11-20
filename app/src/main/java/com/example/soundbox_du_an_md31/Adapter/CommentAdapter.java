package com.example.soundbox_du_an_md31.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundbox_du_an_md31.Model.Comment;
import com.example.soundbox_du_an_md31.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;
    private Context context;
    private OnEditClickListener editClickListener;
    private OnDeleteClickListener deleteClickListener;

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    public void updateComments(List<Comment> updatedComments) {
        this.commentList.clear();
        this.commentList.addAll(updatedComments);
        notifyDataSetChanged();
    }

    // Interface để lắng nghe sự kiện sửa
    public interface OnEditClickListener {
        void onEditClick(int position);
    }

    public void setOnEditClickListener(OnEditClickListener listener) {
        this.editClickListener = listener;
    }

    // Interface để lắng nghe sự kiện xóa
    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }


    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteClickListener = listener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        if (comment != null) {
            holder.bind(position);
            holder.textUsername.setText(comment.getUserName());
            holder.textComment.setText(comment.getCommentText());
            holder.textTimestamp.setText(formatTimestamp(comment.getTimestamp()));
            // Kiểm tra xem người dùng hiện tại có phải là chủ sở hữu của bình luận không
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null && currentUser.getUid().equals(comment.getUserId())) {
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
        return commentList.size();
    }

    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(timestamp);
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView textUsername, textComment, textTimestamp;
        ImageButton editButton, deleteButton;
        private int currentPosition = RecyclerView.NO_POSITION;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            textUsername = itemView.findViewById(R.id.textUsername);
            textComment = itemView.findViewById(R.id.textComment);
            textTimestamp = itemView.findViewById(R.id.textTimestamp);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(int position) {
            currentPosition = position;
        }

        public void showEditDeleteOptions() {
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
            // Thêm sự kiện click cho nút sửa
            editButton.setOnClickListener(v -> {
                if (editClickListener != null && currentPosition != RecyclerView.NO_POSITION) {
                    editClickListener.onEditClick(currentPosition);
                }
            });
            deleteButton.setOnClickListener(v -> {
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
}
