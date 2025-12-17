package com.example.theatreapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.theatreapp.R;
import com.example.theatreapp.models.Actor;
import com.example.theatreapp.models.Play;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssignRoleFragment extends Fragment {

    private Spinner spinnerPlays, spinnerActors;
    private Button btnAssign;
    private FirebaseFirestore db;

    private List<Play> playList = new ArrayList<>();
    private List<Actor> actorList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assign_role, container, false);

        db = FirebaseFirestore.getInstance();
        spinnerPlays = view.findViewById(R.id.spinner_plays);
        spinnerActors = view.findViewById(R.id.spinner_actors);
        btnAssign = view.findViewById(R.id.btn_assign);

        loadData();

        btnAssign.setOnClickListener(v -> performAssignment());

        return view;
    }

    private void loadData() {
        // Загружаем спектакли
        db.collection("plays").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> titles = new ArrayList<>();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Play p = doc.toObject(Play.class);
                    p.setPlayId(doc.getId());
                    playList.add(p);
                    titles.add(p.getTitle());
                }
                spinnerPlays.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, titles));
            }
        });

        // Загружаем актеров (пользователей с ролью 'actor')
        db.collection("users").whereEqualTo("role", "actor").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> names = new ArrayList<>();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Actor a = doc.toObject(Actor.class);
                    // Если в модели Actor нет поля для UID, используем ID документа
                    actorList.add(a);
                    names.add(a.getName());
                }
                spinnerActors.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, names));
            }
        });
    }

    private void performAssignment() {
        int playPos = spinnerPlays.getSelectedItemPosition();
        int actorPos = spinnerActors.getSelectedItemPosition();

        if (playPos < 0 || actorPos < 0) return;

        Play selectedPlay = playList.get(playPos);
        Actor selectedActor = actorList.get(actorPos);

        // Создаем запись в коллекции assignments
        Map<String, Object> assignment = new HashMap<>();
        assignment.put("playId", selectedPlay.getPlayId());
        assignment.put("playTitle", selectedPlay.getTitle());
        assignment.put("actorName", selectedActor.getName());
        // ВАЖНО: привязываем к кастомному ID актера, чтобы он мог найти свои роли
        assignment.put("actorId", selectedActor.getId());

        db.collection("assignments").add(assignment)
                .addOnSuccessListener(ref -> Toast.makeText(getContext(), "Роль назначена!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}