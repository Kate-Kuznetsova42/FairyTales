package com.example.fairytales;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class AddAndChangeFairyTalesActivity extends AppCompatActivity {
    Button addOrChangeButton;
    EditText userNameFTEditT;
    EditText userAuthorFTEditT;
    EditText userTextFTEditT;
    ImageButton btnChangeImage;
    private String userNameFT_str;
    private String userAuthorFT_str;
    private String userTextFT_str;
    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    String typeAddOrChange;
    int idChange;
    Cursor taleCursor;
    Bitmap imageBitmap;
    private static final int PICK_FILE_REQUEST_CODE = 1;
    private ActivityResultLauncher<Intent> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_and_change_fairy_tales);

        addOrChangeButton = findViewById(R.id.buttonAddFairy);
        userNameFTEditT = findViewById(R.id.addNameFairyTale);
        userAuthorFTEditT = findViewById(R.id.addAuthorFairyTale);
        userTextFTEditT = findViewById(R.id.addTextFairyTale);
        btnChangeImage = findViewById(R.id.btnChangeImage);

        btnChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

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
            String imageName = taleCursor.getString(5);
            if (imageName != null){
                ImageStorageHelper imageStorageHelper = new ImageStorageHelper(this);
                String imagePath = "file://" + imageStorageHelper.getImageFullPath(imageName);

                Glide
                        .with(AddAndChangeFairyTalesActivity.this)
                        .load(Uri.parse(imagePath))
                        .error(R.drawable.ic_action_no_picture)
                        .into(btnChangeImage);
            }
        }

//         Инициализация ActivityResultLauncher
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    try {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Intent data = result.getData();
                            Uri fileUri = data.getData(); // URI выбранного файла
                            String mimeType = getContentResolver().getType(fileUri);
                            if (mimeType.startsWith("image/")) {
                                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
                                btnChangeImage.setImageBitmap(imageBitmap);
                            } else {
                                String fileContent = readFileContent(fileUri); // Чтение содержимого файла
                                String[] fileContentArray = fileContent.split("\n\n", 3);
                                userNameFTEditT.setText(fileContentArray[0]);// Отображение в EditText
                                userAuthorFTEditT.setText(fileContentArray[1]);
                                userTextFTEditT.setText(fileContentArray[2]);
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );

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
                        String sql;
                        String imageName = "faity_tale_" + String.valueOf(System.currentTimeMillis());


                        ImageStorageHelper imageStorageHelper = new ImageStorageHelper(AddAndChangeFairyTalesActivity.this);
                        if (imageBitmap != null) {
                            imageStorageHelper.saveImage(imageBitmap, imageName);
                        }

                        if (typeAddOrChange.equals("change")) {
                            if (imageBitmap != null) {
                                sql = "update " + DatabaseHelper.TABLE + " set " + DatabaseHelper.COLUMN_NAME
                                        + " = '" + userNameFT_str + "', " + DatabaseHelper.COLUMN_AUTHOR + " = '" + userAuthorFT_str + "', "
                                        + DatabaseHelper.COLUMN_TEXT + " = '" + userTextFT_str + "', "
                                        + DatabaseHelper.COLUMN_IMAGEPATH + " = '" + imageName + "' where "
                                        + DatabaseHelper.COLUMN_ID + " = " + idChange + ";";
                            } else {
                                sql = "update " + DatabaseHelper.TABLE + " set " + DatabaseHelper.COLUMN_NAME
                                        + " = '" + userNameFT_str + "', " + DatabaseHelper.COLUMN_AUTHOR + " = '" + userAuthorFT_str + "', "
                                        + DatabaseHelper.COLUMN_TEXT + " = '" + userTextFT_str + "' where "
                                        + DatabaseHelper.COLUMN_ID + " = " + idChange + ";";
                            }
                            db.execSQL(sql);
                            Intent intent = new Intent(AddAndChangeFairyTalesActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            if (imageBitmap != null) {
                                sql = "insert into " + DatabaseHelper.TABLE + "(" + DatabaseHelper.COLUMN_NAME
                                        + "," + DatabaseHelper.COLUMN_AUTHOR + "," + DatabaseHelper.COLUMN_TEXT
                                        + "," + DatabaseHelper.COLUMN_IMAGEPATH + ")" + "values ('" + userNameFT_str + "', '" + userAuthorFT_str + "', '" + userTextFT_str + "', '" + imageName + "');";
                            } else {
                                sql = "insert into " + DatabaseHelper.TABLE + "(" + DatabaseHelper.COLUMN_NAME
                                        + "," + DatabaseHelper.COLUMN_AUTHOR + "," + DatabaseHelper.COLUMN_TEXT
                                        + ")" + "values ('" + userNameFT_str + "', '" + userAuthorFT_str + "', '" + userTextFT_str + "');";

                            }
                            db.execSQL(sql);
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


    private void pickImage() {

        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            filePickerLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void pickFile() {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("text/*"); // Выбор текстового типа файла
            filePickerLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String readFileContent(Uri fileUri) {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = getContentResolver().openInputStream(fileUri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean isFirstLine = true; // Флаг для первой строки
            while ((line = reader.readLine()) != null) {
                if (!isFirstLine) {
                    stringBuilder.append("\n"); // Добавляем "\n" перед каждой строкой, кроме первой
                }
                stringBuilder.append(line);
                isFirstLine = false;
            }
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_import_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.app_bar_settings) {
            Intent intent = new Intent(AddAndChangeFairyTalesActivity.this, SettingsActivity.class);
            intent.putExtra("class", "AddAndChangeFairyTalesActivity");
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.app_bar_content_paste) {
            try {
                pickFile();
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            return true;
        } else {
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