package com.example.theatreapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theatreapp.R;
import com.example.theatreapp.models.Assignment;

import java.util.List;

public class ViewAssignmentAdapter extends RecyclerView.Adapter<ViewAssignmentAdapter.ViewHolder> {

    public interface OnAssignmentClickListener {
        void onDeleteClick(Assignment assignment, int position);
    }

    private List<Assignment> assignmentList;
    private OnAssignmentClickListener listener;

    public ViewAssignmentAdapter(List<Assignment> assignmentList, OnAssignmentClickListener listener) {
        this.assignmentList = assignmentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_assignment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Assignment assignment = assignmentList.get(position);

        // Роль
        holder.textViewRole.setText("Роль: " +
                (assignment.getRoleName() != null ? assignment.getRoleName() : "Не указана"));

        // Актер (имя или ID)
        String actorInfo;
        if (assignment.getActorName() != null && !assignment.getActorName().isEmpty()) {
            actorInfo = assignment.getActorName();
        } else if (assignment.getActorId() != null && !assignment.getActorId().isEmpty()) {
            actorInfo = "Актер ID: " + assignment.getActorId();
        } else {
            actorInfo = "Актер не указан";
        }
        holder.textViewActor.setText("Актер: " + actorInfo);

        // Спектакль (название или ID)
        String playInfo;
        if (assignment.getPlayTitle() != null && !assignment.getPlayTitle().isEmpty()) {
            playInfo = assignment.getPlayTitle();
        } else if (assignment.getPlayId() != null && !assignment.getPlayId().isEmpty()) {
            playInfo = "Спектакль ID: " + assignment.getPlayId();
        } else {
            playInfo = "Спектакль не указан";
        }
        holder.textViewPlay.setText("Спектакль: " + playInfo);

        // Кнопка удаления
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(assignment, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    public void updateList(List<Assignment> newList) {
        assignmentList = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewRole;
        TextView textViewActor;
        TextView textViewPlay;
        Button btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewRole = itemView.findViewById(R.id.text_view_role);
            textViewActor = itemView.findViewById(R.id.text_view_actor);
            textViewPlay = itemView.findViewById(R.id.text_view_play);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}