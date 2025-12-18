package com.example.theatreapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.theatreapp.MainActivity;
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

public class ActorHostFragment extends Fragment {
    private static final String TAG = "ActorHostFragment";
    private TextView textViewAssignmentsCount;
    private TextView textViewWelcome;
    private Button btnLogout;
    private Button btnViewRepertoire;
    private RecyclerView recyclerViewAssignments;
    private AssignmentDetailAdapter assignmentsAdapter;
    private List<AssignmentDetail> assignmentDetailList;

    private FirebaseFirestore db;
    private CollectionReference assignmentsRef;
    private CollectionReference playsRef;

    private SessionManager sessionManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        // Инициализируем SessionManager
        if (getContext() != null) {
            sessionManager = SessionManager.getInstance(getContext());
        }

        if (sessionManager == null) {
            Log.e(TAG, "Failed to initialize SessionManager!");
            sessionManager = SessionManager.getInstance();
        }

        if (sessionManager != null) {
            Log.d(TAG, "Session Info - ID: " + sessionManager.getCurrentActorId() +
                    ", Name: " + sessionManager.getCurrentActorName() +
                    ", Role: " + sessionManager.getCurrentActorRole());
        } else {
            Log.e(TAG, "SessionManager is completely null!");
        }

        db = FirebaseFirestore.getInstance();
        assignmentsRef = db.collection("assignments");
        playsRef = db.collection("plays");

        assignmentDetailList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_actor_host, container, false);

        textViewWelcome = view.findViewById(R.id.text_view_actor_welcome);
        btnLogout = view.findViewById(R.id.btn_logout_actor);
        btnViewRepertoire = view.findViewById(R.id.btn_view_repertoire); // Инициализируем
        recyclerViewAssignments = view.findViewById(R.id.recycler_view_my_assignments);

        // Устанавливаем приветствие
        if (sessionManager != null) {
            String actorName = sessionManager.getCurrentActorName();
            Log.d(TAG, "Actor name from session: " + actorName);

            if (actorName != null && !actorName.isEmpty()) {
                textViewWelcome.setText("Добро пожаловать, " + actorName + "!");
            } else {
                textViewWelcome.setText("Добро пожаловать, Актер!");
                Log.w(TAG, "Actor name is null or empty");
            }
        } else {
            textViewWelcome.setText("Добро пожаловать!");
            Log.e(TAG, "SessionManager is null!");
        }

        // Обработчик кнопки выхода
        btnLogout.setOnClickListener(v -> {
            Log.d(TAG, "Logout button clicked");
            logout();
        });

        // Обработчик кнопки просмотра репертуара
        btnViewRepertoire.setOnClickListener(v -> {
            Log.d(TAG, "View repertoire button clicked");
            viewRepertoire();
        });

        // Настраиваем RecyclerView и Адаптер
        recyclerViewAssignments.setLayoutManager(new LinearLayoutManager(getContext()));
        assignmentsAdapter = new AssignmentDetailAdapter(assignmentDetailList);
        recyclerViewAssignments.setAdapter(assignmentsAdapter);

        loadMyAssignments(); // Запускаем загрузку связанных данных

        return view;
    }

    private void logout() {
        Log.d(TAG, "Logging out...");

        // Очищаем сессию
        if (sessionManager != null) {
            sessionManager.clearSession();
            Log.d(TAG, "Session cleared");
        }

        // Переходим к экрану аутентификации
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.loadFragment(new AuthenticationFragment(), false);
            Toast.makeText(getContext(), "Выход выполнен", Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "Activity is not MainActivity");
            Toast.makeText(getContext(), "Ошибка выхода", Toast.LENGTH_SHORT).show();
        }
    }

    private void viewRepertoire() {
        Log.d(TAG, "Opening repertoire fragment");

        // Переходим к фрагменту репертуара
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.loadFragment(new RepertoireFragment(), true);
            Toast.makeText(getContext(), "Загрузка репертуара...", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMyAssignments() {
        Log.d(TAG, "loadMyAssignments called");

        // Проверяем sessionManager
        if (sessionManager == null) {
            if (getContext() != null) {
                sessionManager = SessionManager.getInstance(getContext());
            }
            if (sessionManager == null) {
                Toast.makeText(getContext(), "Ошибка сессии", Toast.LENGTH_LONG).show();
                return;
            }
        }

        String currentActorId = sessionManager.getCurrentActorId();
        Log.d(TAG, "Current actor ID: " + currentActorId);

        if (currentActorId == null || currentActorId.isEmpty()) {
            Toast.makeText(getContext(), "Ошибка сессии: ID актера не найден.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Actor ID is null or empty");
            return;
        }

        // 1. Получаем все назначения для текущего актера
        assignmentsRef.whereEqualTo("actorId", currentActorId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Assignment> assignments = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Assignment assignment = doc.toObject(Assignment.class);
                            assignments.add(assignment);
                            Log.d(TAG, "Found assignment: " + assignment.getRoleName() + " for play: " + assignment.getPlayId());
                        }

                        Log.d(TAG, "Total assignments found: " + assignments.size());

                        if (assignments.isEmpty()) {
                            Toast.makeText(getContext(), "У вас пока нет назначений.", Toast.LENGTH_SHORT).show();
                            assignmentsAdapter.setDetails(new ArrayList<>());
                            return;
                        }

                        // 2. Создаем список задач для загрузки данных спектаклей
                        List<Task<DocumentSnapshot>> playTasks = new ArrayList<>();
                        for (Assignment assignment : assignments) {
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

                                        AssignmentDetail detail = new AssignmentDetail(
                                                assignment.getRoleName(),
                                                playTitle,
                                                "Основной состав"
                                        );
                                        assignmentDetailList.add(detail);
                                    }

                                    assignmentsAdapter.setDetails(assignmentDetailList);
                                    Log.d(TAG, "Assignments loaded: " + assignmentDetailList.size());
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error loading plays: ", e);
                                    Toast.makeText(getContext(), "Ошибка при загрузке спектаклей", Toast.LENGTH_LONG).show();
                                });
                        if (textViewAssignmentsCount != null) {
                            textViewAssignmentsCount.setText(String.valueOf(assignmentDetailList.size()));
                        }

                    } else {
                        if (task.getException() != null) {
                            Log.e(TAG, "Error loading assignments: ", task.getException());
                        }
                        Toast.makeText(getContext(), "Ошибка загрузки назначений", Toast.LENGTH_LONG).show();
                    }
                });
    }
}