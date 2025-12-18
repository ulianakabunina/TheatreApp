package com.example.theatreapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theatreapp.R;
import com.example.theatreapp.models.AssignmentDetail;

import java.util.List;

public class AssignmentDetailAdapter extends RecyclerView.Adapter<AssignmentDetailAdapter.AssignmentViewHolder> {

    private List<AssignmentDetail> detailList;

    public AssignmentDetailAdapter(List<AssignmentDetail> detailList) {
        this.detailList = detailList;
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_assignment_detail, parent, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        AssignmentDetail detail = detailList.get(position);

        // Роль
        String roleText = detail.getRoleName() != null ? detail.getRoleName() : "Роль не указана";
        holder.textViewRole.setText("👑 " + roleText);

        // Спектакль
        if (detail.getPlayTitle() != null && !detail.getPlayTitle().isEmpty()) {
            holder.textViewPlay.setText("🎭 " + detail.getPlayTitle());
            holder.textViewPlay.setVisibility(View.VISIBLE);
        } else {
            holder.textViewPlay.setVisibility(View.GONE);
        }

        // Тип назначения (если есть)
        if (detail.getAssignmentType() != null && !detail.getAssignmentType().isEmpty()) {
            holder.textViewType.setText("🏷️ " + detail.getAssignmentType());
            holder.textViewType.setVisibility(View.VISIBLE);
        } else {
            holder.textViewType.setVisibility(View.GONE);
        }

        // Актер (если нужно добавить в модель)
        if (detail.getActorName() != null && !detail.getActorName().isEmpty()) {
            holder.textViewActor.setText("🎭 " + detail.getActorName());
            holder.textViewActor.setVisibility(View.VISIBLE);
        } else {
            holder.textViewActor.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return detailList != null ? detailList.size() : 0;
    }

    // Метод для обновления данных
    public void setDetails(List<AssignmentDetail> newDetails) {
        this.detailList = newDetails;
        notifyDataSetChanged();
    }

    static class AssignmentViewHolder extends RecyclerView.ViewHolder {
        TextView textViewRole;
        TextView textViewPlay;
        TextView textViewType;
        TextView textViewActor;

        AssignmentViewHolder(View itemView) {
            super(itemView);
            textViewRole = itemView.findViewById(R.id.text_view_role);
            textViewPlay = itemView.findViewById(R.id.text_view_play);
            textViewType = itemView.findViewById(R.id.text_view_type);
            textViewActor = itemView.findViewById(R.id.text_view_actor);
        }
    }
}