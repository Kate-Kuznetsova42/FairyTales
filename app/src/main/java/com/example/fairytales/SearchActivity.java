package com.example.fairytales;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class SearchActivity extends AppCompatActivity {
    ListView fairyTalesList;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor taleCursor;
    SimpleCursorAdapter taleAdapter;
    EditText taleFilter;

    int size_text = 14;
    //String color_background = "#FFFFFF";
    ConstraintLayout view;

    //Button sizeBig2, sizeBig1, sizeMedium, sizeSmall;
    //Button colorWh, colorSep, colorGr, colorBl;

    final static String SizeTextKey = "SizeText";
    final static String ColorBackgroundKey = "ColorBackground";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this); // Применяем тему и размер текста
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Bundle arguments = getIntent().getExtras();
        if(arguments!=null) {
            //color_background = arguments.getString(ColorBackgroundKey);
            size_text = arguments.getInt(SizeTextKey);

        }
        view = (ConstraintLayout) findViewById(R.id.search_layout_id);
        //view.setBackgroundColor(Color.parseColor(color_background));


        taleFilter = (EditText)findViewById(R.id.talesFilter);
        // получаем элемент ListView
        fairyTalesList = findViewById(R.id.fairyTalesList_search);
        fairyTalesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), FairyTalesActivity.class);
                //intent.putExtra(ColorBackgroundKey, color_background);
                intent.putExtra(SizeTextKey, size_text);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        databaseHelper = new DatabaseHelper(getApplicationContext());
        // создаем базу данных
        //databaseHelper.create_db();
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

            // если в текстовом поле есть текст, выполняем фильтрацию
            // данная проверка нужна при переходе от одной ориентации экрана к другой
            //if(!taleFilter.getText().toString().isEmpty())
              //  taleAdapter.getFilter().filter(taleFilter.getText().toString());

            // установка слушателя изменения текста
            taleFilter.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {
                    if (s.toString().isEmpty()) {
                        // Если строка поиска пуста, очищаем ListView
                        fairyTalesList.setAdapter(null);
                    } else {
                        // В противном случае выполняем фильтрацию
                        taleAdapter.getFilter().filter(s.toString());
                        fairyTalesList.setAdapter(taleAdapter);
                    }
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                // при изменении текста выполняем фильтрацию
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    //taleAdapter.getFilter().filter(s.toString());
                }
            });

            // устанавливаем провайдер фильтрации
            taleAdapter.setFilterQueryProvider(new FilterQueryProvider() {
                @Override
                public Cursor runQuery(CharSequence constraint) {

                    if (constraint == null || constraint.length() == 0) {

                        return null;
                    }
                    else {
                        return db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                                DatabaseHelper.COLUMN_AUTHOR + " like ? or " + DatabaseHelper.COLUMN_NAME + " like ?", new String[]{"%" + constraint.toString() + "%", "%" + constraint.toString() + "%"});
                    }
                }
            });

            // Если в текстовом поле есть текст, выполняем фильтрацию
            // данная проверка нужна при переходе от одной ориентации экрана к другой
            if (!taleFilter.getText().toString().isEmpty()) {
                taleAdapter.getFilter().filter(taleFilter.getText().toString());
                fairyTalesList.setAdapter(taleAdapter);
            } else {
                // Если строка поиска пуста, очищаем ListView
                fairyTalesList.setAdapter(null);
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
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