package com.example.theatreapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private RecyclerView recyclerView;
    private PlayAdapter adapter;
    private List<Play> playList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repertoire, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_repertoire);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PlayAdapter(playList);
        recyclerView.setAdapter(adapter);

        loadRepertoire();
        return view;
    }

    private void loadRepertoire() {
        FirebaseFirestore.getInstance().collection("plays").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    playList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        playList.add(doc.toObject(Play.class));
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}