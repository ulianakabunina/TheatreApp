package com.example.theatreapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.theatreapp.MainActivity;
import com.example.theatreapp.R;
import com.example.theatreapp.models.Actor;

// НОВЫЕ ИМПОРТЫ ДЛЯ AUTH
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationFragment extends Fragment {

    private EditText editTextId;
    private Button buttonLogin;
    private CollectionReference usersRef;
    private FirebaseAuth firebaseAuth; // Поле для Firebase Auth

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        usersRef = FirebaseFirestore.getInstance().collection("users");
        firebaseAuth = FirebaseAuth.getInstance(); // Инициализация Firebase Auth
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authentication, container, false);

        editTextId = view.findViewById(R.id.edit_text_id);
        buttonLogin = view.findViewById(R.id.button_login);

        buttonLogin.setOnClickListener(v -> authenticateUser());

        return view;
    }

    private void authenticateUser() {
        final String inputCustomId = editTextId.getText().toString().trim();

        if (inputCustomId.isEmpty()) {
            Toast.makeText(getContext(), "Введите ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Анонимный вход в Firebase Auth.
        // Это необходимо, чтобы получить Firebase UID и установить request.auth != null
        firebaseAuth.signInAnonymously().addOnCompleteListener(authTask -> {
            if (authTask.isSuccessful()) {

                final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser == null) return;

                final String currentUid = currentUser.getUid();

                // 2. Ищем профиль пользователя в Firestore по его Кастомному ID (полю 'id').
                // Это необходимо, так как мы не знаем, какой ID соответствует какому UID.
                usersRef.whereEqualTo("id", inputCustomId).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {

                        // Пользователь найден.
                        DocumentSnapshot foundDocument = task.getResult().getDocuments().get(0);
                        Actor actor = foundDocument.toObject(Actor.class);

                        if (actor != null) {

                            // 3. Сохраняем/обновляем документ профиля с ID, равным текущему UID.
                            // Это делает наш Document ID совместимым с Правилами безопасности.
                            usersRef.document(currentUid).set(actor)
                                    .addOnSuccessListener(aVoid -> {
                                        // Успешно, теперь есть Document ID = UID.
                                        handleSuccessfulLogin(actor);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Ошибка привязки UID: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });

                        } else {
                            Toast.makeText(getContext(), "Ошибка: неверный формат профиля.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Пользователь не найден.
                        Toast.makeText(getContext(), "ID не найден. Попробуйте снова.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Ошибка аутентификации: " + authTask.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleSuccessfulLogin(Actor actor) {
        if ("admin".equals(actor.getRole())) {
            // Вход как Администратор. Теперь Rule Set сработает.
            ((MainActivity) getActivity()).loadFragment(new AdminHostFragment(), false);
            Toast.makeText(getContext(), "Вход как Администратор", Toast.LENGTH_SHORT).show();
        } else if ("actor".equals(actor.getRole())) {
            // Вход как Актёр
            ((MainActivity) getActivity()).loadFragment(new ActorHostFragment(), false);
            Toast.makeText(getContext(), "Добро пожаловать, " + actor.getName(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Неверная роль.", Toast.LENGTH_SHORT).show();
        }
    }
}