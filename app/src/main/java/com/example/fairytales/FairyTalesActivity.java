package com.example.fairytales;

import android.content.Intent;
import android.database.SQLException;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class FairyTalesActivity extends AppCompatActivity {
    TextView name;
    TextView author;
    TextView text;
    //FairyTale fairyTale;
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor taleCursor;
    long taleId =0;

    //int size_text = 14;
    //String color_background = "#FFFFFF";
    ScrollView scrollView;
    private int scrollPercent;
    //final static String SizeTextKey = "SizeText";
    final static String ColorBackgroundKey = "ColorBackground";
    final static String TaleIdKey = "TaleId";
    //private static final String LOG_TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle extras = getIntent().getExtras();
        outState.putLong("taleId_KEY", taleId);
        outState.putInt("scrollPercent_KEY", scrollPercent);
        //outState.putString(ColorBackgroundKey, color_background);
        //outState.putInt(SizeTextKey, size_text);
        //outState.putLong(TaleIdKey,taleId);
        //Log.i(LOG_TAG, "onSaveInstanceState");
    }

    // получение ранее сохраненного состояния
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        taleId = savedInstanceState.getLong("taleId_KEY");
        FairyTalesActivity.this.scrollPercent = savedInstanceState.getInt("scrollPercent_KEY");
        //size_text = savedInstanceState.getInt(SizeTextKey);
        //color_background = savedInstanceState.getString(ColorBackgroundKey);
        //view = (ScrollView) findViewById(R.id.tale_scroll_id);
        //view.setBackgroundColor(Color.parseColor(color_background));
        //Log.i(LOG_TAG, "onRestoreInstanceState");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fairy_tales);

        name = findViewById(R.id.nameFairyTale);
        text = findViewById(R.id.textFairyTale);
        author = findViewById(R.id.authorFairyTale);

        /*Bundle arguments = getIntent().getExtras();
        if(arguments!=null) {
            //color_background = arguments.getString(ColorBackgroundKey);
            //
            //size_text = arguments.getInt(SizeTextKey);
           // text.setTextSize(size_text);
        }*/
        scrollView = (ScrollView) findViewById(R.id.tale_scroll_id);
        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.open();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            taleId = extras.getLong("id");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    FairyTalesActivity.this.scrollPercent = scrollY;
                    //Toast.makeText(getApplicationContext(),String.valueOf(FairyTalesActivity.this.scrollPercent),Toast.LENGTH_SHORT).show();
                }
            });
        }
        //Toast.makeText(getApplicationContext(),String.valueOf(FairyTalesActivity.this.scrollPercent),Toast.LENGTH_SHORT).show();
        //view.setBackgroundColor(Color.parseColor(color_background));
        /*if (color_background.equals("#F8000000")) {
            name.setTextColor(Color.WHITE);
            author.setTextColor(Color.WHITE);
            text.setTextColor(Color.WHITE);
        }*/


        /*try {
            //Toast toast = Toast.makeText(this, String.valueOf(scrollPercent), Toast.LENGTH_LONG);
            //toast.show();
            db.execSQL("UPDATE " + DatabaseHelper.TABLE + " SET " + DatabaseHelper.COLUMN_SCROLLPERCENT
                + " = " + scrollPercent + " WHERE " + DatabaseHelper.COLUMN_ID + " = " + taleId + ";");
        } catch (SQLException ex) {
            Toast toast = Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }*/
        /// получаем элемент по id из бд
        taleCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(taleId)});
        taleCursor.moveToFirst();
        name.setText(taleCursor.getString(1));
        author.setText(taleCursor.getString(2));
        text.setText(taleCursor.getString(3));
        try {
            scrollPercent = taleCursor.getInt(4);
            //Toast.makeText(this, String.valueOf(scrollPercent), Toast.LENGTH_LONG).show();
            scrollView.post(() -> scrollView.scrollTo(0, scrollPercent)); // Восстанавливаем позицию
        } catch (SQLException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        /*if(arguments!=null){
            fairyTale = (FairyTale) arguments.getSerializable(FairyTale.class.getSimpleName());

            name.setText(fairyTale.getName());
            //textFairyTale.setText(fairyTale.getFILE_NAME());
            //readerFile(textFairyTale);
        }*/
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_import_menu, menu);
        // Скрываем пункт
        menu.findItem(R.id.app_bar_content_paste).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.app_bar_settings) {
            Intent intent = new Intent(FairyTalesActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            //Toast toast = Toast.makeText(this, String.valueOf(scrollPercent), Toast.LENGTH_LONG);
            //toast.show();
            FairyTalesActivity.this.db.execSQL("UPDATE " + DatabaseHelper.TABLE + " SET " + DatabaseHelper.COLUMN_SCROLLPERCENT
                    + " = " + FairyTalesActivity.this.scrollPercent + " WHERE " + DatabaseHelper.COLUMN_ID + " = " + taleId + ";");
        } catch (SQLException ex) {
            Toast toast = Toast.makeText(FairyTalesActivity.this, ex.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }
        taleCursor.close();
        // закрываем подключение
        db.close();
    }
}
