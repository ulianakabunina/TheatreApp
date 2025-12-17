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
        holder.tvTitle.setText(play.getTitle());
        holder.tvGenre.setText(play.getGenre());
        holder.tvDirector.setText("Режиссер: " + play.getDirector());
        holder.tvDate.setText("Премьера: " + play.getPremiereDate());
    }

    @Override
    public int getItemCount() { return playList.size(); }

    static class PlayViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvGenre, tvDirector, tvDate;

        public PlayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_play_title);
            tvGenre = itemView.findViewById(R.id.tv_play_genre);
            tvDirector = itemView.findViewById(R.id.tv_play_director);
            tvDate = itemView.findViewById(R.id.tv_play_date);
        }
    }
}