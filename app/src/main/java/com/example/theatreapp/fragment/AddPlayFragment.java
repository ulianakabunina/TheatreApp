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

import com.example.theatreapp.R;
import com.example.theatreapp.models.Play;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddPlayFragment extends Fragment {

    private EditText etTitle, etAuthor, etDirector, etGenre, etPremiereDate;
    private Button btnSave;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_play, container, false);

        db = FirebaseFirestore.getInstance();

        etTitle = view.findViewById(R.id.edit_text_title);
        etAuthor = view.findViewById(R.id.edit_text_author);
        etDirector = view.findViewById(R.id.edit_text_director);
        etGenre = view.findViewById(R.id.edit_text_genre);
        etPremiereDate = view.findViewById(R.id.edit_text_premiere);
        btnSave = view.findViewById(R.id.button_save_play);

        btnSave.setOnClickListener(v -> savePlayToFirestore());

        return view;
    }

    private void savePlayToFirestore() {
        String title = etTitle.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        String director = etDirector.getText().toString().trim();
        String genre = etGenre.getText().toString().trim();
        String premiereDate = etPremiereDate.getText().toString().trim(); // 🔴 3.2 Validation Error — нет проверки формата даты
        String status = "В репертуаре";

        if (title.isEmpty()) { // 🔴 3.1 Validation Error — проверяется только одно поле
            Toast.makeText(getContext(), "Введите название спектакля", Toast.LENGTH_SHORT).show(); // 🔴 1.1 NullPointerException — getContext() может быть null
            return;
        }

        Play newPlay = new Play(null, title, author, director, genre, status, premiereDate);

        db.collection("plays")
                .add(newPlay) // 🔴 4.1 Firebase Error — возможны ошибки сети/доступа
                .addOnSuccessListener(documentReference -> {

                    String generatedId = documentReference.getId();

                    db.collection("plays").document(generatedId)
                            .update("playId", generatedId); // 🔴 4.2 Firebase Error — update может не выполниться

                    Toast.makeText(getContext(), "Спектакль успешно добавлен!", Toast.LENGTH_SHORT).show(); // 🔴 1.2 NullPointerException

                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager().popBackStack(); // 🔴 2.1 IllegalStateException — Fragment может быть уже отсоединён
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show(); // 🔴 1.2 NullPointerException
                });
    }
}