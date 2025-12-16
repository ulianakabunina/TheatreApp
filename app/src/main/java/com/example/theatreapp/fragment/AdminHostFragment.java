package com.example.theatreapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.theatreapp.MainActivity;
import com.example.theatreapp.R;

public class AdminHostFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_host, container, false);

        Button btnActors = view.findViewById(R.id.btn_manage_actors);
        Button btnPerformances = view.findViewById(R.id.btn_manage_performances);
        Button btnAssignments = view.findViewById(R.id.btn_manage_assignments);

        btnActors.setOnClickListener(v -> {
            ((MainActivity) getActivity()).loadFragment(new ManageActorsFragment(), true);
        });

        // Заглушки для следующих шагов (пока без изменений)
        btnPerformances.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Следующий шаг: Управление Спектаклями", Toast.LENGTH_SHORT).show();
        });

        btnAssignments.setOnClickListener(v -> {
            ((MainActivity) getActivity()).loadFragment(new ManageAssignmentsFragment(), true);
        });

        return view;
    }
}