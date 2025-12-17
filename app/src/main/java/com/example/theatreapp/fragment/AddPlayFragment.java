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
        // Убедитесь, что название layout файла совпадает с вашим (например, fragment_add_play)
        View view = inflater.inflate(R.layout.fragment_add_play, container, false);

        db = FirebaseFirestore.getInstance();

        // Инициализация всех полей ввода согласно вашей модели Play
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
        String premiereDate = etPremiereDate.getText().toString().trim();
        String status = "В репертуаре"; // Статус по умолчанию

        // Валидация (минимум — название)
        if (title.isEmpty()) {
            Toast.makeText(getContext(), "Введите название спектакля", Toast.LENGTH_SHORT).show();
            return;
        }

        // Создаем объект Play. playId пока null, так как его создаст Firestore
        Play newPlay = new Play(null, title, author, director, genre, status, premiereDate);

        // Сохраняем в коллекцию "plays"
        db.collection("plays")
                .add(newPlay)
                .addOnSuccessListener(documentReference -> {
                    // После успешного создания получаем сгенерированный ID
                    String generatedId = documentReference.getId();

                    // Обновляем поле playId внутри документа, чтобы оно совпадало с ID документа
                    db.collection("plays").document(generatedId)
                            .update("playId", generatedId);

                    Toast.makeText(getContext(), "Спектакль успешно добавлен!", Toast.LENGTH_SHORT).show();

                    // Закрываем фрагмент и возвращаемся назад
                    if (getActivity() != null) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}