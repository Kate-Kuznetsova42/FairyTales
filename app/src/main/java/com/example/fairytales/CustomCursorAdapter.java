package com.example.fairytales;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class CustomCursorAdapter extends SimpleCursorAdapter {
    private LayoutInflater inflater;

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

        // Получаем данные из курсора
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
        String author = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_AUTHOR));

        // Устанавливаем данные в View
        textTitle.setText(name);
        textSubtitle.setText(author);
    }
}
