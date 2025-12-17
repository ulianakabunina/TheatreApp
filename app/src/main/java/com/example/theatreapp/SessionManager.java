package com.example.theatreapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SessionManager {
    private static final String TAG = "SessionManager";
    private static SessionManager instance;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "TheatreAppSession";

    private static final String KEY_ACTOR_ID = "actor_id";
    private static final String KEY_ACTOR_NAME = "actor_name";
    private static final String KEY_ACTOR_ROLE = "actor_role";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    // Конструктор приватный
    private SessionManager(Context context) {
        Log.d(TAG, "SessionManager constructor called");
        if (context != null) {
            Context appContext = context.getApplicationContext();
            sharedPreferences = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            Log.d(TAG, "SharedPreferences initialized successfully");
        } else {
            Log.e(TAG, "Context is null in constructor!");
        }
    }

    // Основной метод получения экземпляра
    public static synchronized SessionManager getInstance(Context context) {
        Log.d(TAG, "getInstance called with context: " + (context != null ? "not null" : "null"));

        if (instance == null) {
            Log.d(TAG, "Creating new instance of SessionManager");
            if (context != null) {
                instance = new SessionManager(context);
            } else {
                Log.e(TAG, "Cannot create instance: context is null!");
                return null;
            }
        }

        return instance;
    }

    // Метод без параметра для обратной совместимости
    public static synchronized SessionManager getInstance() {
        Log.d(TAG, "getInstance called without context");
        if (instance == null) {
            Log.e(TAG, "Instance is null! You must call getInstance(Context) first.");
        }
        return instance;
    }

    // Метод инициализации для использования в MainActivity
    public static void initialize(Context context) {
        Log.d(TAG, "initialize called with context");
        if (instance == null && context != null) {
            instance = new SessionManager(context);
        }
    }

    // Проверяем, инициализирован ли sharedPreferences
    private void checkInitialization() {
        if (sharedPreferences == null) {
            Log.e(TAG, "SharedPreferences is null! SessionManager not properly initialized.");
        }
    }

    // --- SETTERS ---
    public void setCurrentActorId(String actorId) {
        checkInitialization();
        Log.d(TAG, "setCurrentActorId: " + actorId);
        if (sharedPreferences != null) {
            sharedPreferences.edit()
                    .putString(KEY_ACTOR_ID, actorId)
                    .putBoolean(KEY_IS_LOGGED_IN, true)
                    .apply();
            Log.d(TAG, "actorId saved successfully");
        } else {
            Log.e(TAG, "Cannot save actorId: sharedPreferences is null");
        }
    }

    public void setCurrentActorName(String actorName) {
        checkInitialization();
        Log.d(TAG, "setCurrentActorName: " + actorName);
        if (sharedPreferences != null && actorName != null) {
            sharedPreferences.edit().putString(KEY_ACTOR_NAME, actorName).apply();
        }
    }

    public void setCurrentActorRole(String actorRole) {
        checkInitialization();
        Log.d(TAG, "setCurrentActorRole: " + actorRole);
        if (sharedPreferences != null && actorRole != null) {
            sharedPreferences.edit().putString(KEY_ACTOR_ROLE, actorRole).apply();
        }
    }

    // --- GETTERS ---
    public String getCurrentActorId() {
        checkInitialization();
        if (sharedPreferences != null) {
            return sharedPreferences.getString(KEY_ACTOR_ID, null);
        }
        return null;
    }

    public String getCurrentActorName() {
        checkInitialization();
        if (sharedPreferences != null) {
            return sharedPreferences.getString(KEY_ACTOR_NAME, null);
        }
        return null;
    }

    public String getCurrentActorRole() {
        checkInitialization();
        if (sharedPreferences != null) {
            return sharedPreferences.getString(KEY_ACTOR_ROLE, null);
        }
        return null;
    }

    public boolean isLoggedIn() {
        checkInitialization();
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
        }
        return false;
    }

    // --- OTHER METHODS ---
    public void clearSession() {
        checkInitialization();
        Log.d(TAG, "clearSession called");
        if (sharedPreferences != null) {
            sharedPreferences.edit().clear().apply();
        }
    }
}