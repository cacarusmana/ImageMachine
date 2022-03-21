package com.caca.imagemachine.entity;

import android.graphics.Bitmap;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * @author caca rusmana on 18/03/22
 */
@Entity
public class MachineImage {
    @PrimaryKey(autoGenerate = true)
    private long machineImageId;

    private long machineId;
    private String fileName;

    @Ignore
    private Bitmap bitmap;

    @Ignore
    private boolean isChecked = false;

    @Ignore
    private boolean isNew = true;


    public MachineImage() {
    }

    public MachineImage(String imageName, Bitmap bitmap) {
        this.fileName = imageName;
        this.bitmap = bitmap;
    }

    public long getMachineImageId() {
        return machineImageId;
    }

    public void setMachineImageId(long machineImageId) {
        this.machineImageId = machineImageId;
    }

    public long getMachineId() {
        return machineId;
    }

    public void setMachineId(long machineId) {
        this.machineId = machineId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
