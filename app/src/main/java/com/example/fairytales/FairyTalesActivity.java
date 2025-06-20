package com.example.fairytales;

import android.content.Intent;
import android.database.SQLException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.io.File;

public class FairyTalesActivity extends AppCompatActivity {
    TextView name;
    TextView author;
    TextView text;
    ImageView image;
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor taleCursor;
    long taleId =0;
    String imagePath;
    ScrollView scrollView;
    private int scrollPercent;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle extras = getIntent().getExtras();
        outState.putLong("taleId_KEY", taleId);
        outState.putInt("scrollPercent_KEY", scrollPercent);
    }

    // получение ранее сохраненного состояния
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        taleId = savedInstanceState.getLong("taleId_KEY");
        FairyTalesActivity.this.scrollPercent = savedInstanceState.getInt("scrollPercent_KEY");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fairy_tales);

        name = findViewById(R.id.nameFairyTale);
        text = findViewById(R.id.textFairyTale);
        author = findViewById(R.id.authorFairyTale);
        image = findViewById(R.id.imageFairyTale);

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
                }
            });
        }

        /// получаем элемент по id из бд
        taleCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(taleId)});
        taleCursor.moveToFirst();
        name.setText(taleCursor.getString(1));
        author.setText(taleCursor.getString(2));
        text.setText(taleCursor.getString(3));        
        String IMAGE_DIR = "tale_images";
        String imageName = taleCursor.getString(5);
        ImageStorageHelper imageStorageHelper = new ImageStorageHelper(this);
        String imagePath = "file://" + imageStorageHelper.getImageFullPath(imageName);

        Glide
                .with(FairyTalesActivity.this)
                .load(Uri.parse(imagePath))
                .error(R.drawable.ic_action_no_picture)
                .into(image);
        try {
            scrollPercent = taleCursor.getInt(4);
            scrollView.post(() -> scrollView.scrollTo(0, scrollPercent)); // Восстанавливаем позицию
        } catch (SQLException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
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
            intent.putExtra("class", "FairyTalesActivity");
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
            FairyTalesActivity.this.db.execSQL("UPDATE " + DatabaseHelper.TABLE + " SET " + DatabaseHelper.COLUMN_SCROLLPERCENT
                    + " = " + FairyTalesActivity.this.scrollPercent + " WHERE " + DatabaseHelper.COLUMN_ID + " = " + taleId + ";");
        } catch (SQLException ex) {
            Toast toast = Toast.makeText(FairyTalesActivity.this, ex.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }
        taleCursor.close();
        db.close();
    }
}
