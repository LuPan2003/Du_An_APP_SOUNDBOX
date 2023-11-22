package com.example.soundbox_du_an_md31.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundbox_du_an_md31.Adapter.CommentAdapter;
import com.example.soundbox_du_an_md31.Model.Comment;
import com.example.soundbox_du_an_md31.Model.Song;
import com.example.soundbox_du_an_md31.R;
import com.example.soundbox_du_an_md31.Service.MusicService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {
    private DatabaseReference commentsRef;
    private EditText commentEditText;
    private Button submitButton;
    private String songId;
    private ImageView tvBack;

    // RecyclerView và Adapter
    private RecyclerView recyclerViewComments;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        commentsRef = FirebaseDatabase.getInstance().getReference().child("comments");
        commentEditText = findViewById(R.id.etComment);
        submitButton = findViewById(R.id.postCommentButton);
        tvBack = findViewById(R.id.icon_back);
        // Khởi tạo RecyclerView và Adapter
        recyclerViewComments = findViewById(R.id.recyclerViewComments);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewComments.setLayoutManager(layoutManager);
        recyclerViewComments.setAdapter(commentAdapter);

        Intent intent = getIntent();
        if (intent != null) {
            songId = intent.getStringExtra("SONG_ID");
            if (songId != null && !songId.isEmpty()) {
                Log.d("CommentActivity", "songId from Intent: " + songId);
            } else {
                Log.e("CommentActivity", "SONG_ID is null or empty");
                finish();
                return;
            }
        } else {
            Log.e("CommentActivity", "Intent is null");
            finish();
            return;
        }
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sử dụng FragmentManager để quay lại Fragment trước đó
                onBackPressed();
            }
        });
        //Sữa bình luận
        submitButton.setOnClickListener(v -> confirmAndPostComment());
        commentAdapter.setOnEditClickListener(position -> {
            Comment comment = commentList.get(position);
            showEditCommentDialog(comment);
        });
        //Xóa bình luận
        commentAdapter.setOnDeleteClickListener(position -> {
            Comment comment = commentList.get(position);
            showDeleteCommentDialog(comment);
        });
        //Trả lời bình luận

        commentAdapter.setOnReplyClickListener(position -> {
            // Kiểm tra đăng nhập
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(this, "Vui lòng đăng nhập để bình luận!", Toast.LENGTH_SHORT).show();
                // Người dùng chưa đăng nhập, chuyển họ đến màn hình đăng nhập/đăng ký
                startActivity(new Intent(CommentActivity.this, LoginActivity.class));
                return;
            }
            // Lấy thông tin bình luận hiện tại
            Comment currentComment = commentList.get(position);

            // Kiểm tra và gán giá trị cho currentSong từ nguồn dữ liệu phù hợp
            Song currentSong = getSongFromSomeSource();
            if (currentSong == null) {
                // Nếu không thể lấy được bài hát, hiển thị thông báo hoặc thực hiện xử lý phù hợp
                return;
            }

            // Tiếp tục với việc mở màn hình trả lời bình luận
            Intent intentReply = new Intent(CommentActivity.this, ReplyActivity.class);
            // Sử dụng phương thức để tạo SONG_ID từ Artist và Title
            String songId = createSongId(currentSong.getArtist(), currentSong.getTitle());
            intentReply.putExtra("SONG_ID", songId);
            Log.d("SONG_ID", "SONG_ID: " + songId);
            intentReply.putExtra("COMMENT_ID", currentComment.getCommentId());
            Log.d("SONG_ID", "COMMENT_ID: " + currentComment.getCommentId());
            intentReply.putExtra("COMMENT_TEXT", currentComment.getCommentText());
            Log.d("SONG_ID", "COMMENT_TEXT: " + currentComment.getCommentText());
            startActivity(intentReply);
        });

        // Load và hiển thị danh sách bình luận
        loadComments();
    }


    // Phương thức tạo SONG_ID từ Artist và Title
    private String createSongId(String artist, String title) {
        // Đơn giản là kết hợp Artist và Title, có thể bạn cần chuẩn hóa chúng trước khi kết hợp
        artist = (artist != null) ? artist : "";
        title = (title != null) ? title : "";
        return artist + "_" + title;
    }

    // Phương thức để lấy đối tượng Song từ nguồn dữ liệu phù hợp
    private Song getSongFromSomeSource() {
        // Thực hiện logic để lấy đối tượng Song từ nguồn dữ liệu, ví dụ: danh sách bài hát
        // Nếu không thể lấy được, trả về null hoặc thực hiện xử lý phù hợp
        return MusicService.mListSongPlaying.get(MusicService.mSongPosition);
    }


    // Hàm hiển thị Dialog sửa bình luận
    private void showEditCommentDialog(Comment comment) {
        Log.d("showEditCommentDialog" ,"zzzzzzzzzzzzzz");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sửa bình luận");

        // Tạo một EditText để người dùng nhập nội dung mới
        EditText input = new EditText(this);
        input.setText(comment.getCommentText());
        builder.setView(input);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String updatedComment = input.getText().toString().trim();
            if (!updatedComment.isEmpty()) {
                // Thực hiện cập nhật bình luận trên Firebase
                updateComment(comment.getCommentId(), updatedComment);
            } else {
                Toast.makeText(this, "Vui lòng nhập nội dung bình luận", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Trong phương thức updateComment
    private void updateComment(String commentId, String updatedComment) {
        commentsRef.child(songId).child(commentId).child("commentText").setValue(updatedComment)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(CommentActivity.this, "Cập nhật bình luận thành công", Toast.LENGTH_SHORT).show();

                    // Cập nhật danh sách bình luận và thông báo cho Adapter
                    loadComments(); // Load lại danh sách bình luận từ Firebase
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CommentActivity.this, "Cập nhật bình luận thất bại", Toast.LENGTH_SHORT).show();
                });
    }

    private void showDeleteCommentDialog(Comment comment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xóa bình luận");
        builder.setMessage("Bạn có chắc muốn xóa bình luận này?");

        builder.setPositiveButton("Xóa", (dialog, which) -> {
            // Thực hiện xóa bình luận trên Firebase
            deleteComment(comment.getCommentId());
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }
    private void deleteComment(String commentId) {
        commentsRef.child(songId).child(commentId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(CommentActivity.this, "Xóa bình luận thành công", Toast.LENGTH_SHORT).show();
                    // Cập nhật danh sách bình luận và thông báo cho Adapter
                    loadComments(); // Load lại danh sách bình luận từ Firebase
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CommentActivity.this, "Xóa bình luận thất bại", Toast.LENGTH_SHORT).show();
                });
    }

    private void confirmAndPostComment() {
        // Xác nhận trước khi đăng bình luận
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc muốn đăng bình luận?")
                .setPositiveButton("Đăng", (dialog, which) -> postComment())
                .setNegativeButton("Hủy", null)
                .show();
    }


    private void postComment() {
        // Kiểm tra đăng nhập
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // Kiểm tra kết nối internet
        if (!isOnline()) {
            Toast.makeText(this, "Không có kết nối internet. Vui lòng kiểm tra lại.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tiếp tục với việc đăng bình luận
        String commentText = commentEditText.getText().toString().trim();

        if (commentText.isEmpty()) {
            return;
        }

        String userId = currentUser.getUid(); // Lấy ID của người dùng
        String userName = currentUser.getDisplayName(); // Lấy tên hiển thị của người dùng
        long timestamp = System.currentTimeMillis(); // Lấy thời gian hiện tại
        String commentKey = commentsRef.child(songId).push().getKey(); // Tạo khóa cho bình luận mới
        Comment comment = new Comment(
                commentKey,
                userId,
                userName,
                songId,
                commentText,
                timestamp
        );
        commentsRef.child(songId).child(commentKey).setValue(comment)
                .addOnSuccessListener(aVoid -> {
                    commentEditText.setText("");
                    Toast.makeText(CommentActivity.this, "Bình luận thành công", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CommentActivity.this, "Gửi bình luận thất bại", Toast.LENGTH_SHORT).show();
                });
    }

    // Phương thức kiểm tra kết nối internet
    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void loadComments() {
        // Xóa danh sách bình luận cũ để tránh trùng lặp khi load lại
        commentList.clear();
        commentAdapter.notifyDataSetChanged();

        // Lắng nghe sự kiện khi có thay đổi trong danh sách bình luận
        commentsRef.child(songId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Comment comment = dataSnapshot.getValue(Comment.class);
                if (comment != null) {
                    commentList.add(comment);
                    commentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                // Xử lý khi có sự thay đổi trong bình luận (nếu cần)
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // Xử lý khi có bình luận bị xóa (nếu cần)
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                // Xử lý khi có sự di chuyển trong danh sách bình luận (nếu cần)
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("CommentActivity", "Error loading comments: " + databaseError.getMessage());
            }
        });
    }
}
