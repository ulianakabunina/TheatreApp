package com.example.theatreapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theatreapp.R;
import com.example.theatreapp.SessionManager;
import com.example.theatreapp.adapters.AssignmentDetailAdapter;
import com.example.theatreapp.models.Assignment;
import com.example.theatreapp.models.AssignmentDetail;
import com.example.theatreapp.models.Play;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ActorHostFragment extends Fragment {

    private TextView textViewWelcome;
    private RecyclerView recyclerViewAssignments;
    private AssignmentDetailAdapter assignmentsAdapter;
    private List<AssignmentDetail> assignmentDetailList;

    private FirebaseFirestore db;
    private CollectionReference assignmentsRef;
    private CollectionReference playsRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        assignmentsRef = db.collection("assignments");
        playsRef = db.collection("plays");

        assignmentDetailList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_actor_host, container, false);

        textViewWelcome = view.findViewById(R.id.text_view_actor_welcome);
        recyclerViewAssignments = view.findViewById(R.id.recycler_view_my_assignments);

        // Устанавливаем приветствие
        String actorName = SessionManager.getInstance().getCurrentActorName();
        textViewWelcome.setText("Добро пожаловать, " + (actorName != null ? actorName : "Актер"));

        // Настраиваем RecyclerView и Адаптер
        recyclerViewAssignments.setLayoutManager(new LinearLayoutManager(getContext()));
        assignmentsAdapter = new AssignmentDetailAdapter(assignmentDetailList);
        recyclerViewAssignments.setAdapter(assignmentsAdapter);

        // TODO: Добавить обработчик для кнопки перехода к FR-10 (Общий репертуар)

        loadMyAssignments(); // Запускаем загрузку связанных данных

        return view;
    }

    // --- ФУНКЦИОНАЛ: FR-09 (Просмотр назначений со связанными данными) ---
    private void loadMyAssignments() {
        String currentActorId = SessionManager.getInstance().getCurrentActorId();

        if (currentActorId == null) {
            Toast.makeText(getContext(), "Ошибка сессии: ID актера не найден.", Toast.LENGTH_LONG).show();
            return;
        }

        // 1. Получаем все назначения для текущего актера
        assignmentsRef.whereEqualTo("actorId", currentActorId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Assignment> assignments = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            assignments.add(doc.toObject(Assignment.class));
                        }

                        if (assignments.isEmpty()) {
                            Toast.makeText(getContext(), "У вас пока нет назначений.", Toast.LENGTH_SHORT).show();
                            assignmentsAdapter.setDetails(new ArrayList<>()); // Очистить список
                            return;
                        }

                        // 2. Создаем список задач для загрузки данных спектаклей
                        List<Task<DocumentSnapshot>> playTasks = new ArrayList<>();
                        for (Assignment assignment : assignments) {
                            // Загружаем документ Play по playId из коллекции "plays"
                            Task<DocumentSnapshot> playTask = playsRef.document(assignment.getPlayId()).get();
                            playTasks.add(playTask);
                        }

                        // 3. Выполняем все задачи одновременно (объединение)
                        Tasks.whenAllSuccess(playTasks)
                                .addOnSuccessListener(results -> {
                                    assignmentDetailList.clear();

                                    for (int i = 0; i < assignments.size(); i++) {
                                        Assignment assignment = assignments.get(i);
                                        DocumentSnapshot playSnapshot = (DocumentSnapshot) results.get(i);

                                        String playTitle = "Спектакль не найден";
                                        if (playSnapshot.exists()) {
                                            Play play = playSnapshot.toObject(Play.class);
                                            if (play != null) {
                                                playTitle = play.getTitle();
                                            }
                                        }

                                        // Создаем объект с полной информацией
                                        AssignmentDetail detail = new AssignmentDetail(
                                                assignment.getRoleName(),
                                                playTitle,
                                                "Основной состав" // Упрощение, т.к. тип не хранится в вашем Assignment
                                        );
                                        assignmentDetailList.add(detail);
                                    }

                                    assignmentsAdapter.setDetails(assignmentDetailList);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Ошибка при объединении данных: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });

                    } else {
                        Toast.makeText(getContext(), "Ошибка загрузки назначений.", Toast.LENGTH_LONG).show();
                    }
                });
    }
}