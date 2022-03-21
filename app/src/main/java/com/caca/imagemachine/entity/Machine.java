package com.caca.imagemachine.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @author caca rusmana on 18/03/22
 */
@Entity
public class Machine implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long machineId;

    private String machineType;

    private String machineName;

    private String machineQrCode;

    private String lastMaintenanceDate;

    public long getMachineId() {
        return machineId;
    }

    public void setMachineId(long machineId) {
        this.machineId = machineId;
    }

    public String getMachineType() {
        return machineType;
    }

    public void setMachineType(String machineType) {
        this.machineType = machineType;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getMachineQrCode() {
        return machineQrCode;
    }

    public void setMachineQrCode(String machineQrCode) {
        this.machineQrCode = machineQrCode;
    }

    public String getLastMaintenanceDate() {
        return lastMaintenanceDate;
    }

    public void setLastMaintenanceDate(String lastMaintenanceDate) {
        this.lastMaintenanceDate = lastMaintenanceDate;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.machineId);
        dest.writeString(this.machineType);
        dest.writeString(this.machineName);
        dest.writeString(this.machineQrCode);
        dest.writeString(this.lastMaintenanceDate);
    }

    public void readFromParcel(Parcel source) {
        this.machineId = source.readLong();
        this.machineType = source.readString();
        this.machineName = source.readString();
        this.machineQrCode = source.readString();
        this.lastMaintenanceDate = source.readString();
    }

    public Machine() {
    }

    protected Machine(Parcel in) {
        this.machineId = in.readLong();
        this.machineType = in.readString();
        this.machineName = in.readString();
        this.machineQrCode = in.readString();
        this.lastMaintenanceDate = in.readString();
    }

    public static final Parcelable.Creator<Machine> CREATOR = new Parcelable.Creator<Machine>() {
        @Override
        public Machine createFromParcel(Parcel source) {
            return new Machine(source);
        }

        @Override
        public Machine[] newArray(int size) {
            return new Machine[size];
        }
    };
}
