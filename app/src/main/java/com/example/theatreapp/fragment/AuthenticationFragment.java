package com.example.theatreapp.fragment;

import android.os.Bundle;
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

import com.example.theatreapp.MainActivity;
import com.example.theatreapp.R;
import com.example.theatreapp.SessionManager;
import com.example.theatreapp.models.Actor;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthenticationFragment extends Fragment {
    private static final String TAG = "AuthFragment";

    private EditText editTextId;
    private Button buttonLogin;
    private CollectionReference usersRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        usersRef = FirebaseFirestore.getInstance().collection("users");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_authentication, container, false);

        editTextId = view.findViewById(R.id.edit_text_id);
        buttonLogin = view.findViewById(R.id.button_login);

        buttonLogin.setOnClickListener(v -> {
            Log.d(TAG, "Login button clicked");
            authenticateUser();
        });

        return view;
    }

    private void authenticateUser() {
        String inputId = editTextId.getText().toString().trim();
        Log.d(TAG, "authenticateUser with ID: " + inputId);

        if (inputId.isEmpty()) {
            Toast.makeText(getContext(), "Введите ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверяем, что контекст не null
        if (getContext() == null) {
            Log.e(TAG, "Context is null!");
            Toast.makeText(getActivity(), "Ошибка контекста", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ищем по полю 'id' внутри документа
        usersRef.whereEqualTo("id", inputId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                Log.d(TAG, "User found in Firestore");

                // Нашли документ, где поле id = inputId
                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                Actor actor = document.toObject(Actor.class);

                if (actor != null) {
                    Log.d(TAG, "Actor object created: name=" + actor.getName() + ", role=" + actor.getRole());

                    // Получаем SessionManager - используем getActivity() как контекст
                    SessionManager session = SessionManager.getInstance(getActivity());

                    if (session == null) {
                        Log.e(TAG, "SessionManager is null! Trying alternative approach...");
                        // Пробуем другой способ
                        session = SessionManager.getInstance();

                        if (session == null) {
                            Log.e(TAG, "Cannot get SessionManager instance!");
                            Toast.makeText(getContext(), "Ошибка приложения", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    // Сохраняем данные
                    String documentId = document.getId();
                    Log.d(TAG, "Saving to session - Document ID: " + documentId + ", Name: " + actor.getName());

                    session.setCurrentActorId(documentId);
                    session.setCurrentActorName(actor.getName());
                    session.setCurrentActorRole(actor.getRole());

                    Log.d(TAG, "Session saved successfully");

                    // Переходим к соответствующему фрагменту
                    handleSuccessfulLogin(actor);
                } else {
                    Log.e(TAG, "Actor object is null");
                    Toast.makeText(getContext(), "Ошибка: неверный формат профиля.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "User not found or error: " + (task.getException() != null ? task.getException().getMessage() : "Unknown"));
                Toast.makeText(getContext(), "ID не найден. Попробуйте снова.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleSuccessfulLogin(Actor actor) {
        Log.d(TAG, "handleSuccessfulLogin called with role: " + actor.getRole());

        if (getActivity() == null) {
            Log.e(TAG, "Activity is null");
            return;
        }

        if (!(getActivity() instanceof MainActivity)) {
            Log.e(TAG, "Activity is not MainActivity");
            return;
        }

        MainActivity mainActivity = (MainActivity) getActivity();

        try {
            if ("admin".equals(actor.getRole())) {
                Log.d(TAG, "Loading AdminHostFragment");
                mainActivity.loadFragment(new AdminHostFragment(), false);
                Toast.makeText(getContext(), "Вход как Администратор", Toast.LENGTH_SHORT).show();
            } else if ("actor".equals(actor.getRole())) {
                Log.d(TAG, "Loading ActorHostFragment");
                mainActivity.loadFragment(new ActorHostFragment(), false);
                Toast.makeText(getContext(), "Добро пожаловать, " + actor.getName(), Toast.LENGTH_SHORT).show();
            } else {
                Log.w(TAG, "Unknown role: " + actor.getRole());
                Toast.makeText(getContext(), "Неверная роль.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in handleSuccessfulLogin: ", e);
            Toast.makeText(getContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}