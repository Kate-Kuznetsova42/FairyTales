package com.example.fairytales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddAndChangeFairyTalesActivity extends AppCompatActivity {
    Button addOrChangeButton;
    EditText userNameFTEditT;
    EditText userAuthorFTEditT;
    EditText userTextFTEditT;
    private String userNameFT_str;
    private String userAuthorFT_str;
    private String userTextFT_str;
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    String typeAddOrChange;
    int idChange;
    Cursor taleCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_and_change_fairy_tales);

        addOrChangeButton = findViewById(R.id.buttonAddFairy);
        userNameFTEditT = findViewById(R.id.addNameFairyTale);
        userAuthorFTEditT = findViewById(R.id.addAuthorFairyTale);
        userTextFTEditT = findViewById(R.id.addTextFairyTale);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            typeAddOrChange = extras.getString("type");
        }

        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.open();

        if (typeAddOrChange.equals("change")) {
            addOrChangeButton.setText(R.string.change_button_str);
            Bundle extrasChange = getIntent().getExtras();
            if (extrasChange != null) {
                idChange = extrasChange.getInt("idUpdate");
            }
            // получаем элемент по id из бд
            taleCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE + " where " +
                    DatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(idChange)});
            taleCursor.moveToFirst();
            userNameFTEditT.setText(taleCursor.getString(1));
            userAuthorFTEditT.setText(taleCursor.getString(2));
            userTextFTEditT.setText(taleCursor.getString(3));
        }

        View.OnClickListener clLisAddORChangeButton = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    userNameFT_str = userNameFTEditT.getText().toString();
                    userAuthorFT_str = userAuthorFTEditT.getText().toString();
                    userTextFT_str = userTextFTEditT.getText().toString();

                    if (userNameFT_str.equals("")) {
                        Toast.makeText(getApplicationContext(), "Заполни поле 'Название'", Toast.LENGTH_SHORT).show();

                    } else if (userTextFT_str.equals("")) {
                        Toast.makeText(getApplicationContext(), "Заполни поле 'Текст'", Toast.LENGTH_SHORT).show();
                    } else {
                        if (typeAddOrChange.equals("change")) {
                            db.execSQL("update " + DatabaseHelper.TABLE + " set " + DatabaseHelper.COLUMN_NAME
                                    + " = '" + userNameFT_str + "', " + DatabaseHelper.COLUMN_AUTHOR + " = '" + userAuthorFT_str + "', "
                                    + DatabaseHelper.COLUMN_TEXT + " = '" + userTextFT_str + "' where "
                                    + DatabaseHelper.COLUMN_ID + " = " + idChange + ";");
                            Intent intent = new Intent(AddAndChangeFairyTalesActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            db.execSQL("insert into " + DatabaseHelper.TABLE + "(" + DatabaseHelper.COLUMN_NAME
                                    + "," + DatabaseHelper.COLUMN_AUTHOR + "," + DatabaseHelper.COLUMN_TEXT
                                    + ")" + "values ('" + userNameFT_str + "', '" + userAuthorFT_str + "', '" + userTextFT_str + "');");
                            Intent intent = new Intent(AddAndChangeFairyTalesActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }

                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };
        addOrChangeButton.setOnClickListener(clLisAddORChangeButton);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.app_bar_settings) {
            Intent intent = new Intent(AddAndChangeFairyTalesActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Закрываем подключение
        db.close();
    }
}