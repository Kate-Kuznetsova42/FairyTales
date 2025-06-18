package com.example.fairytales;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;

public class CustomCursorAdapter extends SimpleCursorAdapter {
    private LayoutInflater inflater;
    private ImageStorageHelper imageStorageHelper;
    public CustomCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Создаем новое представление (view) из нашей разметки
        return inflater.inflate(R.layout.activity_list_item, parent, false);
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Настраиваем отображение данных
        TextView textTitle = view.findViewById(R.id.nameFairyTale_list);
        TextView textSubtitle = view.findViewById(R.id.authorFairyTale_list);
        ImageView imageView = view.findViewById(R.id.imageFairyTale_list);

        // Получаем данные из курсора
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
        String author = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AUTHOR));
        String imageName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IMAGEPATH));
        imageStorageHelper = new ImageStorageHelper(context);
        String imagePath = "file://" + imageStorageHelper.getImageFullPath(imageName);

        // Устанавливаем данные в View
        textTitle.setText(name);
        textSubtitle.setText(author);
        Glide
                .with(context).
                load(Uri.parse(imagePath))
                .error(R.drawable.ic_action_no_picture)
                .into(imageView);
    }
}
