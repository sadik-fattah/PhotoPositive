package com.guercifzone.photorecovrie;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.guercifzone.photorecovrie.Classes.ImageProcessing;
import com.guercifzone.photorecovrie.Classes.ImageSaver;

import java.io.FileNotFoundException;

public class Home extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private ImageView imageView;
    private Button loadImageBtn, invertImageBtn,saveImageBtn ,refreshBtn;
    private ProgressBar progressBar;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        imageView = findViewById(R.id.imageView);
        loadImageBtn = findViewById(R.id.loadImageBtn);
        invertImageBtn = findViewById(R.id.invertImageBtn);
        saveImageBtn =  findViewById(R.id.saveImageBtn);
        progressBar = findViewById(R.id.progressBar);
        refreshBtn = findViewById(R.id.refreshBtn);

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reload the main activity
                imageView.setImageDrawable(null);
                invertImageBtn.setVisibility(View.INVISIBLE);
                saveImageBtn.setVisibility(View.INVISIBLE);
                loadImageBtn.setEnabled(true);
                refreshBtn.setVisibility(View.INVISIBLE);
            }
        });
        loadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                invertImageBtn.setVisibility(View.VISIBLE);


            }
        });
        invertImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the ProgressBar
                progressBar.setVisibility(View.VISIBLE);
                 loadImageBtn.setEnabled(false);
                // Check if there's a drawable in the imageView
                if (imageView.getDrawable() != null) {
                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

                    // Perform the image inversion in a separate thread to avoid blocking the UI
                    new AsyncTask<Bitmap, Void, Bitmap>() {
                        @Override
                        protected Bitmap doInBackground(Bitmap... params) {
                            // Invert the image in the background thread
                            return ImageProcessing.invertImage(params[0]);
                        }

                        @Override
                        protected void onPostExecute(Bitmap invertedBitmap) {
                            // Hide the ProgressBar once the image is processed
                            progressBar.setVisibility(View.GONE);
                            saveImageBtn.setVisibility(View.VISIBLE);
                            invertImageBtn.setEnabled(false);
                            // Set the inverted image on the imageView
                            imageView.setImageBitmap(invertedBitmap);
                        }
                    }.execute(bitmap);
                }
            }
        });
        saveImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshBtn.setVisibility(View.VISIBLE);
                saveImageBtn.setVisibility(View.INVISIBLE);
            /*   if (ContextCompat.checkSelfPermission(Home.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Home.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);
                } else {
                    // Permission already granted, you can call the save function here
                    ImageSaver.SaveImageToGallery(Home.this, imageView);
                    Toast.makeText(getApplicationContext(), "Image saved successfully!", Toast.LENGTH_SHORT).show();
                }*/
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    if (ContextCompat.checkSelfPermission(Home.this, android.Manifest.permission.READ_MEDIA_IMAGES)
            != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(Home.this,
                new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                1);
    }
}
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ImageSaver.SaveImageToGallery(Home.this, imageView);
                Toast.makeText(getApplicationContext(), "Image saved successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
                imageView.setImageBitmap(bitmap);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }
    }
}