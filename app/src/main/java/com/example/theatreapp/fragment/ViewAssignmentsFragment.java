package com.example.theatreapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theatreapp.R;
import com.example.theatreapp.adapters.ViewAssignmentAdapter;
import com.example.theatreapp.models.Assignment;
import com.example.theatreapp.models.Actor;
import com.example.theatreapp.models.Play;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewAssignmentsFragment extends Fragment {
    private static final String TAG = "ViewAssignments";

    private RecyclerView recyclerViewAssignments;
    private ViewAssignmentAdapter assignmentsAdapter;
    private List<Assignment> assignmentList;
    private FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        assignmentList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_assignments, container, false);

        recyclerViewAssignments = view.findViewById(R.id.recycler_view_assignments);

        // Настраиваем адаптер
        assignmentsAdapter = new ViewAssignmentAdapter(assignmentList, new ViewAssignmentAdapter.OnAssignmentClickListener() {
            @Override
            public void onDeleteClick(Assignment assignment, int position) {
                deleteAssignment(assignment, position);
            }
        });

        recyclerViewAssignments.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAssignments.setAdapter(assignmentsAdapter);

        loadAllAssignments();

        // Кнопка назад
        Button btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    private void loadAllAssignments() {
        db.collection("assignments").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        assignmentList.clear();

                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Assignment assignment = doc.toObject(Assignment.class);
                            // Сохраняем ID документа из Firestore
                            assignment.setDocumentId(doc.getId());
                            // Если assignmentId пустой, используем documentId
                            if (assignment.getAssignmentId() == null || assignment.getAssignmentId().isEmpty()) {
                                assignment.setAssignmentId(doc.getId());
                            }
                            assignmentList.add(assignment);
                        }

                        // Загружаем дополнительную информацию
                        loadAdditionalDetails();

                    } else {
                        Toast.makeText(getContext(), "Ошибка загрузки назначений", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error loading assignments", task.getException());
                    }
                });
    }

    private void loadAdditionalDetails() {
        for (final Assignment assignment : assignmentList) {
            // Загружаем имя актера
            if (assignment.getActorId() != null && !assignment.getActorId().isEmpty()) {
                db.collection("users").document(assignment.getActorId())
                        .get()
                        .addOnSuccessListener(actorDoc -> {
                            if (actorDoc.exists()) {
                                Actor actor = actorDoc.toObject(Actor.class);
                                if (actor != null) {
                                    assignment.setActorName(actor.getName());
                                    assignmentsAdapter.notifyDataSetChanged();
                                }
                            }
                        });
            }

            // Загружаем название спектакля
            if (assignment.getPlayId() != null && !assignment.getPlayId().isEmpty()) {
                db.collection("plays").document(assignment.getPlayId())
                        .get()
                        .addOnSuccessListener(playDoc -> {
                            if (playDoc.exists()) {
                                Play play = playDoc.toObject(Play.class);
                                if (play != null) {
                                    assignment.setPlayTitle(play.getTitle());
                                    assignmentsAdapter.notifyDataSetChanged();
                                }
                            }
                        });
            }
        }
    }

    private void deleteAssignment(Assignment assignment, int position) {
        // Используем documentId для удаления (это ID документа в Firestore)
        String docIdToDelete = assignment.getDocumentId() != null ?
                assignment.getDocumentId() : assignment.getAssignmentId();

        if (docIdToDelete == null || docIdToDelete.isEmpty()) {
            Toast.makeText(getContext(), "Ошибка: ID назначения не найден", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("assignments").document(docIdToDelete)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Удаляем из списка
                    assignmentList.remove(position);
                    assignmentsAdapter.notifyItemRemoved(position);
                    Toast.makeText(getContext(), "Назначение удалено", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Assignment deleted: " + docIdToDelete);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Ошибка удаления: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error deleting assignment", e);
                });
    }
}