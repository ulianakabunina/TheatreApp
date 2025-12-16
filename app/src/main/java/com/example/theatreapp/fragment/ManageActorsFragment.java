package com.example.theatreapp.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theatreapp.R;
import com.example.theatreapp.models.Actor;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManageActorsFragment extends Fragment {

    private EditText editTextName, editTextActorId; // Изменили FIO на Name
    private Button buttonCreateActor;
    private RecyclerView recyclerViewActors;
    private CollectionReference actorsRef;

    private List<Actor> actorList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ссылка на коллекцию "users" в Firestore
        actorsRef = FirebaseFirestore.getInstance().collection("users");
        actorList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_actors, container, false);

        editTextName = view.findViewById(R.id.edit_text_fio); // Используем тот же ID разметки (fio)
        editTextActorId = view.findViewById(R.id.edit_text_actor_id);
        buttonCreateActor = view.findViewById(R.id.button_create_actor);
        recyclerViewActors = view.findViewById(R.id.recycler_view_actors);

        // ... (Настройка RecyclerView, которую мы пока опускаем)

        buttonCreateActor.setOnClickListener(v -> createActor());
        loadActors();

        return view;
    }

    // --- ФУНКЦИОНАЛ: СОЗДАНИЕ УЧЕТНОЙ ЗАПИСИ (ID и ФИО/Name) ---
    private void createActor() {
        final String name = editTextName.getText().toString().trim();
        final String id = editTextActorId.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(id)) {
            Toast.makeText(getContext(), "Поля ФИО/Name и ID обязательны для заполнения", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Проверяем, существует ли документ с таким ID
        actorsRef.document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Toast.makeText(getContext(), "Ошибка: ID уже используется.", Toast.LENGTH_SHORT).show();
            } else {
                // 2. Создаем нового актера
                // Важно: Поля в объекте Actor должны совпадать с ключами в Firestore: name, role
                Actor newActor = new Actor(id, name, "actor");

                // Сохраняем актера в Firestore, используя ID как ключ документа
                actorsRef.document(id).set(newActor)
                        .addOnCompleteListener(setTask -> {
                            if (setTask.isSuccessful()) {
                                Toast.makeText(getContext(), "Актер " + name + " успешно создан!", Toast.LENGTH_SHORT).show();
                                editTextName.setText("");
                                editTextActorId.setText("");
                            } else {
                                Toast.makeText(getContext(), "Ошибка создания актера: " + setTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    // --- ФУНКЦИОНАЛ: ПРОСМОТР АКТЕРОВ ---
    private void loadActors() {
        // Слушаем изменения в коллекции
        actorsRef.whereEqualTo("role", "actor").addSnapshotListener((snapshots, error) -> {
            if (error != null) {
                Log.w("Firestore", "Listen failed.", error);
                Toast.makeText(getContext(), "Не удалось загрузить актеров.", Toast.LENGTH_LONG).show();
                return;
            }

            if (snapshots != null) {
                actorList.clear();
                for (QueryDocumentSnapshot doc : snapshots) {
                    // doc.toObject(Actor.class) автоматически маппит поля
                    Actor actor = doc.toObject(Actor.class);
                    // Устанавливаем ID, который равен ID документа
                    actor.setId(doc.getId());
                    actorList.add(actor);
                }
                // TODO: Здесь адаптер должен быть обновлен: actorAdapter.notifyDataSetChanged();
            }
        });
    }
}