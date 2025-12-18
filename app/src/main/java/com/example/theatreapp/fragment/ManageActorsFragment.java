package com.example.theatreapp.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theatreapp.R;
import com.example.theatreapp.adapters.ActorAdapter;
import com.example.theatreapp.models.Actor;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManageActorsFragment extends Fragment {

    private EditText editTextName, editTextActorId;
    private Button buttonCreateActor;
    private RecyclerView recyclerViewActors;
    private CollectionReference actorsRef;

    private List<Actor> actorList;
    private ActorAdapter actorAdapter;

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

        editTextName = view.findViewById(R.id.edit_text_fio);
        editTextActorId = view.findViewById(R.id.edit_text_actor_id);
        buttonCreateActor = view.findViewById(R.id.button_create_actor);
        recyclerViewActors = view.findViewById(R.id.recycler_view_actors);

        // Настройка RecyclerView
        actorAdapter = new ActorAdapter(actorList);
        recyclerViewActors.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewActors.setAdapter(actorAdapter);

        actorAdapter.setOnActorClickListener(new ActorAdapter.OnActorClickListener() {
            @Override
            public void onDeleteClick(Actor actor, int position) {
                deleteActor(actor, position);
            }
        });

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
                Actor newActor = new Actor(id, name, "actor");

                // Сохраняем актера в Firestore, используя ID как ключ документа
                actorsRef.document(id).set(newActor)
                        .addOnCompleteListener(setTask -> {
                            if (setTask.isSuccessful()) {
                                Toast.makeText(getContext(), "Актер " + name + " успешно создан!", Toast.LENGTH_SHORT).show();
                                editTextName.setText("");
                                editTextActorId.setText("");
                                // После создания перезагружаем список
                                loadActors();
                            } else {
                                Toast.makeText(getContext(), "Ошибка создания актера: " + setTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    // --- ФУНКЦИОНАЛ: ПРОСМОТР АКТЕРОВ ---
    private void loadActors() {
        actorsRef.whereEqualTo("role", "actor").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                actorList.clear();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Actor actor = doc.toObject(Actor.class);
                    // Устанавливаем ID, который равен ID документа
                    actor.setId(doc.getId());
                    actorList.add(actor);
                    Log.d("ManageActors", "Loaded actor: " + actor.getName() + ", ID: " + actor.getId());
                }

                // Обновляем адаптер
                if (actorAdapter != null) {
                    actorAdapter.notifyDataSetChanged();
                }

                // Показываем количество загруженных актеров
                if (actorList.isEmpty()) {
                    Toast.makeText(getContext(), "Нет зарегистрированных актеров", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Загружено актеров: " + actorList.size(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("ManageActors", "Error loading actors", task.getException());
                Toast.makeText(getContext(), "Не удалось загрузить актеров: " +
                                (task.getException() != null ? task.getException().getMessage() : "Unknown error"),
                        Toast.LENGTH_LONG).show();
            }
        });
        actorsRef.whereEqualTo("role", "actor").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                actorList.clear();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Actor actor = doc.toObject(Actor.class);
                    actor.setId(doc.getId());
                    actorList.add(actor);
                }

                // Обновляем счетчик
                TextView tvActorCount = getView().findViewById(R.id.tv_actor_count);
                if (tvActorCount != null) {
                    tvActorCount.setText("Актеров в базе: " + actorList.size());
                }

                actorAdapter.notifyDataSetChanged();
            }
        });
    }

    // Метод для удаления актера
    private void deleteActor(Actor actor, int position) {
        if (actor.getId() == null || actor.getId().isEmpty()) {
            Toast.makeText(getContext(), "Ошибка: ID актера не найден", Toast.LENGTH_SHORT).show();
            return;
        }

        actorsRef.document(actor.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Удаляем из списка
                    actorList.remove(position);
                    actorAdapter.notifyItemRemoved(position);
                    Toast.makeText(getContext(), "Актер удален", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Ошибка удаления: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}