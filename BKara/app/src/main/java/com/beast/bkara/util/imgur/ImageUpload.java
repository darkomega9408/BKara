package com.beast.bkara.util.imgur;

import java.io.File;

/**
 * Created by VINH on 4/21/2016.
 */
public class ImageUpload {

    private File image;
    private String title;
    private String description;
    private String albumId;

    public ImageUpload(){
        
    }

    public ImageUpload(File image){
        this.image = image;
        this.title = "Title";
        this.description = "Description";
    }

    public ImageUpload(File image, String title, String description, String albumId) {
        this.image = image;
        this.title = title;
        this.description = description;
        this.albumId = albumId;
    }

    public File getImage() {
        return image;
    }

    public void setImage(File image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }
}
