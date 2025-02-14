package com.example.fairytales;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class FairyTalesActivity extends AppCompatActivity {
    TextView name;
    TextView author;
    TextView text;
    //FairyTale fairyTale;
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor taleCursor;
    long taleId =0;

    int size_text = 14;
    //String color_background = "#FFFFFF";
    ScrollView view;
    final static String SizeTextKey = "SizeText";
    final static String ColorBackgroundKey = "ColorBackground";
    final static String TaleIdKey = "TaleId";
    //private static final String LOG_TAG = SettingsActivity.class.getSimpleName();

   /* @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ColorBackgroundKey, color_background);
        outState.putInt(SizeTextKey, size_text);
        outState.putLong(TaleIdKey,taleId);
        //Log.i(LOG_TAG, "onSaveInstanceState");
    }

    // получение ранее сохраненного состояния
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        size_text = savedInstanceState.getInt(SizeTextKey);
        color_background = savedInstanceState.getString(ColorBackgroundKey);
        view = (ScrollView) findViewById(R.id.tale_scroll_id);
        view.setBackgroundColor(Color.parseColor(color_background));
        //Log.i(LOG_TAG, "onRestoreInstanceState");
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fairy_tales);

        name = findViewById(R.id.nameFairyTale);
        text = findViewById(R.id.textFairyTale);
        author = findViewById(R.id.authorFairyTale);

        Bundle arguments = getIntent().getExtras();
        if(arguments!=null) {
            //color_background = arguments.getString(ColorBackgroundKey);
            //
            size_text = arguments.getInt(SizeTextKey);
        }
        view = (ScrollView) findViewById(R.id.tale_scroll_id);
        //view.setBackgroundColor(Color.parseColor(color_background));
        /*if (color_background.equals("#F8000000")) {
            name.setTextColor(Color.WHITE);
            author.setTextColor(Color.WHITE);
            text.setTextColor(Color.WHITE);
        }*/

        text.setTextSize(size_text);

        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.open();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            taleId = extras.getLong("id");
        }
        /// получаем элемент по id из бд
        taleCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(taleId)});
        taleCursor.moveToFirst();
        name.setText(taleCursor.getString(1));
        author.setText(taleCursor.getString(2));
        text.setText(taleCursor.getString(3));
        taleCursor.close();
        // закрываем подключение
        db.close();

        /*if(arguments!=null){
            fairyTale = (FairyTale) arguments.getSerializable(FairyTale.class.getSimpleName());

            name.setText(fairyTale.getName());
            //textFairyTale.setText(fairyTale.getFILE_NAME());
            //readerFile(textFairyTale);
        }*/
    }
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.app_bar_settings) {
            Intent intent = new Intent(FairyTalesActivity.this, SettingsActivity.class);
            // передача объекта с ключом "hello" и значением "Hello World"
            //intent.putExtra("hello", "Hello World");
            startActivity(intent);
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }*/
}
