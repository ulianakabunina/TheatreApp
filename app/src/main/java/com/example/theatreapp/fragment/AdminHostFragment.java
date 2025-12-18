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
import com.example.theatreapp.SessionManager;

public class AdminHostFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_host, container, false);

        Button btnActors = view.findViewById(R.id.btn_manage_actors);
        Button btnAssignments = view.findViewById(R.id.btn_manage_assignments);
        Button buttonAddPlay = view.findViewById(R.id.btn_add_play);
        Button buttonAssignRole = view.findViewById(R.id.btn_assign_role);
        Button btnViewRepertoire = view.findViewById(R.id.btn_view_repertoire);
        Button btnLogout = view.findViewById(R.id.btn_logout_admin);

        btnActors.setOnClickListener(v -> {
            ((MainActivity) getActivity()).loadFragment(new ManageActorsFragment(), true);
        });

        btnAssignments.setOnClickListener(v -> {
            ((MainActivity) getActivity()).loadFragment(new ManageAssignmentsFragment(), true);
        });

        buttonAddPlay.setOnClickListener(v -> {
            ((MainActivity) getActivity()).loadFragment(new AddPlayFragment(), true);
        });

        buttonAssignRole.setOnClickListener(v -> {
            ((MainActivity) getActivity()).loadFragment(new ManageAssignmentsFragment(), true);
        });

        btnViewRepertoire.setOnClickListener(v -> {
            ((MainActivity) getActivity()).loadFragment(new RepertoireFragment(), true);
        });

        btnLogout.setOnClickListener(v -> {
            // Очищаем сессию
            SessionManager.getInstance().clearSession();

            // Переходим к экрану аутентификации
            ((MainActivity) getActivity()).loadFragment(new AuthenticationFragment(), false);
            Toast.makeText(getContext(), "Выход выполнен", Toast.LENGTH_SHORT).show();
        });

        btnAssignments.setOnClickListener(v -> {
            // Переходим к просмотру и удалению назначений
            ((MainActivity) getActivity()).loadFragment(new ViewAssignmentsFragment(), true);
        });

        return view;
    }
}