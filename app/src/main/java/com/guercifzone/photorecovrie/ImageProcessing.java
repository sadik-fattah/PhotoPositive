package com.guercifzone.photorecovrie;



import android.graphics.Bitmap;
import android.graphics.Color;
public class ImageProcessing {
    // Method to invert the colors of a bitmap
    public static Bitmap invertImage(Bitmap original) {
        // Create a mutable bitmap with the same dimensions
        Bitmap invertedBitmap = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);

        // Loop through each pixel and invert its color
        for (int x = 0; x < original.getWidth(); x++) {
            for (int y = 0; y < original.getHeight(); y++) {
                // Get the color of the current pixel
                int pixelColor = original.getPixel(x, y);

                // Extract the RGB components from the pixel color
                int red = Color.red(pixelColor);
                int green = Color.green(pixelColor);
                int blue = Color.blue(pixelColor);

                // Invert the color components
                int invertedRed = 255 - red;
                int invertedGreen = 255 - green;
                int invertedBlue = 255 - blue;

                // Set the new color to the corresponding pixel in the inverted bitmap
                invertedBitmap.setPixel(x, y, Color.rgb(invertedRed, invertedGreen, invertedBlue));
            }
        }

        return invertedBitmap;
    }
}
