package com.guercifzone.photorecovrie.Classes;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ImageSaver {
    public static  void SaveImageToGallery(Context context, ImageView imageView){
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            saveImageToMediaStore(context,bitmap);
        }else {
            SaveImageToFileSystem(context,bitmap);
        }
    }

    private static void saveImageToMediaStore(Context context, Bitmap bitmap) {
        try {
            // Create a ContentValues object to describe the image
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "Image_" + System.currentTimeMillis() + ".png");
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ImageSaver");

            // Insert the image into the MediaStore and get a Uri
            Uri imageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

            // Check if Uri is not null
            if (imageUri != null) {
                // Get OutputStream from the Uri
                try (OutputStream outputStream = context.getContentResolver().openOutputStream(imageUri)) {
                    if (outputStream != null) {
                        // Compress and write the bitmap to the output stream
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                        Log.d("ImageSaver", "Image saved successfully!");
                        Toast.makeText(context, "Image saved successfully!", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Log.e("ImageSaver", "Error saving image", e);
                    Toast.makeText(context, "Error saving image to MediaStore", Toast.LENGTH_SHORT).show();
                }
            }

        } catch (Exception e) {
            Log.e("ImageSaver", "Error saving image to MediaStore", e);
            Toast.makeText(context, "Error saving image to MediaStore", Toast.LENGTH_SHORT).show();
        }
    }
    private static void SaveImageToFileSystem(Context context, Bitmap bitmap) {
        try {
            String fileName = "image_" +System.currentTimeMillis() + ".png";
            FileOutputStream outputStream  = context.openFileOutput(fileName,context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
            outputStream.flush();
            outputStream.close();
            Log.d("ImageSaver","Image saved successfully!");
        }catch (IOException e){
            Log.e("ImageSaver", "Error Saving image",e);

        }
    }
}
