package com.example.sal.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemCourses implements Parcelable {
    private String curs;
    private String opisanie;
    private String id;
    private boolean isRead;
    private boolean isTestPassed;

    public ItemCourses() {
        // пустой конструктор без аргументов
    }

    public ItemCourses(String id, String curs, String opisanie) {
        this.id = id;
        this.curs = curs;
        this.opisanie = opisanie;
    }

    public String getCurs() {
        return curs;
    }

    public String getOpisanie() {
        return opisanie;
    }

    public String getId() {
        return id;
    }

    public void setCurs(String curs) {
        this.curs = curs;
    }

    public void setOpisanie(String opisanie) {
        this.opisanie = opisanie;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(curs);
        dest.writeString(opisanie);
        dest.writeString(id);
        dest.writeByte((byte) (isTestPassed ? 1 : 0));
        dest.writeByte((byte) (isRead ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private ItemCourses(Parcel in) {
        id = in.readString();
        curs = in.readString();
        opisanie = in.readString();
        isTestPassed = in.readByte() != 0;
        isRead = in.readByte() != 0;
    }

    public static final Creator<ItemCourses> CREATOR = new Creator<ItemCourses>() {
        @Override
        public ItemCourses createFromParcel(Parcel in) {
            return new ItemCourses(in);
        }

        @Override
        public ItemCourses[] newArray(int size) {
            return new ItemCourses[size];
        }
    };

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isTestPassed() {
        return isTestPassed;
    }

    public void setTestPassed(boolean testPassed) {
        isTestPassed = testPassed;
    }
}
