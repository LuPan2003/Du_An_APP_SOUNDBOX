package com.example.soundbox_du_an_md31.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundbox_du_an_md31.Model.RankingItem;
import com.example.soundbox_du_an_md31.R;
public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {
    private RankingItem userRankingItem;

    // Constructor để chỉ lấy RankingItem của người dùng hiện tại
    public RankingAdapter(RankingItem userRankingItem) {
        this.userRankingItem = userRankingItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo ViewHolder từ layout item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Gọi hàm bind để hiển thị thông tin của người dùng hiện tại
        holder.bind(userRankingItem);
    }

    @Override
    public int getItemCount() {
        // Luôn trả về 1 vì chỉ có thông tin của người dùng hiện tại
        return 1;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUserName;
        private TextView tvUserEmail;
        private TextView tvUserPoints;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            tvUserName = itemView.findViewById(R.id.tv_user_name);
//            tvUserEmail = itemView.findViewById(R.id.tv_user_email);
            tvUserPoints = itemView.findViewById(R.id.tv_user_points);
        }

        public void bind(RankingItem rankingItem) {
//            tvUserName.setText("Tài khoản: "+ rankingItem.getUserName());
//            tvUserEmail.setText("Email: "+rankingItem.getUserEmail());
            tvUserPoints.setText(String.valueOf(rankingItem.getUserPoints()) +" Điểm");
        }
    }
}

