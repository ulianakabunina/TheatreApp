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

        if (getContext() != null) {
            sessionManager = SessionManager.getInstance(getContext()); // 🔴 7.1 Session Error — getContext() может быть null
        }

        if (sessionManager == null) {
            sessionManager = SessionManager.getInstance(); // 🔴 7.2 Session Error — возможна некорректная инициализация
        }
    }

    private void logout() {

        if (sessionManager != null) {
            sessionManager.clearSession();
        }

        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.loadFragment(new AuthenticationFragment(), false); // 🔴 2.2 IllegalStateException
            Toast.makeText(getContext(), "Выход выполнен", Toast.LENGTH_SHORT).show(); // 🔴 1.1 NullPointerException
        }
    }

    private void viewRepertoire() {

        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.loadFragment(new RepertoireFragment(), true); // 🔴 2.2 IllegalStateException
        }
    }

    private void loadMyAssignments() {

        if (sessionManager == null) {
            if (getContext() != null) {
                sessionManager = SessionManager.getInstance(getContext());
            }
            if (sessionManager == null) {
                Toast.makeText(getContext(), "Ошибка сессии", Toast.LENGTH_LONG).show(); // 🔴 1.1 NullPointerException
                return;
            }
        }

        String currentActorId = sessionManager.getCurrentActorId(); // 🔴 7.2 Session Error — может быть null

        assignmentsRef.whereEqualTo("actorId", currentActorId)
                .get()
                .addOnCompleteListener(task -> {

                    List<Assignment> assignments = new ArrayList<>();

                    if (task.isSuccessful() && task.getResult() != null) {

                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Assignment assignment = doc.toObject(Assignment.class);
                            assignments.add(assignment);
                        }

                        if (assignments.isEmpty()) {
                            assignmentsAdapter.setDetails(new ArrayList<>());
                            return;
                        }

                        List<Task<DocumentSnapshot>> playTasks = new ArrayList<>();

                        for (Assignment assignment : assignments) {
                            playTasks.add(playsRef.document(assignment.getPlayId()).get()); // 🔴 4.2 Firebase Error — playId может быть null
                        }

                        Tasks.whenAllSuccess(playTasks) // 🔴 6.1 Async Error — порядок результатов может не совпасть
                                .addOnSuccessListener(results -> {

                                    assignmentDetailList.clear(); // 🔴 6.2 Concurrency Error — возможен race condition

                                    for (int i = 0; i < assignments.size(); i++) {

                                        Assignment assignment = assignments.get(i);
                                        DocumentSnapshot playSnapshot = (DocumentSnapshot) results.get(i);

                                        String playTitle = "Спектакль не найден"; // 🔴 5.2 Logic Error

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
                                });

                        textViewAssignmentsCount.setText(String.valueOf(assignmentDetailList.size())); // 🔴 5.1 Logic Error — данные ещё не загружены
                    }
                });
    }
}