package com.example.theatreapp;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.theatreapp.fragment.ActorHostFragment;
import com.example.theatreapp.fragment.AdminHostFragment;
import com.example.theatreapp.fragment.AuthenticationFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MainActivity onCreate");

        // ВАЖНО: Инициализируем SessionManager здесь
        SessionManager.initialize(this);
        Log.d(TAG, "SessionManager initialized");

        setContentView(R.layout.activity_main);

        // Проверяем, залогинен ли пользователь
        SessionManager session = SessionManager.getInstance();
        if (session != null && session.isLoggedIn()) {
            String role = session.getCurrentActorRole();
            Log.d(TAG, "User is logged in with role: " + role);

            if ("admin".equals(role)) {
                loadFragment(new AdminHostFragment(), false);
            } else {
                loadFragment(new ActorHostFragment(), false);
            }
        } else {
            loadFragment(new AuthenticationFragment(), false);
        }
    }

    public void loadFragment(Fragment fragment, boolean addToBackStack) {
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);

            if (addToBackStack) {
                transaction.addToBackStack(null);
            }

            transaction.commit();
        } catch (Exception e) {
            Log.e(TAG, "Error loading fragment: ", e);
        }
    }
}