package com.example.fairytales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.DialogInterface;
import android.database.SQLException;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.View;
import android.widget.AdapterView;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.ImageView;


import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    ListView fairyTalesList;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor taleCursor;
    CustomCursorAdapter taleAdapter;
    int idFT;
    Cursor cursorLV_menu;
    Cursor cursorNameFT;
    ConstraintLayout view;
    String nameFairyTale;
    ImageView imageView;
    public void showDialog (){
        // Создаем AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        MainActivity.this.cursorNameFT = MainActivity.this.db.rawQuery("SELECT " + DatabaseHelper.COLUMN_NAME
                + " FROM " + DatabaseHelper.TABLE + " WHERE " + DatabaseHelper.COLUMN_ID + " = ?", new String[] {String.valueOf(MainActivity.this.idFT)});
        cursorNameFT.moveToFirst();
        String name = cursorNameFT.getString(cursorNameFT.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
        builder.setTitle("Удаление"); // Заголовок
        builder.setMessage("Вы уверены, что хотите удалить сказку '" + name + "'?"); // Текст сообщения
        builder.setIcon(R.drawable.ic_action_delete); // Иконка

        // Кнопка "Подтвердить"
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                MainActivity.this.db.execSQL("DELETE FROM " + DatabaseHelper.TABLE
                        + " WHERE " + DatabaseHelper.COLUMN_ID + " = " + MainActivity.this.idFT + ";");
                getData_updateListView();
                dialog.cancel();
            }
        });
        // Кнопка "Отменить"
        builder.setNegativeButton(R.string.cansel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        // Показываем диалог
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applyTheme(this); // Применяем тему и размер текста
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageStorageHelper storageHelper = new ImageStorageHelper(this);

        // Простая проверка - если папка пуста, копируем файлы
        if (!storageHelper.hasImagesInStorage()) {
            storageHelper.copyPreloadedImages();
        }

        view = (ConstraintLayout) findViewById(R.id.main_layout_id);

        // получаем элемент ListView
        fairyTalesList = findViewById(R.id.fairyTalesList);
        registerForContextMenu(fairyTalesList);
        fairyTalesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), FairyTalesActivity.class);
                intent.putExtra("id", id);
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
            getData_updateListView();
        } catch (SQLException ex) {
            Toast toast = Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }

    }
    public void getData_updateListView(){
        try {
            imageView = findViewById(R.id.imageFairyTale_list);
            //получаем данные из бд в виде курсора
            taleCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE, null);
            // определяем, какие столбцы из курсора будут выводиться в ListView
            String[] headers = new String[]{DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_AUTHOR};
            // создаем адаптер, передаем в него курсор
            taleAdapter = new CustomCursorAdapter(this, R.layout.activity_list_item,
                    taleCursor, headers, new int[]{R.id.nameFairyTale_list, R.id.authorFairyTale_list}, 0);
            fairyTalesList.setAdapter(taleAdapter);
        } catch (SQLException ex){
            Toast toast = Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    //Получение данных из базы данных
    public String getStoryFromDatabase(int idTale) {
        Cursor cursorForFile;
        cursorForFile = MainActivity.this.db.rawQuery("SELECT " + DatabaseHelper.COLUMN_NAME + ", " + DatabaseHelper.COLUMN_AUTHOR + ", " + DatabaseHelper.COLUMN_TEXT
                + " FROM " + DatabaseHelper.TABLE + " WHERE " + DatabaseHelper.COLUMN_ID + " = ?", new String[] {String.valueOf(idTale)});
        cursorForFile.moveToFirst();
        nameFairyTale = cursorForFile.getString(cursorForFile.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
        String author = cursorForFile.getString(cursorForFile.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AUTHOR));
        String text = cursorForFile.getString(cursorForFile.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TEXT));

        String fairyTaleDB = nameFairyTale + "\n\n" + author + "\n\n" + text; // Формируем текст сказки

        cursorForFile.close();
        return fairyTaleDB;
    }

    //Запись данных в файл
    public void saveStoryToFile(Context context, String storyData, String fileName) {
        try {
            // Открываем файл для записи во внутреннем хранилище
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            try {
                fos.write(storyData.getBytes());
            } catch (IOException e) {
                Toast.makeText(this, "Write: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            fos.close();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    //Передача файла другим приложениям
    public void shareStoryFile(Context context, String fileName) {
        try {
            // Получаем URI файла
            File file = new File(context.getFilesDir(), fileName);
            Uri fileUri = FileProvider.getUriForFile(
                    context,
                    context.getApplicationContext().getPackageName() + ".fileprovider",
                    file
            );

            // Создаем Intent для отправки файла
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_STREAM, fileUri); // Передаем URI файла
            sendIntent.setType("text/plain");

            // Создаем Sharesheet
            Intent shareIntent = Intent.createChooser(sendIntent, "Поделиться сказкой");
            context.startActivity(shareIntent);
        } catch (Exception ex){
            Toast.makeText(this, ex.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.context_menu_ft, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.app_bar_search) {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.app_bar_add) {
            Intent intent = new Intent(MainActivity.this, AddAndChangeFairyTalesActivity.class);
            intent.putExtra("type", "add");
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.app_bar_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            intent.putExtra("class", "MainActivity");
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        // Получаем информацию о выбранном элементе
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position; // Позиция элемента в списке

        // Получаем курсор по позиции
        MainActivity.this.cursorLV_menu = (Cursor) MainActivity.this.fairyTalesList.getAdapter().getItem(position);

        // Извлекаем ID элемента из курсора
        MainActivity.this.idFT = cursorLV_menu.getInt(cursorLV_menu.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
        if (item.getItemId() == R.id.delete_item) {
            try {
                showDialog();
            }catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            return true;
        } else if (item.getItemId() == R.id.change_item) {
            Intent intent = new Intent(MainActivity.this, AddAndChangeFairyTalesActivity.class);
            intent.putExtra("idUpdate", MainActivity.this.idFT);
            intent.putExtra("type", "change");
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.send_item) {
            saveStoryToFile(this, getStoryFromDatabase(MainActivity.this.idFT),  nameFairyTale + ".txt");
            shareStoryFile(this, nameFairyTale + ".txt");
            return true;
        } else {
            return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Закрываем подключение и курсор
        db.close();
        taleCursor.close();
        cursorLV_menu.close();
        cursorNameFT.close();
    }
}