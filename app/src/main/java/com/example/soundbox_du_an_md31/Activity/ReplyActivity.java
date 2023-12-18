package com.example.soundbox_du_an_md31.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundbox_du_an_md31.Adapter.ReplyAdapter;
import com.example.soundbox_du_an_md31.Model.ReplyComment;
import com.example.soundbox_du_an_md31.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ReplyActivity extends AppCompatActivity {
    private DatabaseReference commentsReply;
    private String songId;
    private String commentId;
    private String commentText;
    private EditText editTextReply;
    private Button buttonPostReply;
    private RecyclerView recyclerViewReplies;
    private ReplyAdapter replyAdapter;
    private List<ReplyComment> replyList; // Danh sách trả lời
    private ImageView icon_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        // Ánh xạ các thành phần UI
        editTextReply = findViewById(R.id.edit_text_reply);
        buttonPostReply = findViewById(R.id.button_post_reply);
        icon_back = findViewById(R.id.icon_back);
        recyclerViewReplies = findViewById(R.id.recycler_view_comments);
        // Lấy dữ liệu từ Intent
        songId = getIntent().getStringExtra("SONG_ID");
        commentId = getIntent().getStringExtra("COMMENT_ID");
        commentText = getIntent().getStringExtra("COMMENT_TEXT");
        commentsReply = FirebaseDatabase.getInstance().getReference().child("commentreply");
        // Khởi tạo Adapter và RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        replyList = new ArrayList<>();
        replyAdapter = new ReplyAdapter(this, replyList, commentId);
        recyclerViewReplies.setLayoutManager(layoutManager);
        recyclerViewReplies.setAdapter(replyAdapter);

        buttonPostReply.setOnClickListener(v -> postReply());
        icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        replyAdapter.setOnEditClickListener(position -> {
            Log.d("replyAdapter", "zzzzzzzzzzzzzz");
            ReplyComment replyComment = replyList.get(position);
            showEditCommentDialog(replyComment);
        });
        //Xóa bình luận
        replyAdapter.setOnDeleteClickListener(position -> {
            ReplyComment replyComment = replyList.get(position);
            showDeleteCommentDialog(replyComment);
        });
        loadRepliesForComment(commentId); // Load trả lời cho bình luận đã chọn
    }

    private void showEditCommentDialog(ReplyComment replyComment) {
        Log.d("showEditCommentDialog", "zzzzzzzzzzzzzz");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa bình luận");

        // Tạo một EditText để người dùng nhập nội dung mới
        EditText input = new EditText(this);
        input.setText(replyComment.getReplyText());
        builder.setView(input);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String updatedComment = input.getText().toString().trim();
            if (!updatedComment.isEmpty()) {
                // Thực hiện cập nhật bình luận trên Firebase
                updateComment(replyComment.getReplyId(), updatedComment);
            } else {
                Toast.makeText(this, "Vui lòng nhập nội dung bình luận", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Trong phương thức updateComment
    private void updateComment(String replyId, String updatedComment) {
        commentsReply.child(songId).child(replyId).child("replyText").setValue(updatedComment)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ReplyActivity.this, "Cập nhật bình luận thành công", Toast.LENGTH_SHORT).show();
                    // Cập nhật chỉ mục thay đổi trong danh sách và thông báo cho Adapter
                    int index = findIndexOfReply(replyId);
                    if (index != -1) {
                        replyList.get(index).setReplyText(updatedComment);
                        replyAdapter.notifyItemChanged(index);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ReplyActivity.this, "Cập nhật bình luận thất bại", Toast.LENGTH_SHORT).show();
                });
    }

    private void showDeleteCommentDialog(ReplyComment replyComment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xóa bình luận");
        builder.setMessage("Bạn có chắc muốn xóa bình luận này?");

        builder.setPositiveButton("Xóa", (dialog, which) -> {
            // Thực hiện xóa bình luận trên Firebase
            deleteComment(replyComment.getReplyId());
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void deleteComment(String replyId) {
        commentsReply.child(songId).child(replyId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ReplyActivity.this, "Xóa bình luận thành công", Toast.LENGTH_SHORT).show();
                    // Cập nhật chỉ mục thay đổi trong danh sách và thông báo cho Adapter
                    int index = findIndexOfReply(replyId);
                    if (index != -1) {
                        replyList.remove(index);
                        replyAdapter.notifyItemRemoved(index);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ReplyActivity.this, "Xóa bình luận thất bại", Toast.LENGTH_SHORT).show();
                });
    }

    private void postReply() {
        // Bước 1: Kiểm tra xem văn bản trả lời có trống không
        String replyText = editTextReply.getText().toString().trim();
        if (replyText.isEmpty()) {
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid(); // Lấy ID của người dùng
            String userName = currentUser.getDisplayName(); // Lấy tên hiển thị của người dùng
            long timestamp = System.currentTimeMillis(); // Lấy thời gian hiện tại
            String commentKey = commentsReply.child(songId).push().getKey(); // Tạo khóa cho bình luận mới

            // Đảo vị trí của userId và userName trong đối tượng ReplyComment
            ReplyComment replyComment = new ReplyComment(
                    commentKey, // Bạn cần tạo một ID duy nhất cho phần trả lời
                    commentId, // Thay thế bằng ID bình luận thực tế
                    userId, // Thay thế bằng ID người dùng thực tế
                    userName, // Thay thế bằng tên người dùng thực tế
                    commentText, // Nội dung của bình luận đang được trả lời
                    replyText, // Nội dung của trả lời mới
                    timestamp
            );

            commentsReply.child(songId).child(commentKey).setValue(replyComment)
                    .addOnSuccessListener(aVoid -> {
                        editTextReply.setText("");
                        Toast.makeText(ReplyActivity.this, "Bình luận thành công", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ReplyActivity.this, "Gửi bình luận thất bại", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Người dùng chưa đăng nhập, xử lý tương ứng
            Toast.makeText(ReplyActivity.this, "Bạn cần đăng nhập để bình luận", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadRepliesForComment(String commentId) {
        // Xóa danh sách trả lời cũ để tránh trùng lặp khi load lại
        replyList.clear();
        replyAdapter.notifyDataSetChanged();

        // Lắng nghe sự kiện khi có thay đổi trong danh sách trả lời
        commentsReply.child(songId).orderByChild("commentId").equalTo(commentId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                ReplyComment replyComment = dataSnapshot.getValue(ReplyComment.class);
                if (replyComment != null) {
                    replyList.add(replyComment);
                    replyAdapter.notifyItemInserted(replyList.size() - 1);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                ReplyComment updatedReply = dataSnapshot.getValue(ReplyComment.class);
                if (updatedReply != null) {
                    // Xử lý khi có sự thay đổi trong bình luận (nếu cần)
                    int index = findIndexOfReply(updatedReply.getReplyId());
                    if (index != -1) {
                        replyList.set(index, updatedReply);
                        replyAdapter.notifyItemChanged(index);
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                ReplyComment deletedReply = dataSnapshot.getValue(ReplyComment.class);
                if (deletedReply != null) {
                    // Xử lý khi có bình luận bị xóa (nếu cần)
                    int index = findIndexOfReply(deletedReply.getReplyId());
                    if (index != -1) {
                        replyList.remove(index);
                        replyAdapter.notifyItemRemoved(index);
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                // Xử lý khi có sự di chuyển trong danh sách trả lời (nếu cần)
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("CommentActivity", "Error loading replies: " + databaseError.getMessage());
            }
        });
    }
    private int findIndexOfReply(String replyId) {
        for (int i = 0; i < replyList.size(); i++) {
            if (replyList.get(i).getReplyId().equals(replyId)) {
                return i;
            }
        }
        return -1;
    }
}

