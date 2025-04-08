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
//    int size_text = 14;
    //String color_background = "#FFFFFF";
    ConstraintLayout view;
    Button buttonSave;
    String nameClass;

    //Button sizeBig2, sizeBig1, sizeMedium, sizeSmall;
    //Button colorWh, colorSep, colorGr, colorBl;

    final static String SizeTextKey = "SizeText";
    final static String ColorBackgroundKey = "ColorBackground";
    private static final String LOG_TAG = SettingsActivity.class.getSimpleName();



    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putString(ColorBackgroundKey, color_background);
        //outState.putInt(SizeTextKey, size_text);
        //Log.i(LOG_TAG, "onSaveInstanceState");
    }

    // получение ранее сохраненного состояния
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //size_text = savedInstanceState.getInt(SizeTextKey);
        //color_background = savedInstanceState.getString(ColorBackgroundKey);
        //view = (ConstraintLayout) findViewById(R.id.settings_layout_id);
        //view.setBackgroundColor(Color.parseColor(color_background));
        //Log.i(LOG_TAG, "onRestoreInstanceState");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nameClass = extras.getString("class");
        }

//        Bundle arguments = getIntent().getExtras();
//        if(arguments!=null) {
//            //color_background = arguments.getString(ColorBackgroundKey);
//            size_text = arguments.getInt(SizeTextKey);
//        }
        view = (ConstraintLayout) findViewById(R.id.settings_layout_id);
        //view.setBackgroundColor(Color.parseColor(color_background));

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
                    }
                    if (nameClass.equals(getString(R.string.name_class_fairy_tales_activity))) {
                        intent = new Intent(SettingsActivity.this, FairyTalesActivity.class);
                        startActivity(intent);
                    }
                    if (nameClass.equals(getString(R.string.name_class_main_activity))) {
                        intent = new Intent(SettingsActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }catch (Exception e){
                    Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        };
        buttonSave.setOnClickListener(clckLstnrSave);

        //textView.setTextSize(28);
        //textViewContextMenu.setBackgroundColor(Color.RED);

        /*addBtn = (Button) findViewById(R.id.button);
        View.OnClickListener clckLstnrOPEN = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fairyTaleAdd = new FairyTale(nameFTEditText.getText().toString());
                writerFile(fairyTaleAdd, textFTEditText.getText().toString());

                Intent intent = new Intent(AddFairyTalesActivity.this, MainActivity.class);
                intent.putExtra(FairyTale.class.getSimpleName(), fairyTaleAdd);
                startActivity(intent);
            }
        };
        addBtn.setOnClickListener(clckLstnrOPEN);*/
    }
    // обработка нажатия на кнопку размера текста
    public void onSizeClick(int size){
//        size_text = size;
    }
    // обработка нажатия на кнопку цвета фона
    public void onColorClick(String color){
        //color_background = color;
        ThemeManager.saveTheme(this, color);
        recreate(); // Перезапуск активности для применения темы
//        view = (ConstraintLayout) findViewById(R.id.settings_layout_id);
//        //view.setBackgroundColor(Color.parseColor(color_background));
    }
}
