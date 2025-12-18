package com.example.theatreapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theatreapp.R;
import com.example.theatreapp.models.Actor;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ActorAdapter extends RecyclerView.Adapter<ActorAdapter.ViewHolder> {

    public interface OnActorClickListener {
        void onDeleteClick(Actor actor, int position);
    }

    private List<Actor> actorList;
    private OnActorClickListener listener;

    public ActorAdapter(List<Actor> actorList) {
        this.actorList = actorList;
    }

    public void setOnActorClickListener(OnActorClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_actor, parent, false); // Используем новый макет
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Actor actor = actorList.get(position);

        holder.textViewName.setText(actor.getName() != null ? actor.getName() : "Без имени");
        holder.textViewId.setText("ID: " + (actor.getId() != null ? actor.getId() : "Не указан"));
        holder.textViewRole.setText("Роль: " + (actor.getRole() != null ? actor.getRole() : "Не указана"));

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(actor, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return actorList.size();
    }

    public void updateList(List<Actor> newList) {
        actorList = newList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewId;
        TextView textViewRole;
        Button btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_name);
            textViewId = itemView.findViewById(R.id.text_view_id);
            textViewRole = itemView.findViewById(R.id.text_view_role);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}