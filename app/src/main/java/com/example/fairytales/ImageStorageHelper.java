package com.example.fairytales;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class ImageStorageHelper {
    private static final String IMAGE_DIR = "tale_images";
    private Context context;

    public ImageStorageHelper(Context context) {
        this.context = context.getApplicationContext();
    }

    // Сохранение изображения
    public String saveImage(Bitmap bitmap, String imageName) {
        File imageFile = getImageFile(imageName);
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.WEBP, 100, fos);
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Получение файла изображения
    public File getImageFile(String imageName) {
        File storageDir = new File(context.getFilesDir(), IMAGE_DIR);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        return new File(storageDir, imageName + ".webp");
    }
    // Получение пути изображения
    public String getImageFullPath(String imageName) {
        File imageFile = new File( context.getFilesDir(), IMAGE_DIR + "/" + imageName + ".webp");
        return "file://" + imageFile.getAbsolutePath();
    }

    public void copyPreloadedImages() {
        try {
            // Получаем список файлов из assets/preloaded_images
            String[] files = context.getAssets().list("preloaded_images");
            if (files == null || files.length == 0) return;

            // Копируем каждый файл
            for (String filename : files) {
                InputStream in = context.getAssets().open("preloaded_images/" + filename);
                OutputStream out = Files.newOutputStream(getImageFile(filename.split("\\.")[0]).toPath());

                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }

                in.close();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Проверяет, есть ли уже изображения в папке
     */
    public boolean hasImagesInStorage() {
        File storageDir = getImageFile("").getParentFile();
        if (storageDir == null || !storageDir.exists()) {
            return false;
        }
        String[] files = storageDir.list();
        return files != null && files.length > 0;
    }
}