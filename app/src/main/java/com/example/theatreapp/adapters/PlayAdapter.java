package com.example.theatreapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theatreapp.R;
import com.example.theatreapp.models.Play;

import java.util.List;

public class PlayAdapter extends RecyclerView.Adapter<PlayAdapter.PlayViewHolder> {

    private List<Play> playList;

    public PlayAdapter(List<Play> playList) {
        this.playList = playList;
    }

    @NonNull
    @Override
    public PlayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_play, parent, false);
        return new PlayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayViewHolder holder, int position) {
        Play play = playList.get(position);

        // Заголовок спектакля
        holder.tvTitle.setText(play.getTitle() != null ? play.getTitle() : "Без названия");

        // Жанр
        if (play.getGenre() != null && !play.getGenre().isEmpty()) {
            holder.tvGenre.setText(play.getGenre());
            holder.tvGenre.setVisibility(View.VISIBLE);
        } else {
            holder.tvGenre.setVisibility(View.GONE);
        }

        // Режиссер и автор
        StringBuilder directorInfo = new StringBuilder();
        if (play.getDirector() != null && !play.getDirector().isEmpty()) {
            directorInfo.append(play.getDirector());
        }
        if (play.getAuthor() != null && !play.getAuthor().isEmpty()) {
            if (directorInfo.length() > 0) {
                directorInfo.append(" • ");
            }
            directorInfo.append(play.getAuthor());
        }

        if (directorInfo.length() > 0) {
            holder.tvDirector.setText(directorInfo.toString());
            holder.tvDirector.setVisibility(View.VISIBLE);
        } else {
            holder.tvDirector.setVisibility(View.GONE);
        }

        // Дата премьеры
        if (play.getPremiereDate() != null && !play.getPremiereDate().isEmpty()) {
            holder.tvDate.setText("📅 " + play.getPremiereDate());
            holder.tvDate.setVisibility(View.VISIBLE);
        } else {
            holder.tvDate.setVisibility(View.GONE);
        }

        // Статус (опционально, если хотите отображать)
        if (play.getStatus() != null && !play.getStatus().isEmpty()) {
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setText(getStatusEmoji(play.getStatus()) + " " + play.getStatus());
        } else {
            holder.tvStatus.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return playList != null ? playList.size() : 0;
    }

    public void setPlays(List<Play> plays) {
        this.playList = plays;
        notifyDataSetChanged();
    }

    // Метод для получения эмодзи по статусу
    private String getStatusEmoji(String status) {
        if (status == null) return "📋";
        switch (status.toLowerCase()) {
            case "в репертуаре":
            case "активен":
                return "✅";
            case "завершен":
            case "завершён":
                return "🎬";
            case "премьера":
                return "🎉";
            case "планируется":
                return "📅";
            default:
                return "📋";
        }
    }

    static class PlayViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvGenre, tvDirector, tvDate, tvStatus;

        public PlayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_play_title);
            tvGenre = itemView.findViewById(R.id.tv_play_genre);
            tvDirector = itemView.findViewById(R.id.tv_play_director);
            tvDate = itemView.findViewById(R.id.tv_play_date);
            tvStatus = itemView.findViewById(R.id.tv_play_status);
        }
    }
}