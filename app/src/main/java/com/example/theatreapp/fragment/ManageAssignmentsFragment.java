package com.example.theatreapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.theatreapp.R;
import com.example.theatreapp.models.Actor;
import com.example.theatreapp.models.Assignment;
import com.example.theatreapp.models.Play;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManageAssignmentsFragment extends Fragment {

    private Spinner spinnerPlays, spinnerActors;
    private EditText editTextRoleName;
    private Button buttonAssign;

    private FirebaseFirestore db;
    private CollectionReference playsRef, actorsRef, assignmentsRef;

    // Списки для хранения данных
    private List<Play> playList;
    private List<Actor> actorList;
    private List<String> playTitles;
    private List<String> actorNames;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        playsRef = db.collection("plays");
        actorsRef = db.collection("users");
        assignmentsRef = db.collection("assignments");

        playList = new ArrayList<>();
        actorList = new ArrayList<>();
        playTitles = new ArrayList<>();
        actorNames = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_assignments, container, false);

        spinnerPlays = view.findViewById(R.id.spinner_plays);
        spinnerActors = view.findViewById(R.id.spinner_actors);
        editTextRoleName = view.findViewById(R.id.edit_text_role_name);
        buttonAssign = view.findViewById(R.id.button_assign_actor);

        loadPlays();
        loadActors();

        buttonAssign.setOnClickListener(v -> createAssignment());

        return view;
    }

    // --- Загрузка Спектаклей (Plays) из Firestore ---
    private void loadPlays() {
        playsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                playList.clear();
                playTitles.clear();

                playTitles.add("Выберите Спектакль");
                playList.add(null);

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Play play = document.toObject(Play.class);
                    play.setPlayId(document.getId());
                    playList.add(play);
                    playTitles.add(play.getTitle());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        getContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        playTitles);
                spinnerPlays.setAdapter(adapter);

            } else {
                Toast.makeText(getContext(), "Ошибка загрузки спектаклей: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // --- Загрузка Актеров (Actors) из Firestore ---
    private void loadActors() {
        // Фильтруем по роли "actor"
        actorsRef.whereEqualTo("role", "actor").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                actorList.clear();
                actorNames.clear();

                actorNames.add("Выберите Актера");
                actorList.add(null);

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Actor actor = document.toObject(Actor.class);
                    actor.setId(document.getId());
                    actorList.add(actor);
                    // Формат: ФИО (ID)
                    actorNames.add(actor.getName() + " (" + actor.getId() + ")");
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        getContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        actorNames);
                spinnerActors.setAdapter(adapter);

            } else {
                Toast.makeText(getContext(), "Ошибка загрузки актеров: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // --- Создание Назначения (Assignment) (FR-07) ---
    private void createAssignment() {
        int playPos = spinnerPlays.getSelectedItemPosition();
        int actorPos = spinnerActors.getSelectedItemPosition();
        String roleName = editTextRoleName.getText().toString().trim();

        if (playPos <= 0 || actorPos <= 0 || roleName.isEmpty()) {
            Toast.makeText(getContext(), "Необходимо выбрать спектакль, актера и ввести название роли.", Toast.LENGTH_SHORT).show();
            return;
        }

        final Play selectedPlay = playList.get(playPos);
        final Actor selectedActor = actorList.get(actorPos);

        // 1. Проверка на дублирование (документ с таким playId, actorId и roleName уже существует)
        assignmentsRef.whereEqualTo("playId", selectedPlay.getPlayId())
                .whereEqualTo("actorId", selectedActor.getId())
                .whereEqualTo("roleName", roleName)
                .get().addOnCompleteListener(checkTask -> {
                    if (checkTask.isSuccessful() && !checkTask.getResult().isEmpty()) {
                        Toast.makeText(getContext(), "Актер уже назначен на эту роль в этом спектакле!", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // 2. Создание нового назначения
                    String assignmentId = assignmentsRef.document().getId();
                    Assignment newAssignment = new Assignment(
                            assignmentId,
                            selectedPlay.getPlayId(),
                            selectedActor.getId(),
                            roleName // Ввод роли Админом, так как отдельной коллекции ролей нет
                    );

                    // 3. Сохранение в Firestore
                    assignmentsRef.document(assignmentId).set(newAssignment)
                            .addOnCompleteListener(setTask -> {
                                if (setTask.isSuccessful()) {
                                    Toast.makeText(getContext(),
                                            "Назначение: " + selectedActor.getName() + " на роль " + roleName + " успешно создано!",
                                            Toast.LENGTH_LONG).show();

                                    // Очистка UI после успешного сохранения
                                    editTextRoleName.setText("");
                                    spinnerPlays.setSelection(0);
                                    spinnerActors.setSelection(0);
                                } else {
                                    Toast.makeText(getContext(), "Ошибка назначения: " + setTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                });
    }
}