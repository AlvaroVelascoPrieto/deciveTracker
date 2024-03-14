package com.example.entrega.view;

import static android.app.PendingIntent.getActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.entrega.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Preferences extends AppCompatActivity {

    static final String DEFAULT_LANGUAGE = "en";
    private Spinner languageSpinner;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String selectedLanguage = preferences.getString("selected_language", "");

        languageSpinner = findViewById(R.id.idSpinerLanguage);
        Button applyChanges = findViewById(R.id.idBtnApplyChanges);
        // Spinner Drop down elements
        List<String> languages = new ArrayList<String>();
        languages.add("Spanish");
        languages.add("English");
        languages.add("Basque");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languages);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        languageSpinner.setAdapter(dataAdapter);
        languageSpinner.setSelection(dataAdapter.getPosition(selectedLanguage));

        applyChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String language = languageSpinner.getSelectedItem().toString();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(Preferences.this);
                String currentLanguage = preferences.getString("selected_language", "");

                // Check if the selected language is different from the current language
                if (!language.equals(getLanguageName(currentLanguage))) {
                    setLocale(language);
                    recreate();
                }

                Intent i = new Intent(Preferences.this, MainActivity.class);
                startActivity(i);
            }
        });


    }
    private void setLocale(String language) {
        Locale newLocale;
        switch (language) {
            case "Spanish":
                newLocale = new Locale("es");
                break;
            case "Basque":
                newLocale = new Locale("eu");
                break;
            default:
                newLocale = Locale.ENGLISH;
                break;
        }

        // Set the new locale configuration
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(newLocale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        // Save the selected language in SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("selected_language", newLocale.getLanguage());
        editor.apply();
    }

    private String getLanguageName(String languageCode) {
        switch (languageCode) {
            case "es":
                return "Spanish";
            case "eu":
                return "Basque";
            default:
                return "English";
        }
    }


}
