package com.beast.bkara.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;
import com.beast.bkara.R;
import com.beast.bkara.util.imgur.ImageUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 *
 *
 * <p>HOW TO USE:</p>
 *
 * <p>
 * 1/ @onCreate
 * mImageView = (CircularImageView) v.findViewById(R.id.sign_up_avatar);
 * mImageCaptureHandler = new ImageCaptureHandler(getActivity(),mImageView);
 * mImageCaptureHandler.setAlbumName(getString(R.string.bkara_album_avatar));
 * </p>
 *
 * <p>
 * 2/ @onClick
 * Intent takePictureIntent = mImageCaptureHandler.dispatchTakePictureIntent(actionCode);
 * startActivityForResult(takePictureIntent, actionCode);
 * </p>
 *
 * <ul>
 * 3/ Override those methods and call inside them mImageCaptureHandler.XXX() respectively
 * <li>3.1/ @onActivityResult</li>
 * <li>3.2/ @onSaveInstanceState</li>
 * <li>3.3/ @onViewStateRestored</li>
 * </ul>
 *
 * @author  VINH 4/16/2016
 * @see AlbumStorageDirFactory
 * @see BaseAlbumDirFactory
 * @see FroyoAlbumDirFactory
 * @see ImageUtil
 */
public class ImageCaptureHandler {

    public static final int ACTION_TAKE_PHOTO_B = 1;
    public static final int ACTION_TAKE_PHOTO_S = 2;
    public static final int ACTION_TAKE_VIDEO = 3;

    public static final String BITMAP_STORAGE_KEY = "viewbitmap";
    public static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
    private ImageView mImageView;
    private Bitmap mImageBitmap;

    public static final String VIDEO_STORAGE_KEY = "viewvideo";
    public static final String VIDEOVIEW_VISIBILITY_STORAGE_KEY = "videoviewvisibility";
    private VideoView mVideoView;
    private Uri mVideoUri;


    private String mCurrentPhotoPath;

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

    private String albumName;
    private Activity mContext;

    private boolean isAlreadyCaptured = false;

    public boolean isAlreadyCaptured() {
        return isAlreadyCaptured;
    }

    public String getmCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    public ImageCaptureHandler(Activity context, ImageView imageView) {
        this.mContext = context;
        this.mImageView = imageView;

        // Create AlbumFactory based on Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }

        // Default album name
        setAlbumName(mContext.getString(R.string.bkara_album_avatar));
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(mContext.getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap
		* When you loaded a bitmap image from gallery , it may be rotated in ImageView
		* Use this util below to load it correctly
		* */
        mImageBitmap = ImageUtil.loadBitmapFromGallery(mCurrentPhotoPath, bmOptions);

		/* Associate the Bitmap to the ImageView */
        mImageView.setImageBitmap(mImageBitmap);
        //mVideoUri = null;
        mImageView.setVisibility(View.VISIBLE);
        //mVideoView.setVisibility(View.INVISIBLE);
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        mContext.sendBroadcast(mediaScanIntent);
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    /* Photo album for this application */
    public String getAlbumName() {
        return this.albumName;
    }

    public Intent dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        switch(actionCode) {
            case ACTION_TAKE_PHOTO_B:
                File f = null;

                try {
                    f = setUpPhotoFile();
                    mCurrentPhotoPath = f.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                } catch (IOException e) {
                    e.printStackTrace();
                    f = null;
                    mCurrentPhotoPath = null;
                }
                break;

            default:
                break;
        } // switch

        return takePictureIntent;
    }

    public void handleSmallCameraPhoto(Intent intent) {
        Bundle extras = intent.getExtras();
        mImageBitmap = (Bitmap) extras.get("data");
        mImageView.setImageBitmap(mImageBitmap);
        //mVideoUri = null;
        mImageView.setVisibility(View.VISIBLE);
        //mVideoView.setVisibility(View.INVISIBLE);
        isAlreadyCaptured = true;
    }

    public void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            setPic();
            galleryAddPic();
            //mCurrentPhotoPath = null;
            isAlreadyCaptured = true;
        }

    }

    public void handleCameraVideo(Intent intent) {
        mVideoUri = intent.getData();
        //mVideoView.setVideoURI(mVideoUri);
        mImageBitmap = null;
        //mVideoView.setVisibility(View.VISIBLE);
        mImageView.setVisibility(View.INVISIBLE);
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
        //outState.putParcelable(VIDEO_STORAGE_KEY, mVideoUri);
        outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null) );
        //outState.putBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY, (mVideoUri != null) );
    }

    public void onViewStateRestored(Bundle savedInstanceState) {
        if( savedInstanceState == null )
            return;
        mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);
        //mVideoUri = savedInstanceState.getParcelable(VIDEO_STORAGE_KEY);
        if( mImageBitmap != null )
            mImageView.setImageBitmap(mImageBitmap);
        /*mImageView.setVisibility(
                savedInstanceState.getBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY) ?
                        ImageView.VISIBLE : ImageView.INVISIBLE
        );*/
        //mVideoView.setVideoURI(mVideoUri);
        /*mVideoView.setVisibility(
                savedInstanceState.getBoolean(VIDEOVIEW_VISIBILITY_STORAGE_KEY) ?
                        ImageView.VISIBLE : ImageView.INVISIBLE
        ); */
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTION_TAKE_PHOTO_B: {
                if (resultCode == mContext.RESULT_OK) {
                    handleBigCameraPhoto();
                }
                break;
            } // ACTION_TAKE_PHOTO_B

            case ACTION_TAKE_PHOTO_S: {
                if (resultCode == mContext.RESULT_OK) {
                    handleSmallCameraPhoto(data);
                }
                break;
            } // ACTION_TAKE_PHOTO_S

            case ACTION_TAKE_VIDEO: {
                if (resultCode == mContext.RESULT_OK) {
                    handleCameraVideo(data);
                }
                break;
            } // ACTION_TAKE_VIDEO
        } // switch
    }


}
