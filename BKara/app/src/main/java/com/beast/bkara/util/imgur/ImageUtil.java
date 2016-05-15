package com.beast.bkara.util.imgur;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import java.io.IOException;

/**
 * Created by VINH on 4/16/2016.
 */
public class ImageUtil {
    private static final String TAG = "Image Util";

    /**
     * Get corresponding degree
     *
     * @param exifOrientation
     * @return
     */
    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

    /**
     * Decode the JPEG file into a Bitmap
     * When you loaded a bitmap image from gallery , it may be rotated in ImageView
     * Use this util below to load it correctly
     *
     * @param mCurrFilePath Current path to the photo
     * @param bmOptions Some options you want to set for this photo
     * @return
     */
    public static Bitmap loadBitmapFromGallery(String mCurrFilePath, BitmapFactory.Options bmOptions ) {
        Bitmap mImageBitmap = BitmapFactory.decodeFile(mCurrFilePath, bmOptions);

        try {
            ExifInterface exif = new ExifInterface(mCurrFilePath);
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotationInDegrees = exifToDegrees(rotation);
            Matrix matrix = new Matrix();
            if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}
            mImageBitmap = Bitmap.createBitmap(mImageBitmap,0,0, mImageBitmap.getWidth(), mImageBitmap.getHeight(), matrix, true);

        }catch(IOException ex){
            Log.e(TAG, "Failed to get Exif data", ex);
        }

        return mImageBitmap;
    }
}
