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
        // Используем простую разметку для строки списка (нужно будет создать!)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_assignment_detail, parent, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        AssignmentDetail detail = detailList.get(position);

        // Отображение: Название роли (Название спектакля)
        holder.textViewRole.setText(detail.getRoleName());
        holder.textViewPlay.setText("в спектакле: " + detail.getPlayTitle());
        // Отображение типа, если нужно
        // holder.textViewType.setText(detail.getAssignmentType());
    }

    @Override
    public int getItemCount() {
        return detailList.size();
    }

    // Метод для обновления данных
    public void setDetails(List<AssignmentDetail> newDetails) {
        this.detailList = newDetails;
        notifyDataSetChanged();
    }

    static class AssignmentViewHolder extends RecyclerView.ViewHolder {
        TextView textViewRole;
        TextView textViewPlay;
        // TextView textViewType; // Если будем использовать

        AssignmentViewHolder(View itemView) {
            super(itemView);
            textViewRole = itemView.findViewById(R.id.text_role_name);
            textViewPlay = itemView.findViewById(R.id.text_play_title);
            // textViewType = itemView.findViewById(R.id.text_assignment_type);
        }
    }
}