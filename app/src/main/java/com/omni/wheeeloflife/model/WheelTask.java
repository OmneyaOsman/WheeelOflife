package com.omni.wheeeloflife.model;


import android.os.Parcel;
import android.os.Parcelable;

public class WheelTask  implements Parcelable{


    private String key ;
    private  String taskName ;
    private  String note ;
    private  String imageUrl ;
    private  String dueDate ;
    private  String dueTime ;
    private  int reminderNumber;
    private  String reminderUnite;
    private  String createdDate ;
    private  Double addressLat ;
    private  Double addressLon ;
    private String address ;
    private boolean completed ;

    public WheelTask() {
    }

    public WheelTask(String key, String taskName, String note, String imageUrl, String dueDate, String dueTime, int reminderNumber, String reminderUnite, String createdDate, Double addressLat, Double addressLon, String address) {
        this.key = key ;
        this.taskName = taskName;
        this.note = note;
        this.imageUrl = imageUrl;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.reminderNumber = reminderNumber;
        this.reminderUnite = reminderUnite;
        this.createdDate = createdDate;
        this.addressLat = addressLat;
        this.addressLon = addressLon;
        this.address = address;
        this.completed = false;
    }

    protected WheelTask(Parcel in) {
        key = in.readString();
        taskName = in.readString();
        note = in.readString();
        imageUrl = in.readString();
        dueDate = in.readString();
        dueTime = in.readString();
        reminderNumber = in.readInt();
        reminderUnite = in.readString();
        createdDate = in.readString();
        if (in.readByte() == 0) {
            addressLat = null;
        } else {
            addressLat = in.readDouble();
        }
        if (in.readByte() == 0) {
            addressLon = null;
        } else {
            addressLon = in.readDouble();
        }
        address = in.readString();
        completed = in.readByte() != 0;
    }

    public static final Creator<WheelTask> CREATOR = new Creator<WheelTask>() {
        @Override
        public WheelTask createFromParcel(Parcel in) {
            return new WheelTask(in);
        }

        @Override
        public WheelTask[] newArray(int size) {
            return new WheelTask[size];
        }
    };

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public int getReminderNumber() {
        return reminderNumber;
    }

    public void setReminderNumber(int reminderNumber) {
        this.reminderNumber = reminderNumber;
    }

    public String getReminderUnite() {
        return reminderUnite;
    }

    public void setReminderUnite(String reminderUnite) {
        this.reminderUnite = reminderUnite;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public Double getAddressLat() {
        return addressLat;
    }

    public void setAddressLat(Double addressLat) {
        this.addressLat = addressLat;
    }

    public Double getAddressLon() {
        return addressLon;
    }

    public void setAddressLon(Double addressLon) {
        this.addressLon = addressLon;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(key);
        parcel.writeString(taskName);
        parcel.writeString(note);
        parcel.writeString(imageUrl);
        parcel.writeString(dueDate);
        parcel.writeString(dueTime);
        parcel.writeInt(reminderNumber);
        parcel.writeString(reminderUnite);
        parcel.writeString(createdDate);
        if (addressLat == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(addressLat);
        }
        if (addressLon == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(addressLon);
        }
        parcel.writeString(address);
        parcel.writeByte((byte) (completed ? 1 : 0));
    }
}
