package com.example.fairytales;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class SettingsActivity extends AppCompatActivity {
    ConstraintLayout view;
    Button buttonSave;
    String nameClass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nameClass = extras.getString("class");
        }

        view = (ConstraintLayout) findViewById(R.id.settings_layout_id);

        findViewById(R.id.sizeBig2).setOnClickListener((view)->{
            ThemeManager.saveTextSize(this, ThemeManager.TEXT_SIZE_25);
            recreate();
        });
        findViewById(R.id.sizeBig1).setOnClickListener((view)-> {
            ThemeManager.saveTextSize(this, ThemeManager.TEXT_SIZE_18);
            recreate();
        });
        findViewById(R.id.sizeMedium).setOnClickListener((view)->{
            ThemeManager.saveTextSize(this, ThemeManager.TEXT_SIZE_16);
            recreate();
        });
        findViewById(R.id.sizeSmall).setOnClickListener((view)->{
            ThemeManager.saveTextSize(this, ThemeManager.TEXT_SIZE_DEF_14);
            recreate();
        });

        findViewById(R.id.colorWhite).setOnClickListener((view)->onColorClick(ThemeManager.THEME_LIGHT));
        findViewById(R.id.colorSepia).setOnClickListener((view)->onColorClick(ThemeManager.THEME_SEPIA));
        findViewById(R.id.colorBlue).setOnClickListener((view)->onColorClick(ThemeManager.THEME_BLUE));
        findViewById(R.id.colorGreen).setOnClickListener((view)->onColorClick(ThemeManager.THEME_GREEN));

        buttonSave = findViewById(R.id.buttonSave);
        View.OnClickListener clckLstnrSave = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent;
                    if (nameClass.equals(getString(R.string.name_class_add_and_change_fairy_tales_activity))) {
                        intent = new Intent(SettingsActivity.this, AddAndChangeFairyTalesActivity.class);
                        startActivity(intent);
                    } else if (nameClass.equals(getString(R.string.name_class_fairy_tales_activity))) {
                        intent = new Intent(SettingsActivity.this, FairyTalesActivity.class);
                        startActivity(intent);
                    } else if (nameClass.equals(getString(R.string.name_class_main_activity))) {
                        intent = new Intent(SettingsActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        intent = new Intent(SettingsActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }catch (Exception e){
                    Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        };
        buttonSave.setOnClickListener(clckLstnrSave);
    }
    // обработка нажатия на кнопку цвета фона
    public void onColorClick(String color){
        ThemeManager.saveTheme(this, color);
        recreate(); // Перезапуск активности для применения темы
    }
}
