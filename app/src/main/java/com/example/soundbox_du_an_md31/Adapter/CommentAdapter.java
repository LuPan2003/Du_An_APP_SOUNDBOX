package com.example.soundbox_du_an_md31.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundbox_du_an_md31.Model.Comment;
import com.example.soundbox_du_an_md31.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;
    private Context context;
    private OnEditClickListener editClickListener;
    private OnDeleteClickListener deleteClickListener;
    private OnReplyClickListener replyClickListener;
    private OnHeartClickListener heartClickListener;
    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
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

    // Interface để lắng nghe sự kiện trả lời
    public interface OnReplyClickListener {
        void onReplyClick(int position);
    }
    public void setOnReplyClickListener(OnReplyClickListener listener) {
        this.replyClickListener = listener;
    }

    public interface OnHeartClickListener {
        void onHeartClick(int position);
    }

    public void setOnHeartClickListener(OnHeartClickListener listener) {
        this.heartClickListener = listener;
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
//            avatar
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(comment.getUserId()).child("avatar");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
//                    String base64Image = snapshot.toString();
//                    img_avatarProfile.setImageURI(Uri.parse(base64Image));
                        Log.d("quy1", snapshot.toString());
                        String base64Image = (String) snapshot.getValue();
                        byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                        holder.avatar.setImageBitmap(bitmap);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            // Kiểm tra xem người dùng hiện tại có phải là chủ sở hữu của bình luận không
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            // Hiển thị số lượng tim
            holder.heartCountTextView.setText(String.valueOf(comment.getHeartCount()));

            // Kiểm tra xem người dùng đã thả tim cho bình luận hay chưa
            boolean hasLiked = currentUser != null && comment.getHeartedBy() != null && comment.getHeartedBy().contains(currentUser.getUid());

            // Thiết lập hình ảnh của trái tim dựa vào trạng thái
            int heartDrawableRes = hasLiked ? R.drawable.heart_do : R.drawable.heart;
            holder.imgheartcomment.setImageResource(heartDrawableRes);

            // Thêm sự kiện click cho nút thả tim
            holder.imgheartcomment.setOnClickListener(v -> {
                Log.d("txtHeart", "Đã nhấn");
                if (heartClickListener != null) {
                    heartClickListener.onHeartClick(position);
                }
            });

            if (currentUser != null && currentUser.getUid().equals(comment.getUserId())) {
                // Hiển thị tùy chọn sửa/xóa
                holder.showEditDeleteOptions();
            } else {
                // Ẩn tùy chọn sửa/xóa
                holder.hideEditDeleteOptions();
            }

            holder.txtReply.setOnClickListener(v -> {
                Log.d("txtReply", "đã nhấn");
                if (replyClickListener != null) {
                    replyClickListener.onReplyClick(position);
                }
            });
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
        TextView textUsername, textComment, textTimestamp ,txtReply , heartCountTextView;
        TextView editButton, deleteButton;
        private int currentPosition = RecyclerView.NO_POSITION;
        private ImageView imgheartcomment,avatar;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            textUsername = itemView.findViewById(R.id.textUsername);
            textComment = itemView.findViewById(R.id.textComment);
            textTimestamp = itemView.findViewById(R.id.textTimestamp);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            txtReply = itemView.findViewById(R.id.txtReply);
            imgheartcomment = itemView.findViewById(R.id.img_heart_comment);
            avatar = itemView.findViewById(R.id.icon_profile);
            heartCountTextView= itemView.findViewById(R.id.heartCountTextView);
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
