package com.example.fairytales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.database.SQLException;
import android.graphics.Color;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.text.Editable;
import android.widget.FilterQueryProvider;


import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    ListView fairyTalesList;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor taleCursor;
    SimpleCursorAdapter taleAdapter;

    //ArrayList<FairyTale> fairyTales = new ArrayList<FairyTale>();

    //FairyTaleAdapter fairyTaleAdapter;

    /*final static String FairyTalesListKey = "FairyTalesList";

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(FairyTalesListKey, ArrayList<FairyTale>);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fairyTales = savedInstanceState.getParcelableArrayList(FairyTalesListKey, ArrayList<FairyTale>);
    }*/

    //int size_text = 14;
    //String color_background = "#FFFFFF";
    ConstraintLayout view;

    //Button sizeBig2, sizeBig1, sizeMedium, sizeSmall;
    //Button colorWh, colorSep, colorGr, colorBl;

    //final static String SizeTextKey = "SizeText";
    final static String ColorBackgroundKey = "ColorBackground";



    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putString(ColorBackgroundKey, color_background);
       // outState.putInt(SizeTextKey, size_text);
        //Log.i(LOG_TAG, "onSaveInstanceState");
    }

    // получение ранее сохраненного состояния
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //size_text = savedInstanceState.getInt(SizeTextKey);
        //color_background = savedInstanceState.getString(ColorBackgroundKey);
        //Log.i(LOG_TAG, "onRestoreInstanceState");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Bundle arguments = getIntent().getExtras();
        if(arguments!=null) {
            size_text = arguments.getInt(SizeTextKey);
            //color_background = arguments.getString(ColorBackgroundKey);
        }*/
        view = (ConstraintLayout) findViewById(R.id.main_layout_id);
        //view.setBackgroundColor(Color.parseColor(color_background));

        // получаем элемент ListView
        fairyTalesList = findViewById(R.id.fairyTalesList);
        fairyTalesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), FairyTalesActivity.class);
                intent.putExtra("id", id);
                //intent.putExtra(SizeTextKey, size_text);
                //intent.putExtra(ColorBackgroundKey, color_background);
                startActivity(intent);
            }
        });

        databaseHelper = new DatabaseHelper(getApplicationContext());
        // создаем базу данных
        databaseHelper.create_db();
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            // открываем подключение
            db = databaseHelper.open();
            //получаем данные из бд в виде курсора
            taleCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE, null);
            // определяем, какие столбцы из курсора будут выводиться в ListView
            String[] headers = new String[]{DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_AUTHOR};
            // создаем адаптер, передаем в него курсор
            taleAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item,
                    taleCursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);
            fairyTalesList.setAdapter(taleAdapter);
        }
        catch (SQLException ex){}

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.app_bar_search) {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            //intent.putExtra(SizeTextKey, size_text);
            //intent.putExtra(ColorBackgroundKey, color_background);
            startActivity(intent);
            return true;
        }else if (item.getItemId() == R.id.app_bar_add) {
            Intent intent = new Intent(MainActivity.this, AddFairyTalesActivity.class);
            //intent.putExtra(ColorBackgroundKey, color_background);
            //intent.putExtra(SizeTextKey, size_text);
            startActivity(intent);
            return true;
        }else if (item.getItemId() == R.id.app_bar_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            //intent.putExtra(ColorBackgroundKey, color_background);
            //intent.putExtra(SizeTextKey, size_text);
            startActivity(intent);
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Закрываем подключение и курсор
        db.close();
        taleCursor.close();
    }
}