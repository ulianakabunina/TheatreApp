package com.example.theatreapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.theatreapp.fragment.AuthenticationFragment;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Инициализация Firebase в приложении
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        // При первом запуске загружаем фрагмент авторизации
        if (savedInstanceState == null) {
            loadFragment(new AuthenticationFragment(), false);
        }
    }

    /**
     * Метод для загрузки/замены фрагментов в контейнере Activity
     * @param fragment Фрагмент, который нужно загрузить
     * @param addToBackStack Добавлять ли фрагмент в стек возврата (true для навигации)
     */
    public void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Замена фрагмента в контейнере R.id.fragment_container
        transaction.replace(R.id.fragment_container, fragment);

        if (addToBackStack) {
            // Добавляем тег для удобства
            transaction.addToBackStack(fragment.getClass().getSimpleName());
        }

        transaction.commit();
    }
}