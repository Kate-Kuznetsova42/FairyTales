package com.example.fairytales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddFairyTalesActivity extends AppCompatActivity {
    Button addButton;
    EditText userNameFTEditT;
    EditText userAuthorFTEditT;
    EditText userTextFTEditT;
    private String userNameFT_str;
    private String userAuthorFT_str;
    private String userTextFT_str;
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_fairy_tales);

        addButton = findViewById(R.id.buttonAddFairy);
        userNameFTEditT = findViewById(R.id.addNameFairyTale);
        userAuthorFTEditT = findViewById(R.id.addAuthorFairyTale);
        userTextFTEditT = findViewById(R.id.addTextFairyTale);

        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.open();

        View.OnClickListener clLisAddButton = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {userNameFT_str = userNameFTEditT.getText().toString();
                    userAuthorFT_str = userAuthorFTEditT.getText().toString();
                    userTextFT_str = userTextFTEditT.getText().toString();

                    if (userNameFT_str.equals("")) {
                        Toast.makeText(getApplicationContext(),"Заполни поле 'Название'",Toast.LENGTH_SHORT).show();

                    } else if (userTextFT_str.equals("")) {
                        Toast.makeText(getApplicationContext(),"Заполни поле 'Текст'",Toast.LENGTH_SHORT).show();
                    } else {
                        db.execSQL("insert into " + DatabaseHelper.TABLE + "(" + DatabaseHelper.COLUMN_NAME
                                + "," + DatabaseHelper.COLUMN_AUTHOR + "," + DatabaseHelper.COLUMN_TEXT
                                + ")" + "values ('" + userNameFT_str + "', '" + userAuthorFT_str + "', '" + userTextFT_str + "');");
                        Intent intent = new Intent(AddFairyTalesActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        };
        addButton.setOnClickListener(clLisAddButton);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.app_bar_settings) {
            Intent intent = new Intent(AddFairyTalesActivity.this, SettingsActivity.class);
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