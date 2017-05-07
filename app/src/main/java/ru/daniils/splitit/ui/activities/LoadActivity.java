package ru.daniils.splitit.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import ru.daniils.splitit.data.DataBase;
import ru.daniils.splitit.data.Preferences;

/**
 * Стартовая активность
 * Запускается при включении приложения.
 * При отсутствии токена перенаправляет на логин.
 * Иначе запускает MainActivity.
 */
public class LoadActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new DataBase(getApplicationContext()).close();
        Preferences.setContext(this);
        if (!Preferences.getBool("logged"))
            startActivity(new Intent(this, AuthActivity.class));
        else
            startActivity(new Intent(this, MainActivity.class));

        finish();
    }
}
