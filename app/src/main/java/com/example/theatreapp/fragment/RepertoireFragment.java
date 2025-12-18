package com.example.theatreapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.theatreapp.R;
import com.example.theatreapp.adapters.PlayAdapter;
import com.example.theatreapp.models.Play;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RepertoireFragment extends Fragment {
    private static final String TAG = "RepertoireFragment";

    private RecyclerView recyclerView;
    private PlayAdapter adapter;
    private List<Play> playList = new ArrayList<>();
    private TextView tvPlayCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repertoire, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_repertoire);
        tvPlayCount = view.findViewById(R.id.tv_play_count);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PlayAdapter(playList);
        recyclerView.setAdapter(adapter);

        loadRepertoire();
        return view;
    }

    private void loadRepertoire() {
        Log.d(TAG, "Загрузка репертуара...");

        if (tvPlayCount != null) {
            tvPlayCount.setText("⏳ Загрузка спектаклей...");
        }

        FirebaseFirestore.getInstance().collection("plays")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    playList.clear();
                    int count = 0;

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            Play play = doc.toObject(Play.class);
                            // Устанавливаем ID документа
                            play.setPlayId(doc.getId());
                            playList.add(play);
                            count++;
                            Log.d(TAG, "Загружен: " + play.getTitle());
                        } catch (Exception e) {
                            Log.e(TAG, "Ошибка загрузки документа: " + doc.getId(), e);
                        }
                    }

                    // Обновляем адаптер
                    adapter.setPlays(playList);

                    // Обновляем счетчик
                    updatePlayCount(count);

                    Log.d(TAG, "Загружено: " + count + " спектаклей");

                    if (count == 0) {
                        Toast.makeText(getContext(), "Спектакли не найдены", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Ошибка загрузки репертуара", e);
                    if (tvPlayCount != null) {
                        tvPlayCount.setText("❌ Ошибка загрузки");
                    }
                    Toast.makeText(getContext(), "Ошибка загрузки: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void updatePlayCount(int count) {
        if (tvPlayCount != null) {
            String countText;
            if (count == 0) {
                countText = "🎭 Нет спектаклей в репертуаре";
            } else if (count == 1) {
                countText = "🎭 1 спектакль в репертуаре";
            } else if (count < 5) {
                countText = "🎭 " + count + " спектакля в репертуаре";
            } else {
                countText = "🎭 " + count + " спектаклей в репертуаре";
            }
            tvPlayCount.setText(countText);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRepertoire();
    }
}